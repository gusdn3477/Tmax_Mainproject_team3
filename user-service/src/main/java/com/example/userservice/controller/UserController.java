package com.example.userservice.controller;

import com.example.userservice.dto.ApplyDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.ApplyEntity;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final UserService userService;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's Working in User Service, " +
                        "port(local.server.port)=%s, port(server.port)=%s, " +
                        "token_secret=%s, token_expiration_time=%s, gateway_ip=%s",
                env.getProperty("local.server.port"), env.getProperty("server.port"),
                env.getProperty("token.secret"), env.getProperty("token.expiration_time"), env.getProperty("gateway.ip"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }


    /*일반 사용자 회원가입*/
    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody @Valid RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        // convert UserDto to ResponseUser
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }



    /*일반 사용자(지원자) 회원삭제(탈퇴)*/
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@RequestBody @Valid RequestDeleteUser user){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);

        boolean status = userService.deleteUser(userDto.getUserId(),userDto.getEmail(), userDto.getPassword());

        String okMsg = "OK";
        String errorMsg = "NO";

        if(status) {
            return ResponseEntity.status(HttpStatus.OK).body(okMsg);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(errorMsg);
        }
    }

    /* 일반사용자(지원자) 정보 수정*/
    @PutMapping("/users")
    public ResponseEntity<String> updateUser(@RequestBody @Valid RequestUpdateUser user){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDetails = mapper.map(user, UserDto.class);

        UserDto userDto = userService.getUserByUserId(user.getUserId());

        userService.updateByUserId(userDto, userDetails);

        String okMsg = "update user , 200 OK";
        return ResponseEntity.status(HttpStatus.OK).body(okMsg);
    }


    /* 전체 일반사용자(지원자) 목록 */
    @GetMapping("/users")
    public List<ResponseUser> getUsers(HttpServletRequest request) {
        Iterable<UserEntity> usersList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();

        usersList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return result;
    }

    /* 지원내역 전체 보기 */
    @GetMapping("/apply")
    public ResponseEntity<List<ResponseApply>> getApply(){
        Iterable<ApplyEntity> applyList = userService.getApplyByAll();
        List<ResponseApply> result = new ArrayList<>();

        applyList.forEach(v->{
            result.add(new ModelMapper().map(v, ResponseApply.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /* 공고별 지원내역 전체 보기 */
    @GetMapping("/apply/{jobsNo}")
    public ResponseEntity<List<ResponseApply>> getJobsNoApply(@PathVariable("jobsNo") String jobsNo){
        Iterable<ApplyEntity> jobApplyList = userService.getJobsAllApply(jobsNo);
        List<ResponseApply> result = new ArrayList<>();

        jobApplyList.forEach(v->{
            result.add(new ModelMapper().map(v, ResponseApply.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    /* 사용자 상세 보기 (with 주문 목록) */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }


    /* 지원자 공고 지원하기*/
    @PostMapping("/users/apply")
    public ResponseEntity<ResponseApply> createApply(@RequestBody @Valid RequestApply apply){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ApplyDto applyDto = mapper.map(apply, ApplyDto.class);

        userService.createApply(applyDto);

        UserDto userDto = new UserDto();
        ResponseApply returnValue = new ModelMapper().map(applyDto, ResponseApply.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);

    }

    /*일반 사용자 회원가입*/
//    @PostMapping("/users/register")
//    public ResponseEntity createUser(@RequestBody @Valid RequestUser user) {
//        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        UserDto userDto = mapper.map(user, UserDto.class);
//        userService.createUser(userDto);
//
//        // convert UserDto to ResponseUser
//        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
//    }

    /*지원자 비밀번호 찾기 */
    @PostMapping("/users/findpwd")
    public ResponseEntity findPwd(@RequestBody @Valid RequestFindPwd findPwdInfo){

        UserDto userDto = userService.getUserDetailsByEmail(findPwdInfo.getEmail());
        String resultValue = "FALSE";
        if (userDto.getPhoneNum().equals(findPwdInfo.getPhoneNum())) {

            userService.findPwd(userDto.getEmail());

            resultValue = "TRUE";
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultValue);
    }


    /* 지원자 공고 삭제하기 */
    @DeleteMapping("/users/apply")
    public ResponseEntity<String> deleteApply(@RequestBody @Valid RequestDeleteApply apply){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ApplyDto applyDto = mapper.map(apply, ApplyDto.class);

        boolean status = userService.deleteApply(applyDto.getJobsNo(), applyDto.getComfirmPassword(), applyDto.getPassword(), applyDto.getUserId());



        String okMsg = "delete userId , 200 OK";
        String errorMsg = "error~";

        if(status) {
            return ResponseEntity.status(HttpStatus.OK).body(okMsg);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(errorMsg);
        }

    }

    /* 공고 정보 수정*/
    @PutMapping("/users/apply")
    public ResponseEntity<String> updateApply(@RequestBody @Valid RequestUpdateApply apply){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ApplyDto applyDetails = mapper.map(apply, ApplyDto.class);

        ApplyDto applyDto = userService.getApplyByJobsNo(apply.getJobsNo());

        userService.updateByJobsNo(applyDto, applyDetails);

        String okMsg = "update user , 200 OK";
        return ResponseEntity.status(HttpStatus.OK).body(okMsg);
    }

    /*내가 지원한 전체 공고(전형) 내역 리스트로 보기 ( 관리자+ 인사팀전체 + 자기자신만 )*/
    @GetMapping("/apply/{userId}")
    public List<ResponseApply> getApply(@PathVariable("userId") String userId){

        Iterable<ApplyEntity> applysList = userService.getApplyByAll();

        List<ResponseApply> result = new ArrayList<>();

        applysList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseApply.class));
        });

        return result;
    }

    @GetMapping("/apply/{userId}/{jobsNo}")
    public List<ResponseApplyDetail> getApplyDetails(@PathVariable("userId") String userId, @PathVariable("jobsNo") String jobsNo){

        return null;
    }

}