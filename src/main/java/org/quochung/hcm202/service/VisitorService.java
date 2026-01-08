package org.quochung.hcm202.service;

import org.quochung.hcm202.entity.Visitor;

import java.util.List;

public interface VisitorService {
    void recordVisitor(String ip, String userAgent);
    List<Visitor> getAllVisitors();
    long getTotalCount();
}
