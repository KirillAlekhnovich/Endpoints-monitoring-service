package com.applifting.task.service;

import com.applifting.task.dto.MonitoredEndpointDTO;
import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.User;
import com.applifting.task.exception.*;
import com.applifting.task.repository.MonitoredEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitoredEndpointService {
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final ResultService resultService;
    private final ValidationService validationService;
    private final UserService userService;

    @Autowired
    public MonitoredEndpointService(MonitoredEndpointRepository monitoredEndpointRepository, ResultService resultService, ValidationService validationService, UserService userService) {
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.resultService = resultService;
        this.validationService = validationService;
        this.userService = userService;
    }

    public MonitoredEndpointDTO createMonitoredEndpoint(String accessToken, MonitoredEndpointDTO monitoredEndpointDTO)
            throws UserDoesNotExistException, EndpointAlreadyExistsException, EndpointCanNotBeModifiedException, InvalidMonitoringIntervalException {
        User loggedUser = userService.getUserByAccessToken(accessToken);
        validationService.checkIfUserMonitorsThisURL(loggedUser, monitoredEndpointDTO.getUrl());
        // Avoiding nullptr exception
        if (monitoredEndpointDTO.getMonitoredInterval() == null) {
            monitoredEndpointDTO.setMonitoredInterval(0);
        }
        validationService.checkEndpointCreatingParameters(monitoredEndpointDTO);
        monitoredEndpointDTO.setDateOfCreation(LocalDateTime.now());
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint(
                monitoredEndpointDTO.getName(),
                monitoredEndpointDTO.getUrl(),
                monitoredEndpointDTO.getDateOfCreation(),
                monitoredEndpointDTO.getDateOfLastCheck(),
                monitoredEndpointDTO.getMonitoredInterval(),
                loggedUser
        );
        monitoredEndpointRepository.save(monitoredEndpoint);
        resultService.startMonitoringEndpoint(monitoredEndpoint);
        return toDTO(monitoredEndpoint);
    }

    public List<MonitoredEndpointDTO> findAllUserEndpoints(String accessToken)
            throws UserDoesNotExistException, UserHasNoEndpointsException {
        User loggedUser = userService.getUserByAccessToken(accessToken);
        List<MonitoredEndpointDTO> userEndpoints =
                monitoredEndpointRepository.findAllByOwner(loggedUser).stream().map(this::toDTO).toList();
        if (userEndpoints.isEmpty()) {
            throw new UserHasNoEndpointsException();
        }
        return userEndpoints;
    }

    public MonitoredEndpointDTO updateById(String accessToken, Long endpointId, MonitoredEndpointDTO updatedMonitoredEndpointDTO)
            throws UserDoesNotExistException, EndpointDoesNotExistException, UserCantModifyEndpointException,
            EndpointCanNotBeModifiedException, InvalidMonitoringIntervalException {
        User loggedUser = userService.getUserByAccessToken(accessToken);
        MonitoredEndpoint monitoredEndpoint = validationService.getMonitoredEndpoint(endpointId);
        return updateMonitoredEndpoint(loggedUser, monitoredEndpoint, updatedMonitoredEndpointDTO);
    }

    /**
     * This method is separate in order to allow us to update endpoints by different parameters.
     * In this case it's not really necessary, because only updating by id makes sense in our implementation.
     */
    public MonitoredEndpointDTO updateMonitoredEndpoint(User loggedUser, MonitoredEndpoint monitoredEndpoint,
                                                        MonitoredEndpointDTO updatedMonitoredEndpointDTO)
            throws UserCantModifyEndpointException, EndpointCanNotBeModifiedException,
            InvalidMonitoringIntervalException {
        validationService.checkIfUserCanModifyThisEndpoint(loggedUser, monitoredEndpoint);
        validationService.checkEndpointUpdatingParameters(monitoredEndpoint, updatedMonitoredEndpointDTO);
        // Only following parameters are allowed to be updated
        monitoredEndpoint.updateParameters(
                updatedMonitoredEndpointDTO.getName(),
                updatedMonitoredEndpointDTO.getUrl(),
                updatedMonitoredEndpointDTO.getMonitoredInterval());
        monitoredEndpointRepository.save(monitoredEndpoint);
        resultService.startMonitoringEndpoint(monitoredEndpoint);
        return toDTO(monitoredEndpoint);
    }

    public void deleteMonitoredEndpointById(String accessToken, Long id)
            throws EndpointDoesNotExistException, UserDoesNotExistException, UserCantModifyEndpointException {
        User loggedUser = userService.getUserByAccessToken(accessToken);
        MonitoredEndpoint monitoredEndpoint = validationService.getMonitoredEndpoint(id);
        validationService.checkIfUserCanModifyThisEndpoint(loggedUser, monitoredEndpoint);
        resultService.stopMonitoringEndpoint(monitoredEndpoint);
        monitoredEndpointRepository.deleteById(id);
    }

    public MonitoredEndpointDTO toDTO(MonitoredEndpoint monitoredEndpoint) {
        return new MonitoredEndpointDTO(
                monitoredEndpoint.getId(),
                monitoredEndpoint.getName(),
                monitoredEndpoint.getUrl(),
                monitoredEndpoint.getDateOfCreation(),
                monitoredEndpoint.getDateOfLastCheck(),
                monitoredEndpoint.getMonitoredInterval(),
                monitoredEndpoint.getOwner().getId()
        );
    }
}
