package com.oak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter{
	
	@Bean(name="multipartResolver")
	public CommonsMultipartResolver multipartResolver(){
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(10485760);
		return commonsMultipartResolver;
	}
	
	/*@Bean
	public AuthenticationInterceptor authenticationInterceptor() {
	    return new AuthenticationInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(authenticationInterceptor());
	}*/
	
	/*@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(new AuthenticationInterceptor());	    
	}*/
}
