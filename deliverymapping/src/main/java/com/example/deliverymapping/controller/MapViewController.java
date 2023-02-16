package com.example.deliverymapping.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.deliverymapping.model.DeliveryInfo;
import com.example.deliverymapping.model.ProblemResult;
import com.example.deliverymapping.model.ProblemResultParsed;
import com.example.deliverymapping.repositories.CustomQueryRepository;
import com.example.deliverymapping.repositories.ProblemDetailsRepository;
import com.example.deliverymapping.repositories.ProblemResultRepository;

@Controller
public class MapViewController {

    @Autowired
    private CustomQueryRepository customQueryRepository;

    @Autowired
    private ProblemDetailsRepository problemDetailsRepository;

    @Autowired
    private ProblemResultRepository problemResultRepository;

    @GetMapping("/map")
    public String mainWeb(Model model, HttpServletRequest request,
            @RequestParam(name = "q", required = true) String problemId) {

        List<DeliveryInfo> deliveryInfoList = customQueryRepository.findByProblemId(problemId);

        model.addAttribute("delivery_info_list", deliveryInfoList);

        long processedCount = problemDetailsRepository.countByProblemIdAndIsProcessed(problemId, false);

        boolean isLatLonProcessed = false;
        if (processedCount == 0) {
            isLatLonProcessed = true;
        }
        if (!isLatLonProcessed) {
            return "processing";
        }
        String result = "";

        Optional<ProblemResult> resultStringOptional = problemResultRepository.findByProblemId(problemId);
        if (resultStringOptional.isPresent()) {
            result = resultStringOptional.get().getParsedResult();
            result = result.replace("'", "");
            result = result.replace("Depo", "");
            String[] vehicleRoutesArray = result.split("], ", 2);
            String vehicleRoute1 = vehicleRoutesArray[0];
            String vehicleRoute2 = vehicleRoutesArray[1];

            String[] vehicleRoutesArray1 = vehicleRoute1.split(": ", 2);
            String vehiclename1 = vehicleRoutesArray1[0];
            String vehicle1Route = vehicleRoutesArray1[1];
            String[] vehicleRoutesArray2 = vehicleRoute2.split(": ", 2);
            String vehiclename2 = vehicleRoutesArray2[0];
            String vehicle2Route = vehicleRoutesArray2[1];

            String v1 = vehiclename1.replace("{", "");
            String route1 = vehicle1Route.replace("[", "");
            String v2 = vehiclename2;
            String route2 = vehicle2Route.replace("[", "");
            route2 = route2.replace("]", "");
            route2 = route2.replace("}", "");

            String finalEntryRoute1 = "";
            String[] route1splited = route1.split(", ");
            String from1 = route1splited[0];
            String to1 = route1splited[1];
            if (from1.equals(to1)) {
                finalEntryRoute1 = "Stay at the Depo";
            } else {
                finalEntryRoute1 = route1.replace(", ", "-->");
            }

            String finalEntryRoute2 = "";
            String[] route2splited = route2.split(", ");
            String from2 = route2splited[0];
            String to2 = route2splited[1];
            if (from2.equals(to2)) {
                finalEntryRoute2 = "Stay at the Depo";
            } else {
                finalEntryRoute2 = route2.replace(", ", "-->");
            }

            List<ProblemResultParsed> problemResultParsedList = new ArrayList<ProblemResultParsed>();
            problemResultParsedList.add(new ProblemResultParsed(v1, finalEntryRoute1));
            problemResultParsedList.add(new ProblemResultParsed(v2, finalEntryRoute2));

            System.out.println(v1 + ": " + finalEntryRoute1);
            System.out.println(v2 + ": " + finalEntryRoute2);

            model.addAttribute("problem_list", problemResultParsedList);

        }
        return "mapview";
    }
}
