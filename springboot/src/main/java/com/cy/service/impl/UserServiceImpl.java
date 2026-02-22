package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.config.S3Config;
import com.cy.constant.StorageConstant;
import com.cy.entity.User;
import com.cy.mapper.UserMapper;
import com.cy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final S3Client s3Client;
    private final S3Config s3Config;

    @Override
    public R updateUserInfo(User user) {
        userMapper.updateById(user);
        return R.success();
    }

    @Override
    public R getUserInfo(String uId) {
        User user = userMapper.selectById(uId);
        return R.success(user);
    }

    @Override
    public R getBalance(String uid) {
        User user = userMapper.selectById(uid);
        return R.success(user.getBalance());
    }

    @Override
    @SneakyThrows
    public R uploadAvatar(MultipartFile file, String uid) {
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String key = StorageConstant.FOLDER_AVATAR + "/" + uid + ext;

        ensureBucketExists();
        RequestBody body = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(builder ->
                builder.contentType("image/jpeg").bucket(StorageConstant.BUCKET).key(key), body);

        String base = s3Config.getEndpoint();
        if (base != null && base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String avatarUrl = (StringUtils.hasText(base) ? base : StorageConstant.DEFAULT_ENDPOINT) + "/" + StorageConstant.BUCKET + "/" + key;

        User user = new User();
        user.setUid(uid);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        return R.success(avatarUrl);
    }

    private void ensureBucketExists() {
        try {
            s3Client.createBucket(b -> b.bucket(StorageConstant.BUCKET));
        } catch (Exception ignored) {
        }
    }

    @Override
    public R recharge(JSONObject params) {
        String uid = params.getString("uid");
        BigDecimal amount = params.getBigDecimal("amount");
        User user = userMapper.selectById(uid);
        user.setBalance(user.getBalance().add(amount));
        userMapper.updateById(user);
        return R.success();
    }

    @Override
    public R withdraw(JSONObject params) {
        String uid = params.getString("uid");
        BigDecimal amount = params.getBigDecimal("amount");
        User user = userMapper.selectById(uid);
        if (user.getBalance().compareTo(amount) < 0) {
            return R.error("余额不足");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userMapper.updateById(user);
        return R.success();
    }
}
