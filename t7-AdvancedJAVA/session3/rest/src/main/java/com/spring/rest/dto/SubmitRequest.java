package com.spring.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequest {
    private String title;
    private String description;
    private String submittedBy;
}
