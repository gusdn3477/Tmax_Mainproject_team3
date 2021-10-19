package com.example.processservice.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class WrittenDto implements Serializable {

    private String applyNum;
    private String userId;
    private String empNo;
    private Integer writtenScore;
    private String writtenResult;
    private Integer jobsNo;
}