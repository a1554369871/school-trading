package com.trading.dto;

import lombok.Data;

@Data
public class UploadResult {
    private String url;
    private String filename;
    private Long size;

    public UploadResult(String url, String filename, Long size) {
        this.url = url;
        this.filename = filename;
        this.size = size;
    }
}
