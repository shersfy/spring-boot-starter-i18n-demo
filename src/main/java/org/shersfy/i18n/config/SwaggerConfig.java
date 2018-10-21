package org.shersfy.i18n.config;

import java.util.ArrayList;
import java.util.List;

import org.shersfy.i18n.rest.BaseController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {

		ApiInfo info = new ApiInfoBuilder()
				//页面标题
				.title("Swagger")
				//创建人
				//         .contact("admin")
				//版本号
				.version("1.0")
				//描述
				.description("Swagger UI API")
				.build();

		ParameterBuilder builder = new ParameterBuilder()
				.parameterType("header") 
				.modelRef(new ModelRef("string"))
				.name("lang")
				.defaultValue("zh_CN")
				.required(false)
				.description("i18n language");

		List<Parameter> params = new ArrayList<>();
		params.add(builder.build());

		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(info)
				.globalOperationParameters(params)
				.select()
				//为当前包路径
				.apis(RequestHandlerSelectors.basePackage(BaseController.class.getPackage().getName()))
				.paths(PathSelectors.any())
				.build();
	}
}
