package com.aptmgt.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AppUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUtils.class);

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static Integer getRandomDigits() {
        try {
            return 10000000 + SECURE_RANDOM.nextInt(90000000);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String[] getArrayFromString(String value) {
        List<String> list = new ArrayList<>();
        if (value.contains(Constants.COMMA)) {
            Collections.addAll(list, value.split(Constants.COMMA));
        } else {
            list.add(value);
        }
        return list.toArray(new String[list.size()]);
    }

    public static String generateRandomPassword(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
