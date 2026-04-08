package com.beautyscheduler.application.port.out;

import java.io.InputStream;

public interface StoragePort {
    String upload(InputStream inputStream, String originalFilename, String folder);
    void delete(String publicId);
}
