package com.mpteam1.utils.impl;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class ConvertToEnglishNameImpl {
    public static String convertToEnglishName(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
    }
}
