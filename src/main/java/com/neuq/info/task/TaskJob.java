package com.neuq.info.task;

import com.neuq.info.dao.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Lin Dexiang
 * @date 2018/5/5
 */

@Slf4j
@Component("taskJob")
public class TaskJob {

    int i=0;

    @Autowired
    private OrderDao orderDao;

//    //每分钟扫描一次
//    @Scheduled(cron = "0/5 * *  * * ?")
//    public void job1() {
//        orderDao.cancelUnPayOrder(new Date());
//
//    }
}
