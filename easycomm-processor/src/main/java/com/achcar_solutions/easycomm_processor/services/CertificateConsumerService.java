package com.achcar_solutions.easycomm_processor.services;

import com.achcar_solutions.easycomm_core.entities.certificate.CertificateStatus;
import com.achcar_solutions.easycomm_core.infra.kafka.CertificateKafkaMessage;
import com.achcar_solutions.easycomm_core.infra.ports.StoragePort;
import com.achcar_solutions.easycomm_core.repositories.CertificateRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CertificateConsumerService {

    private final CertificateRepository certificateRepository;
    private final StoragePort storagePort;

    public CertificateConsumerService(CertificateRepository certificateRepository, StoragePort storagePort) {
        this.certificateRepository = certificateRepository;
        this.storagePort = storagePort;
    }

    @KafkaListener(topics = "certificates-to-process", groupId = "certificate-processor")
    public void consumeCertificate(CertificateKafkaMessage kafkaMessage) {
        certificateRepository.findById(kafkaMessage.certificateId()).ifPresentOrElse(
                certificate -> {
                    try {
                        byte[] fileData = storagePort.downloadFile(kafkaMessage.s3ObjectKey());
                        boolean ocrSimulation = true;
                        if(ocrSimulation) {
                            certificate.setStatus(CertificateStatus.APPROVED);
                        } else {
                            certificate.setStatus(CertificateStatus.DENIED);
                        }
                        certificateRepository.save(certificate);
                    } catch (Exception e) {
                        System.out.println("Error while processing certificate: " + e.getMessage());
                    }
                },
                () -> System.out.println("Certificate not found: " + kafkaMessage.certificateId())
        );
    }
}
