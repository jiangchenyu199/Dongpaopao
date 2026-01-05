package com.cy.service.app.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cy.entity.app.OrderType;
import com.cy.mapper.app.OrderTypeMapper;
import com.cy.service.app.OrderTypeService;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, OrderType> implements OrderTypeService {
}