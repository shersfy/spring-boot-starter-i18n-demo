package org.shersfy.i18n.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson.JSONObject;

@ControllerAdvice(basePackages="org.shersfy.i18n.rest")
public class BaseController implements ResponseBodyAdvice<Object>{

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	
	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();
	
	@ModelAttribute
	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		THREAD_LOCAL_REQUEST.set(request);
		THREAD_LOCAL_RESPONSE.set(response);
	}

	public HttpServletRequest getRequest() {
		return THREAD_LOCAL_REQUEST.get();
	}

	public HttpServletResponse getResponse() {
		return THREAD_LOCAL_RESPONSE.get();
	}
	
	protected String toJson(Object obj) {
		return JSONObject.toJSONString(obj);
	}

	public String errors(List<ObjectError> list) {
		StringBuffer msg = new StringBuffer(0);
		if(list!=null){
			for(ObjectError err: list){

				String code		 = err.getCode();
				String name		= err.getObjectName();
				Object[] args	= err.getArguments();
				Object rejected = null;
				
				List<Object> argList = new ArrayList<>();
				if(err instanceof FieldError){
					FieldError fErr = (FieldError) err;
					name = fErr.getField();
					code = fErr.getDefaultMessage();
					rejected = fErr.getRejectedValue();
				}

				argList.add(name);
				if(args!=null){
					for(Object arg :args){
						if(arg instanceof DefaultMessageSourceResolvable){
							continue;
						}
						if(arg instanceof Boolean
								&& (DecimalMin.class.getSimpleName().equals(code)
							       || DecimalMax.class.getSimpleName().equals(code))){
							continue;
						}
						argList.add(arg);
					}
				}
				
				if(rejected!=null){
					argList.add(rejected);
				}
				
				msg.append(String.format("%s: %s", name, err.getDefaultMessage())).append(";");
			}
		}
		return msg.toString();
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		LOGGER.info("\n{}\n", body);
		return body;
	}

}
