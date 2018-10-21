# spring-boot-starter-i18n-demo
spring-boot-starter-i18n-demo
### 1. 添加依赖
```
<dependency>
	<groupId>org.shersfy</groupId>
	<artifactId>spring-boot-starter-i18n</artifactId>
	<version>1.0.0.RELEASE</version>
</dependency>

```
### 2. 修改application.yml配置
```
# i18n
i18n:
  config:
    enabled: true
    
```
### 3. 在classpath下添加i18n/messages_*.properties
```
英文：messages_en_US.properties
中文：messages_zh_CN.properties

```
### 4. 指定Controller所在package
```
@ControllerAdvice("org.shersfy.i18n.rest")
public class I18nConfig extends I18nResponseAdvice {
	
}
```
### 5. 封装Result对象
例：
```
MSGT0027E000002=Save Job Error: The job is out of date cron=%s, effective time %s ~ %s

@GetMapping("/msg")
public Result getMsg() {
	Result res = new Result();
	
	I18nModel i18n = new I18nModel("MSGT0027E000002");
	String args[] = {"0 /1 * * * ?", "2018-10-20 00:00:00", "2050-12-31 23:59:59"};
	i18n.setArgs(args);
	
	res.setModel(i18n);
	return res;
}

```