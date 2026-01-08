package org.quochung.hcm202.dto.request;

import lombok.Data;

@Data
public class VisitorRequest {
    private String ipAddress;
    private String userAgent;
}
