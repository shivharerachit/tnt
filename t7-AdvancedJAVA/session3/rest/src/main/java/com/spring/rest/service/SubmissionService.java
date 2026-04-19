package com.spring.rest.service;

import com.spring.rest.dto.SubmitRequest;

import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    public void submitStructuredData(SubmitRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (isBlank(request.getTitle()) || isBlank(request.getDescription()) || isBlank(request.getSubmittedBy())) {
            throw new BadRequestException("title, description and submittedBy are mandatory");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
