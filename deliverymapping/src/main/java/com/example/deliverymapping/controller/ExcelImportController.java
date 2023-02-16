package com.example.deliverymapping.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.deliverymapping.BusinessConstant;
import com.example.deliverymapping.model.DeliveryProblem;
import com.example.deliverymapping.model.ProblemDetails;
import com.example.deliverymapping.repositories.DeliveryProblemRepository;
import com.example.deliverymapping.repositories.ProblemDetailsRepository;

@Controller
public class ExcelImportController {
    public static int MAX_RETRY_UNIQUEID_GEN = 3;
    @Autowired
    private DeliveryProblemRepository deliveryProblemRepository;
    @Autowired
    private ProblemDetailsRepository problemDetailsRepository;

    @GetMapping("/import/excel")
    public String importFromExcel() throws Exception {
        return "importexcel";
    }

    @PostMapping("/import")
    public String mapReapExcelDatatoDB(Model model, @RequestParam("file") MultipartFile reapExcelDataFile)
            throws IOException, com.example.deliverymapping.controller.UniqueIDGenMaxLimitException {
        DeliveryProblem deliveryProblem = new DeliveryProblem();
        String problemId = getAProblemID();
        deliveryProblem.setProblemId(problemId);
        List<ProblemDetails> problemDetailsList = new ArrayList<ProblemDetails>();
        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        String latitude = "0";
        String longitude = "0";
        Boolean isProcessed = false;
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            ProblemDetails problemDetails = new ProblemDetails();
            try {
                XSSFRow row = worksheet.getRow(i);
                Long userId = (long) row.getCell(0).getNumericCellValue();
                String streetAddress = row.getCell(1).getStringCellValue();
                String area = row.getCell(2).getStringCellValue();
                String thana = row.getCell(3).getStringCellValue();
                String district = row.getCell(4).getStringCellValue();
                String weightString = row.getCell(5).getStringCellValue();
                Double weight = Double.parseDouble(weightString);
                String lastDeliveryDate = row.getCell(6).getStringCellValue();
                String earliestDeliveryDate = row.getCell(7).getStringCellValue();
                setProblemDetails(problemId, problemDetailsList, latitude, longitude, isProcessed, problemDetails,
                        userId, streetAddress, area, thana, district, weight, lastDeliveryDate, earliestDeliveryDate);
                problemDetailsRepository.save(problemDetails);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        List<ProblemDetails> problemDetailsList2 = problemDetailsRepository.findByProblemId(problemId);

        if (!(problemDetailsList2.size() > 0)) {
            deliveryProblem.setIsResultParsed(BusinessConstant.PROCESS_STATUS.PENDING.ordinal());
            deliveryProblem.setIsRouteProcessed(BusinessConstant.ROUTE_PROCESS_STATUS.PENDING.ordinal());
            deliveryProblem = deliveryProblemRepository.save(deliveryProblem);
        }
        modelOutput(model, deliveryProblem);

        return "problemid";
    }

    private void modelOutput(Model model, DeliveryProblem deliveryProblem) {
        String mapURL = "";
        model.addAttribute("problem_id", deliveryProblem.getProblemId());
        mapURL = "/map?q=" + deliveryProblem.getProblemId();
        model.addAttribute("map_url", mapURL);
    }

    private void setProblemDetails(String problemId, List<ProblemDetails> problemDetailsList, String latitude,
            String longitude, Boolean isProcessed, ProblemDetails problemDetails, Long userId, String streetAddress,
            String area, String thana, String district, Double weight, String lastDeliveryDate,
            String earliestDeliveryDate) {
        problemDetails.setUserId(userId);
        problemDetails.setStreetAddress(streetAddress);
        problemDetails.setThana(thana);
        problemDetails.setArea(area);
        problemDetails.setDistrict(district);
        problemDetails.setProductWeight(weight);
        problemDetails.setLastDeliveryDate(lastDeliveryDate);
        problemDetails.setEarliestDeliveryDate(earliestDeliveryDate);
        problemDetails.setLatitude(latitude);
        problemDetails.setLongitude(longitude);
        problemDetails.setIsProcessed(isProcessed);
        problemDetails.setProblemId(problemId);
        problemDetailsList.add(problemDetails);
    }

    private String getAProblemID() throws UniqueIDGenMaxLimitException {
        String problemId = CustomRandomGenerator.generateRandomProblemId();
        int tryCount = 0;
        while (tryCount < MAX_RETRY_UNIQUEID_GEN && deliveryProblemRepository.findByProblemId(problemId).isPresent()) {
            problemId = CustomRandomGenerator.generateRandomProblemId();
            tryCount++;
        }
        if (tryCount == MAX_RETRY_UNIQUEID_GEN) {
            throw new UniqueIDGenMaxLimitException("Max Limit Exceeded");
        }
        return problemId;
    }

}