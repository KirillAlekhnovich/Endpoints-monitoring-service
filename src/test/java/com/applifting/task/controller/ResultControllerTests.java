package com.applifting.task.controller;

import com.applifting.task.dto.ResultDTO;
import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.service.ResultService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultController.class)
public class ResultControllerTests {

    @MockBean
    ResultService resultService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testGetResult() throws Exception {
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint();
        monitoredEndpoint.setId(1L);
        ResultDTO resultDTO = new ResultDTO(2L, null, "200", "Payload", monitoredEndpoint);
        Mockito.when(resultService.findResultAsDTO("123", 1L)).thenReturn(resultDTO);
        mockMvc.perform(get("/results/1").header("Access-token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedHttpStatusCode", Matchers.is("200")))
                .andExpect(jsonPath("$.returnedPayload", Matchers.is("Payload")));
    }

    @Test
    public void testGetLastResults() throws Exception {
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint();
        monitoredEndpoint.setId(1L);
        ResultDTO resultDTO = new ResultDTO(2L, null, "200", "Payload", monitoredEndpoint);
        ResultDTO resultDTO2 = new ResultDTO(3L, null, "301", "Redirect", monitoredEndpoint);

        List<ResultDTO> players = List.of(resultDTO, resultDTO2);

        Mockito.when(resultService.getLastResults("123", 1L)).thenReturn(players);

        mockMvc.perform(get("/results/last_endpoint_results/1").header("Access-token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))

                .andExpect(jsonPath("$[0].returnedHttpStatusCode", Matchers.is("200")))
                .andExpect(jsonPath("$[0].returnedPayload", Matchers.is("Payload")))

                .andExpect(jsonPath("$[1].returnedHttpStatusCode", Matchers.is("301")))
                .andExpect(jsonPath("$[1].returnedPayload", Matchers.is("Redirect")));

    }

}
