package com.example.myselectshop.controller;

import com.example.myselectshop.entity.ApiUseTime;
import com.example.myselectshop.entity.UserRoleEnum;
import com.example.myselectshop.repository.ApiUseTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiUseTimeController {

    private final ApiUseTimeRepository apiUseTimeRepository;

    @GetMapping("/api/use/time")
    @Secured(UserRoleEnum.Authority.ADMIN)
    public List<ApiUseTime> getAllApiUseTime() {
        return apiUseTimeRepository.findAll();
    }
}