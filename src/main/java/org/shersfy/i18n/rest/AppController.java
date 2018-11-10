package org.shersfy.i18n.rest;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.shersfy.i18n.I18nMessages;
import org.shersfy.i18n.I18nModel;
import org.shersfy.i18n.beans.Result;
import org.shersfy.i18n.filter.AesUtil;
import org.shersfy.i18n.filter.CookieUtil;
import org.shersfy.i18n.model.User;
import org.shersfy.i18n.service.UserInfoService;
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

	@Autowired
	private UserInfoService userInfoService;

	@GetMapping("/index")
	public Object index() {
		return String.format("Welcom %s Application", appname);
	}

	@GetMapping("/login")
	public Result login(String username, String password) {

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setId(1L);
		user.setTimestamp(System.currentTimeMillis());

		String token = AesUtil.encryptHexStr(user.toString(), AesUtil.AES_SEED);
		String refresh = "_r_"+user.getId();
		
		userInfoService.cacheToken(token, refresh, user);

		// 刷新token, 清除and添加到cookie
		CookieUtil.clearCookie(getRequest(), getResponse());
		Cookie cookie = new Cookie("_t_", token);
		cookie.setDomain(getRequest().getServerName());
		cookie.setPath("/");
		cookie.setMaxAge(1800);
		CookieUtil.addCookie(getResponse(), cookie);
		Cookie r = new Cookie("_r_", refresh);
		r.setDomain(getRequest().getServerName());
		r.setPath("/");
		r.setMaxAge(1800);
		CookieUtil.addCookie(getResponse(), r);

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

		I18nModel i18n = new I18nModel("MSGT0027E000002");
		String args[] = {"0 /1 * * * ?", "2018-10-20 00:00:00", "2050-12-31 23:59:59"};
		i18n.setArgs(args);

		res.setModel(i18n);
		return res;
	}

}
