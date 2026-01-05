package com.cy.controller.app;

import com.cy.common.R;
import com.cy.entity.app.Service;
import com.cy.service.app.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 服务管理
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping("/save")
    public R save() {
        Service service = new Service();
        service.setIcon("🎂");
        service.setBgColor("#AAAAAA");
        service.setEnabled(true);
        serviceService.saveOrUpdate(service);
        return R.success();
    }

    @GetMapping("/detail")
    public R detail(@RequestParam("serviceId") String serviceId) {
        return R.success(serviceService.getById(serviceId).getForm());
    }

    @GetMapping("/list")
    public R list() {
        return R.success(serviceService.list());
    }
}
