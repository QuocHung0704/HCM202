package org.quochung.hcm202.service.impl;

import lombok.RequiredArgsConstructor;
import org.quochung.hcm202.entity.Visitor;
import org.quochung.hcm202.repository.VisitorRepository;
import org.quochung.hcm202.service.VisitorService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    @Override
    public void recordVisitor(String ip, String userAgent) {
        var lastVisitor = visitorRepository.findFirstByIpAddressOrderByVisitDateDesc(ip);

        if (lastVisitor.isEmpty() || lastVisitor.get().getVisitDate().isBefore(LocalDateTime.now().minusSeconds(10))) {
            createNewVisitor(ip, userAgent);
        }
    }

    public long getTotalCount() {
        return visitorRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll(Sort.by(Sort.Direction.DESC, "visitDate"));
    }

    private void createNewVisitor(String ip, String userAgent) {
        Visitor visitor = Visitor.builder()
                .ipAddress(ip)
                .userAgent(userAgent)
                .visitDate(LocalDateTime.now())
                .build();
        visitorRepository.save(visitor);
    }
}
