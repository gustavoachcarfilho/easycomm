package com.achcar_solutions.easycomm_core.entities.certificate;

import com.mongodb.lang.Nullable;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "certificates")
public class Certificate {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    private String title;
    private CertificateCategory category;
    private Integer durationInHours;
    private LocalDate expirationDate;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private String s3ObjectKey;
    private CertificateStatus status;
    @Nullable
    private String validator_id;
    @Nullable
    private LocalDateTime validationTimestamp;
    @Nullable
    private String rejectionReason;
}
