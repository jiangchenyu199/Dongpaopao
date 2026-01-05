package com.github.solanej.mapper.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.app.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
