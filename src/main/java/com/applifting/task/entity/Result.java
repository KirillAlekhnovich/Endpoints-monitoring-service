package com.applifting.task.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private LocalDateTime dateOfCheck;

    @NotNull
    private String returnedHttpStatusCode;

    @Lob
    @NotNull
    private String returnedPayload;

    @NotNull
    @ManyToOne(targetEntity = MonitoredEndpoint.class)
    @JoinColumn(name = "monitored_endpoint_id")
    private MonitoredEndpoint monitoredEndpoint;

    public Result(LocalDateTime dateOfCheck, String returnedHttpStatusCode, String returnedPayload, MonitoredEndpoint monitoredEndpoint) {
        this.dateOfCheck = dateOfCheck;
        this.returnedHttpStatusCode = returnedHttpStatusCode;
        this.returnedPayload = returnedPayload;
        this.monitoredEndpoint = monitoredEndpoint;
    }
}
