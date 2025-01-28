package com.sy.sypicture;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * @author 诺诺
 * 结束DDD重构
 */
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@EnableAsync
@MapperScan("com.sy.sypicture.infrastructure.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SyPictureBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyPictureBackendApplication.class, args);
	}

}
