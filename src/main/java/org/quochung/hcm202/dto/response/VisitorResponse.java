package org.quochung.hcm202.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VisitorResponse {
    private Long id;
    private String ipAddress;
    private String userAgent;
    private String visitDate;
}
