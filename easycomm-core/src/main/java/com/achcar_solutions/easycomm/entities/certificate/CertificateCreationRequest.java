package com.achcar_solutions.easycomm.entities.certificate;

public record CertificateCreationRequest(String createdBy,
                                         String title,
                                         CertificateCategory category,
                                         Integer durationInHours,
                                         String fileName,
                                         String fileType) {
}
