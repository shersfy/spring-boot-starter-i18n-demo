package org.shersfy.i18n.config;

import org.shersfy.i18n.filter.SsoFilter;
import org.shersfy.i18n.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Bean
	public FilterRegistrationBean<SsoFilter> ssoFilter(){
		FilterRegistrationBean<SsoFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new SsoFilter(userInfoService));
		bean.addUrlPatterns("/*");
		bean.setOrder(1);
		return bean;
	}

}
