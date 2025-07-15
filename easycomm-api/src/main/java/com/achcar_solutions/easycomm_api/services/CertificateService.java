package com.achcar_solutions.easycomm_api.services;

import com.achcar_solutions.easycomm_core.entities.certificate.Certificate;
import com.achcar_solutions.easycomm_core.entities.certificate.CertificateCreationRequest;
import com.achcar_solutions.easycomm_core.entities.certificate.CertificateStatus;
import com.achcar_solutions.easycomm_core.infra.kafka.CertificateKafkaMessage;
import com.achcar_solutions.easycomm_api.infra.kafka.KafkaTopicConfiguration;
import com.achcar_solutions.easycomm_core.infra.ports.StoragePort;
import com.achcar_solutions.easycomm_core.repositories.CertificateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);
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
        logger.info("Received request to create a new certificate with title: '{}'", request.title());

        logger.debug("Checking for existing certificate for user '{}', title '{}', and category '{}'", request.createdBy(), request.title(), request.category());
        var maybeExistentCertificate = certificateRepository.findByCreatedByAndTitleAndCategory(request.createdBy(), request.title(), request.category());
        if (maybeExistentCertificate.isPresent()) {
            logger.warn("Certificate creation blocked: A certificate with the same title and category already exists for this user.");
            throw new RuntimeException("Certificate already exists for the given user, title, and category.");
        }
        logger.info("No duplicates found. Proceeding with file validation.");
        validateCertificate(file);
        logger.info("File validation successful.");

        try {
            String originalFileName = file.getOriginalFilename();
            assert originalFileName != null;
            String fileType = originalFileName.substring(originalFileName.lastIndexOf("."));
            String s3ObjectKey = UUID.randomUUID() + fileType;
            logger.info("Generated unique S3 object key: {}", s3ObjectKey);

            logger.info("Uploading file to S3 storage...");
            String fileUrl = storagePort.uploadFile(file.getBytes(), s3ObjectKey, fileType);
            logger.info("File uploaded successfully. URL: {}", fileUrl);

            Certificate certificate = Certificate.builder()
                    .createdBy(request.createdBy())
                    .createdDate(LocalDateTime.now())
                    .title(request.title())
                    .category(request.category())
                    .durationInHours(request.durationInHours())
                    .expirationDate(request.expirationDate())
                    .fileUrl(fileUrl)
                    .fileName(originalFileName)
                    .fileType(fileType)
                    .s3ObjectKey(s3ObjectKey)
                    .status(CertificateStatus.PENDING)
                    .build();

            logger.info("Saving certificate metadata to the database...");
            Certificate savedCertificate = certificateRepository.insert(certificate);
            logger.info("Certificate metadata saved with ID: {}", savedCertificate.getId());

            CertificateKafkaMessage kafkaMessage = new CertificateKafkaMessage(savedCertificate.getId(), savedCertificate.getS3ObjectKey());
            logger.info("Sending message to Kafka topic '{}' for certificate ID: {}", KafkaTopicConfiguration.CERTIFICATE_TOPIC, savedCertificate.getId());
            kafkaTemplate.send(KafkaTopicConfiguration.CERTIFICATE_TOPIC, kafkaMessage);
            logger.info("Message successfully sent to Kafka.");
            return savedCertificate;

        } catch (IOException exception) {
            logger.error("Error reading file bytes during upload for certificate title '{}'", request.title(), exception);
            throw new RuntimeException("Error uploading file to storage: " + exception.getMessage(), exception);
        }


    }

    public List<Certificate> getAllCertificates() {
        logger.info("Request received to get all certificates.");
        return certificateRepository.findAll();
    }

    public Certificate getCertificateById(String id) {
        logger.info("Request received to get certificate by ID: {}", id);
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));
    }

    public void deleteCertificate(String id) {
        logger.info("Request received to delete certificate by ID: {}", id);
        // TODO: We'll need to add logic here to delete from S3 as well.
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
