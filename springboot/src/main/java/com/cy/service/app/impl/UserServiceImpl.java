package com.cy.service.app.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.entity.app.User;
import com.cy.mapper.app.UserMapper;
import com.cy.service.app.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final S3Client s3Client;

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
        // 获取流
        RequestBody body = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        PutObjectResponse avatar = s3Client.putObject(builder ->
                builder.contentType("image/jpeg").bucket("avatar").key(uid), body);

        /* 当前版保存头像的策略是直接取文件名(uid)放入桶中，所以在数据库更新头像时直接放入[https://host:port/avatar/uid]即可 */
        String avatarUrl = "http://localhost:9000/" + "avatar/" + uid;

        final User user = new User();
        user.setUid(uid);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        return R.success(avatarUrl);
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
