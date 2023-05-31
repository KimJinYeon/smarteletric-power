package com.kapple.smarteletric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// SpringBootApplication 어노테이션을 기능적으로 보면 3가지 어노테이션이 합쳐진 것이라고 볼 수 있다.
// 1. @SpringBootConfiguration , 2. @EnableAutoConfiguration, 3. @ComponentScan
// 스프링 부트 애플리케이션이 실행되면 @ComponetScan 어노테이션이 @Component 시리즈 어노테이션이 붙은 클래스를 발견해 빈(bean)을 등록한다.
// 여기서 @Component 시리즈 어노테이션은 @Controller @RestController @Service @Repository @Configuration등을 의미한다.
@EnableScheduling
@SpringBootApplication
public class SmarteletricApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarteletricApplication.class, args);
	}

}
