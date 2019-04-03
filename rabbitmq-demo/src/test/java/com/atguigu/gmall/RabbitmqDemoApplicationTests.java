package com.atguigu.gmall;

import com.atguigu.gmall.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqDemoApplicationTests {


	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void sendMsg(){

//		Map<String,Object> map = new HashMap<>();
//		map.put("lian",26);

		User user = new User("lianzhenjie",26,"男");

		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

		rabbitTemplate.convertAndSend("exchange.topic","atguigu.lian",user);

		System.out.println("消息发送完成......");
	}


	@Test
	public void contextLoads() {
	}

}
