package com.example.deliverymapping.schedulejobs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.deliverymapping.BusinessConstant;
import com.example.deliverymapping.model.DeliveryProblem;
import com.example.deliverymapping.model.NominatimResJson;
import com.example.deliverymapping.model.ProblemDetails;
import com.example.deliverymapping.repositories.DeliveryProblemRepository;
import com.example.deliverymapping.repositories.ProblemDetailsRepository;

@Component
public class UpdateLatLong {
    private static String nominatimHostName = "192.168.122.195";

    @Autowired
    private ProblemDetailsRepository problemDetailsRepository;

    @Autowired
    private DeliveryProblemRepository deliveryProblemRepository;

    @Scheduled(fixedDelay = 10000) // in milliseconds
    public void scheduledTask() {
        // System.out.println("Checking Lat lon ...");
        String problemId = "";
        List<ProblemDetails> problemDetailsList = problemDetailsRepository.findByLatitudeAndLongitude("0", "0");
        for (ProblemDetails problemDetails : problemDetailsList) {
            String lat = "0";
            String lon = "0";
            String street = problemDetails.getStreetAddress();
            // String city = problemDetails.getArea();
            String country = "Bangladesh";
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("street: " + street);
            // System.out.println("city: " + city);
            String district = problemDetails.getDistrict();
            String thana = problemDetails.getThana();

            // "city=" + city
            String resourceUrl = "http://" + nominatimHostName + "/nominatim/search.php?street=" + street + "&county="
                    + thana + "&state=" + district + "&country=" + country + "&format=json";

            // Fetch JSON response as String wrapped in ResponseEntity
            ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);

            String productsJson = response.getBody();
            System.out.println(productsJson);
            JsonParser springParser = JsonParserFactory.getJsonParser();
            List<Object> mapList = springParser.parseList(productsJson);

            if (mapList.size() == 0) {
                // no lat long found use thana/county as street address
                resourceUrl = "http://" + nominatimHostName + "/nominatim/search.php?county=" + thana + "&city="
                        + district + "&country=" + country + "&format=json";
                // Fetch JSON response as String wrapped in ResponseEntity
                response = restTemplate.getForEntity(resourceUrl, String.class);
                productsJson = response.getBody();
                System.out.println("re-try: " + productsJson);
                springParser = JsonParserFactory.getJsonParser();
                mapList = springParser.parseList(productsJson);
                // retry with area as street
                if (mapList.size() == 0) {
                    // no lat long found use thana/county as street address
                    resourceUrl = "http://" + nominatimHostName + "/nominatim/search.php?county=" + thana + "&state="
                            + district + "&country=" + country + "&format=json";
                    // Fetch JSON response as String wrapped in ResponseEntity
                    response = restTemplate.getForEntity(resourceUrl, String.class);
                    productsJson = response.getBody();
                    System.out.println("re-try: " + productsJson);
                    springParser = JsonParserFactory.getJsonParser();
                    mapList = springParser.parseList(productsJson);
                    // re-try with thana
                    if (mapList.size() == 0) {
                        // no lat long found use thana/county as street address
                        resourceUrl = "http://" + nominatimHostName + "/nominatim/search.php?state=" + district
                                + "&format=json";
                        // Fetch JSON response as String wrapped in ResponseEntity
                        response = restTemplate.getForEntity(resourceUrl, String.class);
                        productsJson = response.getBody();
                        System.out.println("re-try: " + productsJson);
                        springParser = JsonParserFactory.getJsonParser();
                        mapList = springParser.parseList(productsJson);

                        if (mapList.size() == 0) {
                            // no lat long found use thana/county as street address
                            resourceUrl = "http://" + nominatimHostName + "/nominatim/search.php?city=" + district
                                    + "&format=json";
                            // Fetch JSON response as String wrapped in ResponseEntity
                            response = restTemplate.getForEntity(resourceUrl, String.class);
                            productsJson = response.getBody();
                            System.out.println("re-try: " + productsJson);
                            springParser = JsonParserFactory.getJsonParser();
                            mapList = springParser.parseList(productsJson);
                        }
                    }
                }
            }

            for (Object object : mapList) {
                NominatimResJson nominatimResJson = new NominatimResJson();
                @SuppressWarnings("unchecked")
                Map<String, Object> mapdata = (Map<String, Object>) object;
                for (Map.Entry<String, Object> entry : mapdata.entrySet()) {
                    String jsonKey = entry.getKey();

                    if (jsonKey.equals("lat")) {
                        String jsonValue = entry.getValue().toString();

                        nominatimResJson.setLat(jsonValue);

                    } else if (jsonKey.equals("lon")) {
                        String jsonValue = entry.getValue().toString();
                        nominatimResJson.setLon(jsonValue);
                    }
                }
                lat = nominatimResJson.getLat();
                lon = nominatimResJson.getLon();
            }

            if (problemDetails.getLatitude() != "0" && problemDetails.getLongitude() != "0") {
                problemDetails.setIsProcessed(true);
                problemDetailsRepository.save(problemDetails);
            }

            problemId = problemDetails.getProblemId();
            Optional<ProblemDetails> problemDetailsOptional = problemDetailsRepository
                    .findByLatitudeAndLongitudeAndIsProcessedAndProblemId("0", "0", true, problemId);
            if (problemDetailsOptional.isPresent()) {
                Double latDouble = Double.parseDouble(lat);
                Double lonDouble = Double.parseDouble(lon);
                latDouble = randomDouble(latDouble - 0.0005, latDouble + 0.0005);
                lonDouble = randomDouble(lonDouble - 0.0005, lonDouble + 0.0005);

                Optional<ProblemDetails> problemDetailsOptional1 = problemDetailsRepository
                        .findByLatitudeAndLongitudeAndIsProcessedAndProblemId(latDouble.toString(),
                                lonDouble.toString(), true, problemId);
                // TODO: recheck again if new lat long is present by change
                // try to generate new random value (max 3 times)
                if (problemDetailsOptional1.isPresent()) {
                    latDouble = randomDouble(latDouble - 0.0005, latDouble + 0.0005);
                    lonDouble = randomDouble(lonDouble - 0.0005, lonDouble + 0.0005);
                    problemDetails.setLatitude(latDouble.toString());
                    problemDetails.setLongitude(lonDouble.toString());
                    problemDetailsRepository.save(problemDetails);
                } else {
                    problemDetails.setLatitude(latDouble.toString());
                    problemDetails.setLongitude(lonDouble.toString());
                    problemDetailsRepository.save(problemDetails);
                }
            } else {
                problemDetails.setLatitude(lat);
                problemDetails.setLongitude(lon);
                problemDetailsRepository.save(problemDetails);
            }
        }
        Optional<DeliveryProblem> deliveryProblemOptional = deliveryProblemRepository.findByProblemId(problemId);
        if (deliveryProblemOptional.isPresent()) {
            deliveryProblemOptional.get()
                    .setIsRouteProcessed(BusinessConstant.ROUTE_PROCESS_STATUS.NOTCOMPLETED.ordinal());
            deliveryProblemOptional.get().setIsResultParsed(BusinessConstant.PROCESS_STATUS.PENDING.ordinal());
            deliveryProblemRepository.save(deliveryProblemOptional.get());
        }
    }

    public static Double randomDouble(double d, double e) {
        Random random = new Random();

        return random.nextDouble(d, e);
    }
}
