package com.example.deliverymapping.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.deliverymapping.model.ProblemResult;


public interface ProblemResultRepository extends CrudRepository<ProblemResult, Long>{
    Optional<ProblemResult> findByProblemId(String problemId);

}
