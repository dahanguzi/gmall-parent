package com.atguigu.gmall.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitmqService {

    /*
        三种类型的参数
        1、message封装的信息对象
        2、确切的类型比如map等
        3、channel
     */
    @RabbitListener(queues = "atguigu")
    public void receiveMsg(Map<String,Object> map){

        System.out.println("收到消息......"+map);
    }
}