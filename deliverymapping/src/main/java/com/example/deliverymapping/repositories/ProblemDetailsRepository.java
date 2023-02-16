package com.example.deliverymapping.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.deliverymapping.model.ProblemDetails;

public interface ProblemDetailsRepository extends CrudRepository<ProblemDetails, Long> {
    List<ProblemDetails> findByLatitudeAndLongitude(String Latitude, String Longitude);

    List<ProblemDetails> findByProblemIdAndIsProcessed(String problemId, Boolean isProcessed);

    Optional<ProblemDetails> findByLatitudeAndLongitudeAndIsProcessedAndProblemId(String latitude, String longitude,
            Boolean isProcessed, String problemId);

    long countByProblemIdAndIsProcessed(String problemId, Boolean isProcessed);

    List<ProblemDetails> findByProblemId(String problemId);

}
