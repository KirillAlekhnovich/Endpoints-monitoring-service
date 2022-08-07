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
            throw new TokenIsMissingException();
        }
        MonitoredEndpointDTO createdEndpoint = monitoredEndpointService.createMonitoredEndpoint(accessToken, monitoredEndpointDTO);
        return ResponseEntity
                .created(Link.of("http://localhost:8080/endpoints/" + createdEndpoint.getId()).toUri())
                .body(createdEndpoint);
    }

    @GetMapping()
    public ResponseEntity getAllUserEndpoints(@RequestHeader(value = "access-token", required = false) String accessToken) {
        if (accessToken == null) {
            throw new TokenIsMissingException();
        }
        return ResponseEntity.ok(monitoredEndpointService.findAllUserEndpoints(accessToken));
    }

    @PutMapping("/{endpointId}")
    public ResponseEntity<MonitoredEndpointDTO> update(@RequestHeader(value = "access-token", required = false) String accessToken, @PathVariable Long endpointId,
                                                       @RequestBody MonitoredEndpointDTO updatedMonitoredEndpointDTO) {
        if (accessToken == null) {
            throw new TokenIsMissingException();
        }
        MonitoredEndpointDTO updatedEndpoint = monitoredEndpointService.updateById(accessToken, endpointId, updatedMonitoredEndpointDTO);
        return ResponseEntity
                .created(Link.of("http://localhost:8080/endpoints/" + updatedEndpoint.getId()).toUri())
                .body(updatedEndpoint);
    }

    @DeleteMapping("/{endpointId}")
    public ResponseEntity delete(@RequestHeader(value = "access-token", required = false) String accessToken,
                                 @PathVariable Long endpointId) {
        if (accessToken == null) {
            throw new TokenIsMissingException();
        }
        monitoredEndpointService.deleteMonitoredEndpointById(accessToken, endpointId);
        return ResponseEntity.ok().body("Monitored endpoint with id " + endpointId + " was successfully deleted.");
    }
}
