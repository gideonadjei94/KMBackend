package com.gideon.knowmate.Utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileExtractor {

    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            log.error("File extraction failed", e);
            throw new RuntimeException("Could not extract file content");
        }
    }
}
