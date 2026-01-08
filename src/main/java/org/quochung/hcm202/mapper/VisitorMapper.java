package org.quochung.hcm202.mapper;

import org.quochung.hcm202.dto.response.VisitorResponse;
import org.quochung.hcm202.entity.Visitor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class VisitorMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public VisitorResponse toResponse(Visitor visitor) {
        return VisitorResponse.builder()
                .id(visitor.getVisitorId())
                .ipAddress(visitor.getIpAddress())
                .userAgent(visitor.getUserAgent())
                .visitDate(visitor.getVisitDate().format(formatter))
                .build();
    }
}

