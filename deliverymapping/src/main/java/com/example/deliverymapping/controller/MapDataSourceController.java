package com.example.deliverymapping.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.deliverymapping.model.ProblemDetails;
import com.example.deliverymapping.model.ProblemResult;
import com.example.deliverymapping.repositories.ProblemDetailsRepository;
import com.example.deliverymapping.repositories.ProblemResultRepository;

@RestController
public class MapDataSourceController {

    @Autowired
    private ProblemDetailsRepository problemDetailsRepository;

    @GetMapping(value = "/mapdata/{projectNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    String showMapJSON(@PathVariable("projectNo") String projectNo) {
        System.out.println("projectNo: " + projectNo);
        String dummyMapData = "[";
        List<ProblemDetails> problemDetailsList = problemDetailsRepository.findByProblemIdAndIsProcessed(projectNo,
                true);
        for (ProblemDetails problemDetails : problemDetailsList) {
            String lat = problemDetails.getLatitude();
            String lon = problemDetails.getLongitude();
            Boolean isExactAddress = problemDetails.getIsProcessed();
            dummyMapData = getMapData(dummyMapData, problemDetails, lat, lon, isExactAddress);
            if (problemDetailsList.size() > 1
                    && problemDetailsList.indexOf(problemDetails) < problemDetailsList.size() - 1) {
                dummyMapData += ", ";
            }
        }
        dummyMapData += "]";

        System.out.println(dummyMapData);

        return dummyMapData;
    }

    private String getMapData(String dummyMapData, ProblemDetails problemDetails, String lat, String lon,
            Boolean isExactAddress) {
        String description = problemDetails.getId() + " " + problemDetails.getArea() + ", "
                + problemDetails.getThana() + ", " + problemDetails.getDistrict();
        dummyMapData += "{ \"latitude\": \"" + lat + "\", \"longitude\": \"" + lon + "\", \"isExactAddress\": \""
                + isExactAddress + "\", \"description\": \"" + description + "\"" + "}";
        return dummyMapData;
    }
}
