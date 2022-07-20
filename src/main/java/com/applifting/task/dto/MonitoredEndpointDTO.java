package com.applifting.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MonitoredEndpointDTO {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfLastCheck;
    private Integer monitoredInterval;
    private Long ownerId;
}
