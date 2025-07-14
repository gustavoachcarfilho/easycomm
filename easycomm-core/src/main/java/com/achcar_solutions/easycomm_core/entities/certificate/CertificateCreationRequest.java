package com.achcar_solutions.easycomm_core.entities.certificate;

import java.time.LocalDate;

public record CertificateCreationRequest(String createdBy,
                                         String title,
                                         CertificateCategory category,
                                         Integer durationInHours,
                                         LocalDate expirationDate,
                                         String fileName,
                                         String fileType) {
}
