package com.github.solanej.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.solanej.entity.Service;
import com.github.solanej.service.ServiceService;
import com.github.solanej.mapper.ServiceMapper;


@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {
}
