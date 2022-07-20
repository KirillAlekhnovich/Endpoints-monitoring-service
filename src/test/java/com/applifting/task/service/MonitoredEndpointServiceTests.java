package com.applifting.task.service;

import com.applifting.task.dto.MonitoredEndpointDTO;
import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.User;
import com.applifting.task.repository.MonitoredEndpointRepository;
import com.applifting.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MonitoredEndpointServiceTests {

    @InjectMocks
    MonitoredEndpointService monitoredEndpointService;

    @Mock
    ValidationService validationService;

    @Mock
    ResultService resultService;

    @Mock
    MonitoredEndpointRepository monitoredEndpointRepository;

    @Mock
    UserRepository userRepository;

    @Test
    public void testCreateEndpoint() throws Exception {
        User user = new User(1L, "Batman", "batman@example.com", "123");
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint("Google", "https://google.com", null, null, 30, user);
        MonitoredEndpointDTO monitoredEndpointDTO = new MonitoredEndpointDTO(2L, "Google", "https://google.com", null, null, 30, 1L);
        Mockito.when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(monitoredEndpoint);
        Mockito.when(validationService.getUserEntityOrThrowExceptionIfUserDoesNotExist("123")).thenReturn(user);

        MonitoredEndpointDTO returnedDTO = monitoredEndpointService.createMonitoredEndpoint("123", monitoredEndpointDTO);
        assertEquals(monitoredEndpointDTO.getName(), returnedDTO.getName());
        assertEquals(monitoredEndpointDTO.getUrl(), returnedDTO.getUrl());
        assertEquals(monitoredEndpointDTO.getMonitoredInterval(), returnedDTO.getMonitoredInterval());
        assertEquals(monitoredEndpointDTO.getOwnerId(), returnedDTO.getOwnerId());

        verify(monitoredEndpointRepository, times(1)).save(any(MonitoredEndpoint.class));
    }

    @Test
    public void testGetUserEndpoints() throws Exception {
        User user = new User(1L, "Batman", "batman@example.com", "123");
        MonitoredEndpoint monitoredEndpoint1 = new MonitoredEndpoint(2L, "Google", "https://google.com", null, null, 0, user);
        MonitoredEndpoint monitoredEndpoint2 = new MonitoredEndpoint(3L, "YouTube", "https://youtube.com", null, null, 0, user);
        List<MonitoredEndpoint> monitoredEndpoints = List.of(monitoredEndpoint1, monitoredEndpoint2);
        Mockito.when(monitoredEndpointRepository.findAllByOwner(null)).thenReturn(monitoredEndpoints);
        List<MonitoredEndpointDTO> returnedEndpoints = monitoredEndpointService.findAllUserEndpoints("123");

        assertEquals(2, returnedEndpoints.size());
    }

    @Test
    public void testUpdateEndpoint() throws Exception {
        User user = new User(1L, "Batman", "batman@example.com", "123");
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint("Google", "https://google.com", null, null, 30, user);
        MonitoredEndpointDTO monitoredEndpointDTO = new MonitoredEndpointDTO(2L, "Google", "https://google.com", null, null, 30, 1L);
        Mockito.when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(monitoredEndpoint);
        Mockito.when(validationService.getMonitoredEndpointOrThrowExceptionIfEndpointDoesNotExist(2L)).thenReturn(monitoredEndpoint);

        MonitoredEndpointDTO returnedDTO = monitoredEndpointService.updateById("123", 2L, monitoredEndpointDTO);
        assertEquals(monitoredEndpointDTO.getName(), returnedDTO.getName());
        assertEquals(monitoredEndpointDTO.getUrl(), returnedDTO.getUrl());
        assertEquals(monitoredEndpointDTO.getMonitoredInterval(), returnedDTO.getMonitoredInterval());
        assertEquals(monitoredEndpointDTO.getOwnerId(), returnedDTO.getOwnerId());
    }

    @Test
    public void testDeleteEndpoint() throws Exception {
        MonitoredEndpoint monitoredEndpoint = new MonitoredEndpoint();
        monitoredEndpoint.setId(1L);
        Mockito.when(validationService.getMonitoredEndpointOrThrowExceptionIfEndpointDoesNotExist(any(Long.class))).thenReturn(monitoredEndpoint);
        monitoredEndpointService.deleteMonitoredEndpointById("123", monitoredEndpoint.getId());
        verify(monitoredEndpointRepository, times(1)).deleteById(1L);
    }
}
