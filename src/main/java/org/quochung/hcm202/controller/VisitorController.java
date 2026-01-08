package org.quochung.hcm202.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.quochung.hcm202.dto.response.VisitorResponse;
import org.quochung.hcm202.mapper.VisitorMapper;
import org.quochung.hcm202.service.VisitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;
    private final VisitorMapper visitorMapper;

    @GetMapping("/list")
    public ResponseEntity<List<VisitorResponse>> getAllVisitors() {
        List<VisitorResponse> response = visitorService.getAllVisitors()
                .stream()
                .map(visitorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getVisitorCount() {
        return ResponseEntity.ok(visitorService.getTotalCount());
    }

    @PostMapping("/track")
    public ResponseEntity<String> trackVisitor(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");

        visitorService.recordVisitor(ipAddress, userAgent);

        return ResponseEntity.ok("Đã ghi nhận truy cập từ IP: +" + ipAddress);
    }
}
