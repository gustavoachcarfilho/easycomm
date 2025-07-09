package com.achcar_solutions.easycomm.infra.ports;

public interface StoragePort {
    String uploadFile(byte[] fileData, String fileName, String contentType);
}
