package com.applifting.task.service;

import com.applifting.task.dto.ResultDTO;
import com.applifting.task.entity.Result;
import com.applifting.task.repository.MonitoredEndpointRepository;
import com.applifting.task.repository.ResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTests {

    @InjectMocks
    ResultService resultService;

    @Mock
    ResultRepository resultRepository;

    @Mock
    MonitoredEndpointRepository monitoredEndpointRepository;

    @Test
    public void testGetLastResults() {
        Result result1 = new Result(null, "200", "OK", null);
        Result result2 = new Result(null, "404", "Not Found", null);
        List<Result> results = List.of(result1, result2);
        Mockito.when(resultRepository.findAll()).thenReturn(results);
        List<ResultDTO> returnedPlayers = resultRepository.findAll().stream().map(resultService::toDTO).toList();

        assertEquals(2, returnedPlayers.size());
        verify(resultRepository, times(1)).findAll();
    }
}
