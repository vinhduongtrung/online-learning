package com.mpteam1.utils.impl;

import com.mpteam1.repository.UserRepository;
import com.mpteam1.utils.GenerateAccountName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GenerateAccountNameImpl implements GenerateAccountName {
    private UserRepository userRepository;
    @Override
    public String generate(String fullName) {
        String[] partNames = fullName.trim().split(" ");
        String lastName = partNames[partNames.length - 1];
        if (!lastName.isEmpty()) {
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        }
        List<String> firstLetters = Arrays.stream(partNames, 0, partNames.length - 1)
                .map(s -> s.substring(0, 1).toUpperCase())
                .collect(Collectors.toList());
        firstLetters.add(0, lastName);
        String accountName = String.join("", firstLetters);
        String maxAccountName = userRepository.findMaxAccountName(accountName);
        if(maxAccountName != null) {
            Pattern pattern = Pattern.compile("^(\\D+)(\\d*)$");
            Matcher matcher = pattern.matcher(maxAccountName);
            if(matcher.matches()) {
                String numberPart = matcher.group(2);
                if(numberPart == null || numberPart.isEmpty()) {
                    accountName = accountName + "1";
                } else {
                    int number = Integer.parseInt(numberPart);
                    accountName = accountName + (number + 1);
                }
            }
        }
        return accountName;
    }
}
