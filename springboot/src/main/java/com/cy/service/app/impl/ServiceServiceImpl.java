package com.cy.service.app.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cy.entity.app.Service;
import com.cy.service.app.ServiceService;
import com.cy.mapper.app.ServiceMapper;


@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {
}
