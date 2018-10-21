package org.shersfy.i18n.rest;

import org.apache.commons.lang3.StringUtils;
import org.shersfy.i18n.I18nMessages;
import org.shersfy.i18n.I18nModel;
import org.shersfy.i18n.beans.Result;
import org.shersfy.i18n.beans.Result.ResultCode;
import org.shersfy.i18n.model.User;
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
    
    @GetMapping("/login")
    public Result login(String username, String password) {
    	
    	User user = new User();
    	user.setUsername(username);
    	user.setPassword(password);
    	getRequest().getSession().setAttribute("user", user);
    	
    	return new Result(user);
    }
    
    @GetMapping("/user")
    public Result getLoginuser() {
    	Object user = getRequest().getSession().getAttribute("user");
    	return user==null?new Result("not login"):new Result(user);
    }
    
    @GetMapping("/i18n")
    public Object getI18n(@RequestParam(required=true)String lang, String key) {
    	if(StringUtils.isBlank(key)) {
    		return i18n.getI18n(lang);
    	}
    	return i18n.getI18n(lang).getProperty(key);
    }
    
    
    @GetMapping("/msg")
    public Result getMsg() {
    	Result res = new Result();
    	
    	I18nModel msg = new I18nModel("MSGT0027E000002");
    	String args[] = {"0 /1 * * * ?", "2018-10-20 00:00:00", "2050-12-31 23:59:59"};
    	msg.setArgs(args);
    	
    	res.setCode(ResultCode.FAIL);
    	res.setModel(msg);
    	
    	return res;
    }
    
}
