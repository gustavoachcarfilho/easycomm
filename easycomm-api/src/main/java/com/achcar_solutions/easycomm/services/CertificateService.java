package com.achcar_solutions.easycomm.services;

import com.achcar_solutions.easycomm.entities.certificate.Certificate;
import com.achcar_solutions.easycomm.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm.infra.ports.StoragePort;
import com.achcar_solutions.easycomm.repositories.CertificateRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    private final StoragePort storagePort;
    private static final List<String> ALLOWED_FILE_TYPES = List.of("application/pdf", "image/png", "image/jpeg");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 MB

    public CertificateService(StoragePort storagePort) {
        this.storagePort = storagePort;
    }

    public Certificate createCertificate(CertificateCreationRequest request, MultipartFile file) {
        try {
            var maybeExistentCertificate = certificateRepository.findByCreatedByAndTitleAndCategory(request.createdBy(), request.title(), request.category());
            if (maybeExistentCertificate.isPresent()) {
                throw new RuntimeException("Certificate already exists for the given user, title, and category.");
            }
            validateCertificate(file);
            String fileUrl = storagePort.uploadFile(file.getBytes(), request.fileName(), request.fileType());
            return certificateRepository.insert(Certificate.builder()
                    .createdBy(request.createdBy())
                    .title(request.title())
                    .category(request.category())
                    .durationInHours(request.durationInHours())
                    .fileName(request.fileName())
                    .fileType(request.fileType())
                    .fileUrl(fileUrl)
                    .build());
        } catch (DuplicateKeyException | IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Certificate getCertificateById(String id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));
    }

    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }

    private void validateCertificate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("O arquivo excede o tamanho máximo de 5 MB.");
        }

        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Apenas PDFs e imagens (PNG/JPG) são permitidos.");
        }
    }
}
