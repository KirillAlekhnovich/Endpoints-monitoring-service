package com.applifting.task.service;

import com.applifting.task.dto.MonitoredEndpointDTO;
import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.User;
import com.applifting.task.exception.*;
import com.applifting.task.repository.MonitoredEndpointRepository;
import com.applifting.task.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidationService {
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final UserRepository userRepository;

    public ValidationService(MonitoredEndpointRepository monitoredEndpointRepository, UserRepository userRepository) {
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.userRepository = userRepository;
    }

    // Getting User entity by access token
    public User getUserEntityOrThrowExceptionIfUserDoesNotExist(String accessToken) throws UserDoesNotExistException {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByAccessToken(accessToken));
        if (optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with this access token does not exist.");
        }
        return optionalUser.get();
    }

    public MonitoredEndpoint getMonitoredEndpointOrThrowExceptionIfEndpointDoesNotExist(Long id)
            throws EndpointDoesNotExistException {
        Optional<MonitoredEndpoint> optionalId = monitoredEndpointRepository.findById(id);
        if (optionalId.isEmpty()) {
            throw new EndpointDoesNotExistException("Monitored endpoint with id " + id + " does not exist.");
        }
        return optionalId.get();
    }

    // Checks is user created this endpoint
    public void checkIfUserIsAllowedToModifyThisEndpointOrThrowException(User user, MonitoredEndpoint monitoredEndpoint)
            throws UserIsNotAllowedToModifyThisEndpointException {
        if (!user.getId().equals(monitoredEndpoint.getOwner().getId())) {
            throw new UserIsNotAllowedToModifyThisEndpointException("This user is not allowed to modify this endpoint.");
        }
    }

    public void checkIfUserAlreadyHasAnEndpointWithThisURLOrThrowException(User user, String monitoredEndpointURL)
            throws EndpointAlreadyExistsException {
        Optional<MonitoredEndpoint> optionalMonitoredEndpoint
                = Optional.ofNullable(monitoredEndpointRepository.findByOwnerAndUrl(user, monitoredEndpointURL));
        if (optionalMonitoredEndpoint.isPresent()) {
            throw new EndpointAlreadyExistsException("User already monitors an endpoint with this URL (it's id is " +
                    optionalMonitoredEndpoint.get().getId() + ").");
        }
    }

    // User in update method
    public void checkIfRestrictedFieldsHaveNotBeenModifiedOrThrowException
            (MonitoredEndpoint monitoredEndpoint, MonitoredEndpointDTO updatedMonitoredEndpointDTO)
            throws EndpointCanNotBeModifiedException {
        // Checking date of creation
        if (updatedMonitoredEndpointDTO.getDateOfCreation() != null
                && !monitoredEndpoint.getDateOfCreation().equals(updatedMonitoredEndpointDTO.getDateOfCreation())) {
            throw new EndpointCanNotBeModifiedException("Date of monitored endpoint creation can not be modified manually.");
        }
        // Checking date of last check
        if (updatedMonitoredEndpointDTO.getDateOfLastCheck() != null
                && !monitoredEndpoint.getDateOfLastCheck().equals(updatedMonitoredEndpointDTO.getDateOfLastCheck())) {
            throw new EndpointCanNotBeModifiedException("Date of monitored endpoints' last check can not be modified manually.");
        }
        // Checking ownership
        if (updatedMonitoredEndpointDTO.getOwnerId() != null
                && !monitoredEndpoint.getOwner().getId().equals(updatedMonitoredEndpointDTO.getOwnerId())) {
            throw new EndpointCanNotBeModifiedException("You are not allowed to force another user to monitor an endpoint.");
        }
    }

    // Checks during create
    public void checkIfCreatedDTOParametersAreValidOrThrowException(MonitoredEndpointDTO monitoredEndpointDTO)
            throws EndpointCanNotBeModifiedException, InvalidMonitoringIntervalException {
        if (monitoredEndpointDTO.getDateOfCreation() != null) {
            throw new EndpointCanNotBeModifiedException("You are not allowed to set date of creation manually");
        }
        if (monitoredEndpointDTO.getDateOfLastCheck() != null) {
            throw new EndpointCanNotBeModifiedException("You are not allowed to set date of last check manually");
        }
        if (monitoredEndpointDTO.getMonitoredInterval() < 0) {
            throw new InvalidMonitoringIntervalException("Entered monitoring interval is not valid. It can't be below zero.");
        }
    }

    // Checks during update
    public void checkIfUpdatedDTOParametersAreValidOrThrowException(MonitoredEndpoint monitoredEndpoint,
                                                                    MonitoredEndpointDTO updatedEndpointDTO)
            throws EndpointCanNotBeModifiedException, InvalidMonitoringIntervalException {
        checkIfRestrictedFieldsHaveNotBeenModifiedOrThrowException(monitoredEndpoint, updatedEndpointDTO);
        // If new value was not entered keep previous values
        if (updatedEndpointDTO.getName() == null) {
            updatedEndpointDTO.setName(monitoredEndpoint.getName());
        }
        if (updatedEndpointDTO.getUrl() == null) {
            updatedEndpointDTO.setUrl(monitoredEndpoint.getUrl());
        }
        if (updatedEndpointDTO.getMonitoredInterval() == null) {
            updatedEndpointDTO.setMonitoredInterval(monitoredEndpoint.getMonitoredInterval());
        }
        if (updatedEndpointDTO.getMonitoredInterval() < 0) {
            throw new InvalidMonitoringIntervalException("Entered monitoring interval is not valid. It can't be below zero.");
        }
    }
}