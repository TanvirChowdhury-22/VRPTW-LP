package com.example.deliverymapping.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.deliverymapping.model.DeliveryProblem;

public interface DeliveryProblemRepository extends CrudRepository<DeliveryProblem, Long> {
    Optional<DeliveryProblem> findByProblemId(String problemId);

}