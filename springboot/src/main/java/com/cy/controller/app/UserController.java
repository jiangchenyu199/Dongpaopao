package com.cy.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.entity.app.User;
import com.cy.service.app.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户接口")
public class UserController {

    private final UserService userService;

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    public R updateUserInfo(@RequestBody JSONObject jsonObject) {
        User user = jsonObject.getObject("user", User.class);
        return userService.updateUserInfo(user);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public R getUserInfo(@RequestParam("uid") String uId) {
        return userService.getUserInfo(uId);
    }

    /**
     * 获取用户余额
     *
     * @param uid 用户 ID
     * @return 用户余额
     */
    @GetMapping("/balance")
    @ApiOperation("获取用户余额")
    public R getBalance(@RequestParam("uid") String uid) {
        return userService.getBalance(uid);
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    @PostMapping("/avatar")
    @ApiOperation("上传头像")
    public R uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("uid") String uid) {
        return userService.uploadAvatar(file, uid);
    }

    @PutMapping("/recharge")
    @ApiOperation("充值")
    public R recharge(@RequestBody JSONObject params) {
        return userService.recharge(params);
    }

    @PutMapping("/withdraw")
    @ApiOperation("提现")
    public R withdraw(@RequestBody JSONObject params) {
        return userService.withdraw(params);
    }
}
