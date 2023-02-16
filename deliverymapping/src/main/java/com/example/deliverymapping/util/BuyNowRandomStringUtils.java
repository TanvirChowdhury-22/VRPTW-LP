package com.example.deliverymapping.util;

import java.util.Random;

public class BuyNowRandomStringUtils {

    public static final String EMPTY = "";
    public static final String EMPTY_BOOLEAN_FIELDS = "Both fields can not be false";

    public static String random(final int length, final boolean letters, final boolean numbers) {
        Random random = new Random(System.currentTimeMillis());
        int leftLimit = 0;
        int rightLimit = 0;

        if (length == 0) {
            return EMPTY;
        } else if (length < 0) {
            throw new IllegalArgumentException("Requested random string length " + length + " is less than 0.");
        }

        if (letters && numbers) {
            leftLimit = 48;
            rightLimit = 122;
        } else if (!letters && numbers) {
            leftLimit = 48;
            rightLimit = 57;
        } else if (letters && !numbers) {
            leftLimit = 65;
            rightLimit = 122;
        } else {
            throw new IllegalArgumentException(EMPTY_BOOLEAN_FIELDS);
        }

        String generatedString;
        try {
            generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }

        return generatedString;
    }
}
