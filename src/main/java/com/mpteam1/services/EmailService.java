package com.mpteam1.services;

import com.mpteam1.entities.enums.EEmailStatus;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/13/2024, Sat
 **/


public interface EmailService {
    void sendHtmlMessage(EEmailStatus eEmailStatus, String to, String subject, String content);
}
