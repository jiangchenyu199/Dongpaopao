package com.github.solanej.mapper.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.app.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
