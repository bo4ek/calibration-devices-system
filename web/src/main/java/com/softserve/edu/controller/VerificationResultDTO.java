package com.softserve.edu.controller;

import com.softserve.edu.entity.verification.Verification;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class VerificationResultDTO {

    private String id;

    private String status;

    private MultipartFile file;

    public VerificationResultDTO() {}

    public VerificationResultDTO(String id, String status, MultipartFile file) {
        this.id = id;
        this.status = status;
        this.file = file;
    }

}
