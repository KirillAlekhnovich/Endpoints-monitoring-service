package com.applifting.task.repository;

import com.applifting.task.entity.MonitoredEndpoint;
import com.applifting.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Long> {
    List<MonitoredEndpoint> findAllByOwner(User user);
    MonitoredEndpoint findByOwnerAndUrl(User owner, String url);
}
