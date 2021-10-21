package com.example.job.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class JobDto implements Serializable{
    private Long jobsNo; // 공고번호
    private String corpNo2; //기업코드?
    private String empNo; // 인사담당자코드
    private String jobsTitle; //제목
    private String jobsContext; //내용
    private Integer recruitNum; //채용인원
    private String favoriteLang; //선호언어
    private String jobLocation; //근무지역
    private Date createdAt; //생성일
    private String jobType; //
    private String jobQualify;//지원자격
    private String employType; // 채용유형
    private Date applyStart; //지원시작일
    private String workDetail; //업무내용
    private Date applyEnd; //지원마감일
    private Date intvStart1; //면접1시작일
    private Date intvEnd1;
    private Date intvStart2;
    private Date intvEnd2;



}
