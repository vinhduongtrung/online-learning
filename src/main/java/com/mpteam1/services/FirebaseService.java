package com.mpteam1.services;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseService {
    void init(ApplicationReadyEvent event);
    String save(MultipartFile file) throws IOException;
    String generateFileName(String originalFileName);
    String getExtension(String originalFileName);
    String getImageUrl(String name);
    void delete(String name) throws IOException;
}
