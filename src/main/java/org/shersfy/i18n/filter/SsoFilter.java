package org.shersfy.i18n.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.shersfy.i18n.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SsoFilter implements Filter {

	Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfoService userInfoService;

	public SsoFilter(UserInfoService userInfoService) {
		super();
		this.userInfoService = userInfoService;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		res.setContentType("text/html;charset=UTF-8");
		if (req.getRequestURI().toString().equals("/login")) {
			chain.doFilter(request, response);
			return;
		}
		if (isLogined(req, res)) {
			logger.info("已登录, token={}", req.getAttribute("token"));
			res.getWriter().write(String.format("已登录,id=%s<p/> token=%s", req.getAttribute("refresh"),
					req.getAttribute("token")));
			return;
		}

		logger.info("未登陆");
		res.getWriter().write("未登陆");
	}

	@Override
	public void destroy() {

	}

	public boolean isLogined(HttpServletRequest req, HttpServletResponse res) {
		
		String oldToken = CookieUtil.getCookieValue(req, "_t_");
		String refresh  = CookieUtil.getCookieValue(req, "_r_");

		String token = userInfoService.refreshToken(oldToken);
		if (StringUtils.isBlank(token)) {
			return false;
		}
		req.setAttribute("token", token);
		req.setAttribute("refresh", refresh);
		if (token.equals(oldToken)) {
			return true;
		}
		
		// cookie过期时间设置大于缓存过期时间
		Cookie cookie = new Cookie("_t_", token);
		cookie.setDomain(req.getServerName());
		cookie.setPath("/");
		cookie.setMaxAge(1800);
		CookieUtil.addCookie(res, cookie);
		
		return true;
	}

}
