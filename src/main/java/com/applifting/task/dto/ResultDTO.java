package com.applifting.task.dto;

import com.applifting.task.entity.MonitoredEndpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultDTO {
    private Long id;
    private LocalDateTime dateOfCheck;
    private String returnedHttpStatusCode;
    private String returnedPayload;
    private MonitoredEndpoint monitoredEndpoint;
}
