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
            throw new TokenIsMissingException();
        }
        ResultDTO optionalResult = resultService.findResultAsDTO(accessToken, resultId);
        return ResponseEntity.ok(optionalResult);
    }

    @GetMapping("/last_endpoint_results/{endpointId}")
    public ResponseEntity getLastResults(@RequestHeader(value = "access-token", required = false) String accessToken,
                                         @PathVariable Long endpointId) {
        if (accessToken == null) {
            throw new TokenIsMissingException();
        }
        return ResponseEntity.ok(resultService.getLastResults(accessToken, endpointId));
    }
}
