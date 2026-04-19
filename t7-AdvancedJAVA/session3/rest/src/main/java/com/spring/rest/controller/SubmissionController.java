package com.spring.rest.controller;

import com.spring.rest.dto.ApiMessageResponse;
import com.spring.rest.dto.SubmitRequest;
import com.spring.rest.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiMessageResponse> submit(@RequestBody SubmitRequest request) {
        submissionService.submitStructuredData(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiMessageResponse("Structured data submitted successfully"));
    }
}
