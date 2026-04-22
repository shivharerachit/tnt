package com.spring.rest.service;

import com.spring.rest.dto.SubmitRequest;
import com.spring.rest.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    public void submitStructuredData(SubmitRequest request) {
        if (request == null
                || request.getTitle() == null || request.getTitle().trim().isEmpty()
                || request.getDescription() == null || request.getDescription().trim().isEmpty()
                || request.getSubmittedBy() == null || request.getSubmittedBy().trim().isEmpty()) {
            throw new BadRequestException("title, description and submittedBy are mandatory");
        }
    }
}
