package com.achcar_solutions.easycomm.infra.kafka;

public record CertificateKafkaMessage(String certificateId, String s3ObjectKey) {
}
