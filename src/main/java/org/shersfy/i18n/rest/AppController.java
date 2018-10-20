package org.shersfy.i18n.rest;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.shersfy.i18n.I18nMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController extends BaseController{
    
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());
    
    @Value("${spring.application.name}")
    private String appname;
    
    @Autowired
    private I18nMessages i18n;
    
    @GetMapping("/index")
    public Object index() {
        return String.format("Welcom %s Application", appname);
    }
    
    @GetMapping("/i18n")
    public Object getI18n(@RequestParam(required=true)String lang, String key) {
    	Locale locale = Locale.CHINA;
    	if("en".equalsIgnoreCase(lang)
    			|| "en_US".equalsIgnoreCase(lang)) {
    		locale = Locale.US;
    	}
    	
    	if(StringUtils.isBlank(key)) {
    		return i18n.getI18n(locale.getLanguage(), locale.getCountry());
    	}
    	return i18n.getI18n(locale.getLanguage(), locale.getCountry()).getProperty(key);
    }
}
