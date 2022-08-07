package com.applifting.task.service;

import com.applifting.task.dto.ResultDTO;
import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.Result;
import com.applifting.task.entity.User;
import com.applifting.task.exception.EndpointDoesNotExistException;
import com.applifting.task.exception.ResultDoesNotExistException;
import com.applifting.task.exception.UserDoesNotExistException;
import com.applifting.task.exception.UserCantModifyEndpointException;
import com.applifting.task.repository.MonitoredEndpointRepository;
import com.applifting.task.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ResultService {
    private final ResultRepository resultRepository;
    private final MonitoredEndpointRepository monitoredEndpointRepository;
    private final ValidationService validationService;
    private final UserService userService;
    private final ScheduledExecutorService scheduler;
    private final HashMap<Long, ScheduledFuture<?>> scheduledTasks;
    private final int NUMBER_OF_RESULTS_TO_PRINT = 10;

    @Autowired
    public ResultService(ResultRepository resultRepository, MonitoredEndpointRepository monitoredEndpointRepository, ValidationService validationService, UserService userService) {
        this.resultRepository = resultRepository;
        this.monitoredEndpointRepository = monitoredEndpointRepository;
        this.validationService = validationService;
        this.userService = userService;
        this.scheduledTasks = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);

        // Starting all predefined endpoints from database
        for (MonitoredEndpoint monitoredEndpoint : monitoredEndpointRepository.findAll()) {
            startMonitoringEndpoint(monitoredEndpoint);
        }
    }

    public ResultDTO findResultAsDTO(String accessToken, Long resultId)
            throws UserCantModifyEndpointException, UserDoesNotExistException, ResultDoesNotExistException {
        User user = userService.getUserByAccessToken(accessToken);
        Optional<Result> optionalResult = resultRepository.findById(resultId);
        if (optionalResult.isEmpty()) {
            throw new ResultDoesNotExistException(resultId);
        }
        Result result = optionalResult.get();
        validationService.checkIfUserIsAllowedToModifyThisEndpointOrThrowException(user, result.getMonitoredEndpoint());
        return toDTO(result);
    }

    public List<ResultDTO> getLastResults(String accessToken, Long endpointId)
            throws UserDoesNotExistException, EndpointDoesNotExistException, UserCantModifyEndpointException {
        User user = userService.getUserByAccessToken(accessToken);
        MonitoredEndpoint monitoredEndpoint =
                validationService.getMonitoredEndpointOrThrowExceptionIfEndpointDoesNotExist(endpointId);
        validationService.checkIfUserIsAllowedToModifyThisEndpointOrThrowException(user, monitoredEndpoint);
        List<Result> results = resultRepository.findAllByMonitoredEndpoint(monitoredEndpoint);
        // Trim list to get <= 10 last results
        results.sort(Comparator.comparing(Result::getDateOfCheck).reversed());
        if (results.size() == 0) {
            return Collections.emptyList();
        }
        if (results.size() > NUMBER_OF_RESULTS_TO_PRINT) {
            results = results.subList(0, NUMBER_OF_RESULTS_TO_PRINT);
        }
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void startMonitoringEndpoint(MonitoredEndpoint monitoredEndpoint) {
        if (monitoredEndpoint.getMonitoredInterval() == 0) {
            return; // we're not starting monitoring if interval was not entered or if it's zero
        }
        Runnable runnable = () -> getTheResultOfMonitoring(monitoredEndpoint);
        ScheduledFuture<?> newTask =
                scheduler.scheduleAtFixedRate(runnable, 0, monitoredEndpoint.getMonitoredInterval(), TimeUnit.SECONDS);
        if (scheduledTasks.containsKey(monitoredEndpoint.getId())) {
            ScheduledFuture<?> oldTask = scheduledTasks.get(monitoredEndpoint.getId());
            oldTask.cancel(false);
        }
        scheduledTasks.put(monitoredEndpoint.getId(), newTask);
    }

    // Stopping monitoring result when we're deleting monitored endpoint
    public void stopMonitoringEndpoint(MonitoredEndpoint monitoredEndpoint) {
        ScheduledFuture<?> task = scheduledTasks.get(monitoredEndpoint.getId());
        task.cancel(false);
        scheduledTasks.remove(monitoredEndpoint.getId());
        deleteAllResultsForThisEndpoint(monitoredEndpoint);
    }

    // When we're deleting endpoint we have to delete its results
    public void deleteAllResultsForThisEndpoint(MonitoredEndpoint monitoredEndpoint) {
        List<Result> resultsOfThisEndpoint = resultRepository.findAllByMonitoredEndpoint(monitoredEndpoint);
        resultRepository.deleteAll(resultsOfThisEndpoint);
    }

    public void getTheResultOfMonitoring(MonitoredEndpoint monitoredEndpoint) {
        LocalDateTime timestamp = LocalDateTime.now();
        monitoredEndpoint.setDateOfLastCheck(timestamp);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(monitoredEndpoint.getUrl(), String.class);
        String httpStatus = String.valueOf(response.getStatusCodeValue());
        String payload = response.getBody();
        Result result = new Result(
                timestamp,
                httpStatus,
                payload,
                monitoredEndpoint
        );
        monitoredEndpointRepository.save(monitoredEndpoint);
        resultRepository.save(result);
    }

    public ResultDTO toDTO(Result result) {
        return new ResultDTO(
                result.getId(),
                result.getDateOfCheck(),
                result.getReturnedHttpStatusCode(),
                result.getReturnedPayload(),
                result.getMonitoredEndpoint()
        );
    }
}
