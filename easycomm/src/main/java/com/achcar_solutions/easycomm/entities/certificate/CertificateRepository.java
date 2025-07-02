package com.achcar_solutions.easycomm.entities.certificate;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    Optional<Certificate> findById(String certificateId);

    List<Certificate> findAllByCreatedBy(String title);

    Optional<Certificate> findByCreatedByAndTitleAndCategory(String createdBy, String title, CertificateCategory category);
}
