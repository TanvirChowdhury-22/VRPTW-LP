package com.example.deliverymapping.controller;

import com.example.deliverymapping.util.BuyNowRandomStringUtils;

public class CustomRandomGenerator {
    public static String generateRandomProblemId() {
        boolean useLetters = true;
        boolean useNumbers = true;
        int problemIdLength = 12;
        String problemId = BuyNowRandomStringUtils.random(problemIdLength, useLetters, useNumbers).toUpperCase();
        return problemId;
    }

    public static String generateRandomUniqueId() {
        int uniqueIdLength = 12;
        boolean useLettersInId = true;
        boolean useNumbersInId = true;
        String uniqueId = BuyNowRandomStringUtils.random(uniqueIdLength, useLettersInId, useNumbersInId);
        return uniqueId;
    }
}
