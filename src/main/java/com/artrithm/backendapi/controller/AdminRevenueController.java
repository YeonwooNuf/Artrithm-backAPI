package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.AdminRevenueDto;
import com.artrithm.backendapi.service.AdminRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class AdminRevenueController {

    private final AdminRevenueService adminRevenueService;

    @GetMapping
    public AdminRevenueDto getRevenue(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return adminRevenueService.getRevenueStatistics(start, end);
    }
}
