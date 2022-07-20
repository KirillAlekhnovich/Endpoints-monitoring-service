package com.applifting.task.repository;

import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findAllByMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint);
}
