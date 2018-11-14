package com.bird.service.zero;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.bird.core.utils.SpringContextHolder;
import com.bird.service.zero.model.Organization;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bird")
@MapperScan("com.bird.service.zero.mapper")
@DubboComponentScan(basePackages = "com.bird.service.zero")
public class Bootstrap {

	public static void main(String[] args) {
		SpringApplication.run(Bootstrap.class, args);

		System.out.println("=================================");
		System.out.println("[ZERO服务]启动完成!!!");
		System.out.println("=================================");

		OrganizationService service = SpringContextHolder.getBean(OrganizationService.class);
		Organization organization = service.queryById(1L);
		System.out.println("=================================");
	}
}
