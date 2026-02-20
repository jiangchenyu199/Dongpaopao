package com.cy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cy.entity.OrderType;
import com.cy.mapper.OrderTypeMapper;
import com.cy.service.OrderTypeService;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, OrderType> implements OrderTypeService {
}
