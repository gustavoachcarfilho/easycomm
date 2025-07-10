package com.achcar_solutions.easycomm_processor.services;

import com.achcar_solutions.easycomm_core.infra.kafka.CertificateKafkaMessage;
import com.achcar_solutions.easycomm_core.repositories.CertificateRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CertificateConsumerService {

    private final CertificateRepository certificateRepository;

    public CertificateConsumerService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @KafkaListener(topics = "certificates-to-process", groupId = "certificate-processor")
    public void consumeCertificate(CertificateKafkaMessage kafkaMessage) {

    }
}
