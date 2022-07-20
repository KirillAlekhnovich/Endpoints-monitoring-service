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
public class MonitoredEndpoint {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String url;

    private LocalDateTime dateOfCreation;

    private LocalDateTime dateOfLastCheck;

    @NotNull
    private Integer monitoredInterval;

    @NotNull
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;

    public MonitoredEndpoint(String name, String url, LocalDateTime dateOfCreation, LocalDateTime dateOfLastCheck, Integer monitoredInterval, User owner) {
        this.name = name;
        this.url = url;
        this.dateOfCreation = dateOfCreation;
        this.dateOfLastCheck = dateOfLastCheck;
        this.monitoredInterval = monitoredInterval;
        this.owner = owner;
    }

    public void updateParameters(String name, String url, Integer monitoredInterval) {
        this.name = name;
        this.url = url;
        this.monitoredInterval = monitoredInterval;
    }
}
