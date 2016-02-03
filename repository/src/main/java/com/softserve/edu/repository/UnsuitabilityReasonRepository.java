package com.softserve.edu.repository;

import com.softserve.edu.entity.device.UnsuitabilityReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;


public interface UnsuitabilityReasonRepository extends CrudRepository<UnsuitabilityReason, Long> {
    List<UnsuitabilityReason> findAll();
}
