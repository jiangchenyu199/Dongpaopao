package com.cy.controller;

import com.cy.common.R;
import com.cy.config.S3Config;
import com.cy.constant.StorageConstant;
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
    private final S3Config s3Config;

    @PostMapping
    public R upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = StorageConstant.FOLDER_SPLASH) String folder) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择文件");
        }
        if (!StorageConstant.FOLDER_SPLASH.equals(folder) && !StorageConstant.FOLDER_BUSINESS_PROMOTION.equals(folder)) {
            folder = StorageConstant.FOLDER_SPLASH;
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String key = folder + "/" + UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            ensureBucketExists();
            byte[] bytes = file.getBytes();
            RequestBody body = RequestBody.fromBytes(bytes);
            PutObjectRequest request = PutObjectRequest.builder().bucket(StorageConstant.BUCKET).key(key).build();
            s3Client.putObject(request, body);
            String base = s3Config.getEndpoint();
            if (base != null && base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            String url = (base != null ? base : StorageConstant.DEFAULT_ENDPOINT) + "/" + StorageConstant.BUCKET + "/" + key;
            return R.success(url);
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    private void ensureBucketExists() {
        try {
            s3Client.createBucket(b -> b.bucket(StorageConstant.BUCKET));
        } catch (Exception ignored) {
        }
    }
}
