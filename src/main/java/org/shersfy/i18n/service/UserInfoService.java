package org.shersfy.i18n.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.shersfy.i18n.filter.AesUtil;
import org.shersfy.i18n.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class UserInfoService {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public String refreshToken(String token) {
		
		if (StringUtils.isBlank(token)) {
			return null;
		}
		
		// 未过期
		if (stringRedisTemplate.hasKey(token)) {
			return token;
		}

		// 刷新token
		try {
			User user = JSON.parseObject(AesUtil.decryptHexStr(token, AesUtil.AES_SEED), User.class);
			String refresh = "_r_"+user.getId();
			if (!stringRedisTemplate.hasKey(refresh)) {
				return null;
			}
			// token不一致
			if (!token.equals(stringRedisTemplate.opsForValue().get(refresh))) {
				logger.info("被盗token不一致");
				stringRedisTemplate.delete(refresh);
				return null;
			}
			
			user.setTimestamp(System.currentTimeMillis());
			token = AesUtil.encryptHexStr(user.toString(), AesUtil.AES_SEED);
			stringRedisTemplate.opsForValue().set(token, user.toString(), 10, TimeUnit.SECONDS);
			Long expire = stringRedisTemplate.getExpire(refresh);
			stringRedisTemplate.opsForValue().set(refresh, token, expire, TimeUnit.SECONDS);
			logger.info("刷新token={}", token);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return token;
	}

	public void cacheToken(String token, String refresh, User user) {
		stringRedisTemplate.opsForValue().set(token, user.toString(), 10, TimeUnit.SECONDS);
		stringRedisTemplate.opsForValue().set(refresh, token, 120, TimeUnit.SECONDS);
	}

}
