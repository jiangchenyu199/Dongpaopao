package com.cy.controller;

import com.cy.common.R;
import com.cy.config.OSSConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@RestController
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final S3Client s3Client;
    private final OSSConfig ossConfig;

    private static final String BUCKET = "pao";
    private static final String PREFIX_SPLASH = "splash/";

    @PostMapping
    public R upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择文件");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String key = PREFIX_SPLASH + UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            ensureBucketExists();
            byte[] bytes = file.getBytes();
            RequestBody body = RequestBody.fromBytes(bytes);
            PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET).key(key).build();
            s3Client.putObject(request, body);
            String base = ossConfig.getEndpoint();
            if (base != null && base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            String url = (base != null ? base : "http://localhost:9000") + "/" + BUCKET + "/" + key;
            return R.success(url);
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.createBucket(b -> b.bucket(BUCKET));
        } catch (Exception ignored) {
        }
    }
}
