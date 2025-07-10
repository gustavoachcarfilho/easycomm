package com.achcar_solutions.easycomm_api.services;

import com.achcar_solutions.easycomm_core.entities.certificate.Certificate;
import com.achcar_solutions.easycomm_core.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm_core.entities.certificate.CertificateStatus;
import com.achcar_solutions.easycomm_core.infra.kafka.CertificateKafkaMessage;
import com.achcar_solutions.easycomm_api.infra.kafka.KafkaTopicConfiguration;
import com.achcar_solutions.easycomm_core.infra.ports.StoragePort;
import com.achcar_solutions.easycomm_core.repositories.CertificateRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final StoragePort storagePort;
    private final KafkaTemplate<String, CertificateKafkaMessage> kafkaTemplate;

    private static final List<String> ALLOWED_FILE_TYPES = List.of("application/pdf", "image/png", "image/jpeg");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 MB

    public CertificateService(CertificateRepository certificateRepository,
                              StoragePort storagePort,
                              KafkaTemplate<String, CertificateKafkaMessage> kafkaTemplate) {
        this.certificateRepository = certificateRepository;
        this.storagePort = storagePort;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Certificate createCertificate(CertificateCreationRequest request, MultipartFile file) {

        var maybeExistentCertificate = certificateRepository.findByCreatedByAndTitleAndCategory(request.createdBy(), request.title(), request.category());
        if (maybeExistentCertificate.isPresent()) {
            throw new RuntimeException("Certificate already exists for the given user, title, and category.");
        }
        validateCertificate(file);

        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String s3ObjectKey = UUID.randomUUID().toString() + fileExtension;

            String fileUrl = storagePort.uploadFile(file.getBytes(), request.fileName(), request.fileType());

            Certificate certificate = Certificate.builder()
                    .createdBy(request.createdBy())
                    .title(request.title())
                    .category(request.category())
                    .durationInHours(request.durationInHours())
                    .fileName(originalFileName)
                    .fileType(file.getContentType())
                    .fileUrl(fileUrl)
                    .s3ObjectKey(s3ObjectKey)
                    .status(CertificateStatus.PENDING)
                    .build();

            Certificate savedCertificate = certificateRepository.insert(certificate);

            CertificateKafkaMessage kafkaMessage = new CertificateKafkaMessage(savedCertificate.getId(), savedCertificate.getS3ObjectKey());
            kafkaTemplate.send(KafkaTopicConfiguration.CERTIFICATE_TOPIC, kafkaMessage);
            return savedCertificate;

        } catch (IOException exception) {
            throw new RuntimeException("Error uploading file to storage: " + exception.getMessage(), exception);
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
