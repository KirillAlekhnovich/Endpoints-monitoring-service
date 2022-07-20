package com.applifting.task.controller;

import com.applifting.task.dto.MonitoredEndpointDTO;
import com.applifting.task.exception.*;
import com.applifting.task.service.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/endpoints")
public class MonitoredEndpointController {
    public final MonitoredEndpointService monitoredEndpointService;

    @Autowired
    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService) {
        this.monitoredEndpointService = monitoredEndpointService;
    }

    @PostMapping
    public ResponseEntity<MonitoredEndpointDTO> create(@RequestHeader(value = "access-token", required = false) String accessToken,
                                                       @RequestBody MonitoredEndpointDTO monitoredEndpointDTO) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            MonitoredEndpointDTO createdEndpoint =
                    monitoredEndpointService.createMonitoredEndpoint(accessToken, monitoredEndpointDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/endpoints/" + createdEndpoint.getId()).toUri())
                    .body(createdEndpoint);
        } catch (UserDoesNotExistException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EndpointAlreadyExistsException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EndpointCanNotBeModifiedException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (InvalidMonitoringIntervalException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity getAllUserEndpoints(@RequestHeader(value = "access-token", required = false) String accessToken) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            return ResponseEntity.ok(monitoredEndpointService.findAllUserEndpoints(accessToken));
        } catch (UserDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserHasNoMonitoredEndpointsException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{endpointId}")
    public ResponseEntity<MonitoredEndpointDTO> update(@RequestHeader(value = "access-token", required = false) String accessToken, @PathVariable Long endpointId,
                                                       @RequestBody MonitoredEndpointDTO updatedMonitoredEndpointDTO) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            MonitoredEndpointDTO updatedEndpoint =
                    monitoredEndpointService.updateById(accessToken, endpointId, updatedMonitoredEndpointDTO);
            return ResponseEntity
                    .created(Link.of("http://localhost:8080/endpoints/" + updatedEndpoint.getId()).toUri())
                    .body(updatedEndpoint);
        } catch (UserDoesNotExistException | EndpointDoesNotExistException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserIsNotAllowedToModifyThisEndpointException | EndpointCanNotBeModifiedException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (InvalidMonitoringIntervalException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{endpointId}")
    public ResponseEntity delete(@RequestHeader(value = "access-token", required = false) String accessToken,
                                 @PathVariable Long endpointId) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            monitoredEndpointService.deleteMonitoredEndpointById(accessToken, endpointId);
            return ResponseEntity.ok().body("Monitored endpoint with id " + endpointId + " was successfully deleted.");
        } catch (UserDoesNotExistException | EndpointDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserIsNotAllowedToModifyThisEndpointException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
