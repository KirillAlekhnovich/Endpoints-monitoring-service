package com.applifting.task.controller;

import com.applifting.task.dto.MonitoredEndpointDTO;
import com.applifting.task.exception.*;
import com.applifting.task.service.MonitoredEndpointService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MonitoredEndpointController.class)
public class MonitoredEndpointControllerTests {

    @MockBean
    MonitoredEndpointService monitoredEndpointService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateEndpoint() throws Exception {
        // Testing if we create endpoints correctly
        MonitoredEndpointDTO monitoredEndpointDTO = new MonitoredEndpointDTO(2L, "Google", "https://google.com", null, null, 0, 1L);
        Mockito.when(monitoredEndpointService.createMonitoredEndpoint(any(String.class), any(MonitoredEndpointDTO.class))).thenReturn(monitoredEndpointDTO);
        mockMvc.perform(post("/endpoints")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is("Google")))
                .andExpect(jsonPath("$.url", Matchers.is("https://google.com")))
                .andExpect(jsonPath("$.monitoredInterval", Matchers.is(0)));

        // Testing if access token belongs to someone
        Mockito.when(monitoredEndpointService.createMonitoredEndpoint(any(String.class), any(MonitoredEndpointDTO.class))).thenThrow(UserDoesNotExistException.class);
        mockMvc.perform(post("/endpoints")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isNotFound());

        // Testing if we're not trying to create endpoint with same url
        Mockito.when(monitoredEndpointService.createMonitoredEndpoint(any(String.class), any(MonitoredEndpointDTO.class))).thenThrow(EndpointAlreadyExistsException.class);
        mockMvc.perform(post("/endpoints")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isConflict());

        // Testing if user has tried to enter date of creation or date of last check
        Mockito.when(monitoredEndpointService.createMonitoredEndpoint(any(String.class), any(MonitoredEndpointDTO.class))).thenThrow(EndpointCanNotBeModifiedException.class);
        mockMvc.perform(post("/endpoints")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isForbidden());

        // Testing if monitoring interval is valid
        Mockito.when(monitoredEndpointService.createMonitoredEndpoint(any(String.class), any(MonitoredEndpointDTO.class))).thenThrow(InvalidMonitoringIntervalException.class);
        mockMvc.perform(post("/endpoints")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testGetUserEndpoints() throws Exception {
        MonitoredEndpointDTO userEndpoint1 = new MonitoredEndpointDTO(1L, "Google", "https://google.com", null, null, 0, 1L);
        MonitoredEndpointDTO userEndpoint2 = new MonitoredEndpointDTO(2L, "YouTube", "https://youtube.com", null, null, 10, 1L);
        List<MonitoredEndpointDTO> userEndpoints = List.of(userEndpoint1, userEndpoint2);

        Mockito.when(monitoredEndpointService.findAllUserEndpoints("123")).thenReturn(userEndpoints);

        mockMvc.perform(get("/endpoints").header("Access-token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))

                .andExpect(jsonPath("$[0].name", Matchers.is("Google")))
                .andExpect(jsonPath("$[0].url", Matchers.is("https://google.com")))
                .andExpect(jsonPath("$[0].monitoredInterval", Matchers.is(0)))
                .andExpect(jsonPath("$[0].ownerId", Matchers.is(1)))

                .andExpect(jsonPath("$[1].name", Matchers.is("YouTube")))
                .andExpect(jsonPath("$[1].url", Matchers.is("https://youtube.com")))
                .andExpect(jsonPath("$[1].monitoredInterval", Matchers.is(10)))
                .andExpect(jsonPath("$[1].ownerId", Matchers.is(1)));
    }

    @Test
    public void testUpdateEndpoint() throws Exception {
        // Testing if we create endpoints correctly
        MonitoredEndpointDTO updatedDTO = new MonitoredEndpointDTO(1L, "Google", "https://google.com", null, null, 0, 1L);
        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenReturn(updatedDTO);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(jsonPath("$.name", Matchers.is("Google")))
                .andExpect(jsonPath("$.url", Matchers.is("https://google.com")))
                .andExpect(jsonPath("$.monitoredInterval", Matchers.is(0)));

        // Testing if access token belongs to someone
        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenThrow(UserDoesNotExistException.class);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isNotFound());

        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenThrow(EndpointDoesNotExistException.class);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isNotFound());

        // Testing if user has tried to enter date of creation or date of last check
        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenThrow(UserIsNotAllowedToModifyThisEndpointException.class);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isForbidden());

        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenThrow(EndpointCanNotBeModifiedException.class);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isForbidden());

        // Testing if monitoring interval is valid
        Mockito.when(monitoredEndpointService.updateById(any(String.class), any(Long.class), any(MonitoredEndpointDTO.class))).thenThrow(InvalidMonitoringIntervalException.class);
        mockMvc.perform(put("/endpoints/1")
                        .header("Access-token", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Google\",\"url\":\"https://google.com\",\"monitoredInterval\":0}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testDeleteEndpoint() throws Exception {
        mockMvc.perform(delete("/endpoints/1").header("Access-token", "123"))
                .andExpect(status().isOk());
    }
}
