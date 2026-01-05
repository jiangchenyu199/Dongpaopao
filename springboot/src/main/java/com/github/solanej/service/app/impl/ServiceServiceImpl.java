package com.github.solanej.service.app.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.solanej.entity.app.Service;
import com.github.solanej.service.app.ServiceService;
import com.github.solanej.mapper.app.ServiceMapper;


@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {
}
