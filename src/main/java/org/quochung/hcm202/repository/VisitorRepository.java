package org.quochung.hcm202.repository;

import org.quochung.hcm202.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findFirstByIpAddressOrderByVisitDateDesc(String ipAddress);}