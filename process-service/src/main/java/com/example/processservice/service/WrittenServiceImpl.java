package com.example.processservice.service;

import com.example.processservice.jpa.WrittenEntity;
import com.example.processservice.jpa.WrittenRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class WrittenServiceImpl implements WrittenService{

    WrittenRepository writtenRepository;

    @Autowired
    public WrittenServiceImpl(WrittenRepository writtenRepository){
        this.writtenRepository = writtenRepository;
    }

    @Override
    public Iterable<WrittenEntity> getWrittenListByJobsNo(Integer jobsNo){
        return writtenRepository.findByJobsNo(jobsNo);
    }
}
