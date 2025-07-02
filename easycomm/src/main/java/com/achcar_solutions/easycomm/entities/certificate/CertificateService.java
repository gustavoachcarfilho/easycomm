package com.achcar_solutions.easycomm.entities.certificate;

import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate createCertificate(CertificateCreationRequest request) {
        try {
            var maybeExistentCertificate = certificateRepository.findByCreatedByAndTitleAndCategory(request.createdBy(), request.title(), request.category());
            if (maybeExistentCertificate.isPresent()) {
                throw new RuntimeException("Certificate already exists for the given user, title, and category.");
            }
            return certificateRepository.insert(Certificate.builder()
                    .createdBy(request.createdBy())
                    .title(request.title())
                    .category(request.category())
                    .durationInHours(request.durationInHours())
                    .fileName(request.fileName())
                    .fileType(request.fileType())
                    .build());
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException(duplicateKeyException);
        }

    }
}
