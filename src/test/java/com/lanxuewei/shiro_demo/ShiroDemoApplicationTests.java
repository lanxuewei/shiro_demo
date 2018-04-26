package com.lanxuewei.shiro_demo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroDemoApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(ShiroDemoApplicationTests.class);

	@Test
	public void contextLoads() {
	}

    /**
     * 退出时请解除绑定Subject从当前线程中解绑Subject
     */
    @After
    public void tearDown() {
        ThreadContext.unbindSubject();
    }

	/**
	 * 用户登录通用操作
	 */
	@Test
	public void testHelloworld() {
		//1.获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager  realm可自定义
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm.ini");
		//2.得到SecurityManager实例 并绑定给SecurityUtils
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		//3.得到Subject及创建用户名/密码身份验证Token
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		try {
			//4.登录，即身份验证
			logger.info("zhang ----> login");
			subject.login(token);
			logger.info("token = {}", token);
		} catch (AuthenticationException e) {
			//5.身份验证失败
		}
		Assert.assertEquals(true, subject.isAuthenticated());  //断言用户已经登录
		//6.退出
		subject.logout();
	}

    /**
     * 封装login操作
     */
    public void login(String configFile,String username,String password){
        //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<org.apache.shiro.mgt.SecurityManager> factory=
                new IniSecurityManagerFactory(configFile);

        //2、得到SecurityManager实例，并绑定给SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager=factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        //3、得到Subject
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);
    }

    /**
     * 角色相关权限认证
     */
	@Test
    public void testHasRole() {
        login("classpath:shiro-role.ini", "zhang", "123");
        //判断拥有角色: role1
        Assert.assertTrue(SecurityUtils.getSubject().hasRole("role1"));
        //判断拥有角色role1 and role2
        Assert.assertTrue(SecurityUtils.getSubject().hasAllRoles(Arrays.asList("role1", "role2")));
        //判断拥有角色role1、role2、role3分别对含有这里的每一个role进行判断
        /*boolean[] result = SecurityUtils.getSubject().hasRoles(Arrays.asList("role1", "role2", "role3"));
        Assert.assertTrue(result[0]);
        Assert.assertTrue(result[1]);
        Assert.assertTrue(result[2]);*/
    }

    /**
     * 用户角色权限认证
     */
    public void testIsPermitted() {
        login("classpath:shiro-permission.ini", "zhang", "123");
        //判断拥有权限：user:create
        Assert.assertTrue(SecurityUtils.getSubject().isPermitted("user:create"));
        //判断拥有权限：user:update and user:delete
        Assert.assertTrue(SecurityUtils.getSubject().isPermittedAll("user:update", "user:delete"));
        //判断没有权限：user:view
        Assert.assertFalse(SecurityUtils.getSubject().isPermitted("user:view"));
    }

}

