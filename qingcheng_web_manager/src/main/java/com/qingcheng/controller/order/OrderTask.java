package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderTask {

    /*@Scheduled(cron = "* * * * * ?")
    public void orderTimeOutLogic()
    {
        System.out.println(new Date());
    }*/

    @Reference
    private CategoryReportService categoryReportService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void createCategoryReportDate()
    {
        System.out.println("**************************");
        categoryReportService.createData();
    }
}
