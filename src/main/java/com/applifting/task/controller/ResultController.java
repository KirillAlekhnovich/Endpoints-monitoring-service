package com.applifting.task.controller;

import com.applifting.task.dto.ResultDTO;
import com.applifting.task.exception.*;
import com.applifting.task.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/results")
public class ResultController {
    public final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ResultDTO> get(@RequestHeader(value = "access-token", required = false) String accessToken,
                                         @PathVariable Long resultId) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            ResultDTO optionalResult = resultService.findResultAsDTO(accessToken, resultId);
            return ResponseEntity.ok(optionalResult);
        } catch (UserDoesNotExistException | ResultDoesNotExistException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserIsNotAllowedToModifyThisEndpointException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/last_endpoint_results/{endpointId}")
    public ResponseEntity getLastResults(@RequestHeader(value = "access-token", required = false) String accessToken,
                                         @PathVariable Long endpointId) {
        if (accessToken == null) {
            return new ResponseEntity("Access token was not found in header.", HttpStatus.NOT_FOUND);
        }
        try {
            return ResponseEntity.ok(resultService.getLastResults(accessToken, endpointId));
        } catch (UserDoesNotExistException | EndpointDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserIsNotAllowedToModifyThisEndpointException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
