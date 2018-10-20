package org.shersfy.i18n.rest;

import org.shersfy.i18n.I18nMessages;
import org.shersfy.i18n.I18nModel;
import org.shersfy.i18n.beans.Result;
import org.shersfy.i18n.beans.Result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice("org.shersfy.i18n.rest")
public class I18nResponseAdvice implements ResponseBodyAdvice<Result>{

	@Autowired
	private I18nMessages i18n;

	@Override
	public boolean supports(MethodParameter returnType, 
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Result beforeBodyWrite(Result res, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
					ServerHttpResponse response) {
		
		if(res.getCode()!=ResultCode.SUCESS) {
			I18nModel model = (I18nModel) res.getModel();
			String lang = request.getHeaders().getFirst("lang");
			String msg  = i18n.getI18n(lang).getProperty(model.getKey(), model.getArgs());
			res.setMsg(msg);
		}
		
		return res;
	}
	
}
