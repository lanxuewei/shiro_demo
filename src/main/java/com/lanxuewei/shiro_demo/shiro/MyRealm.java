/**
 * Copyright (c) 2017-2018 DeepWise All Rights Reserved.
 * http://www.deepwise.com
 */
package com.lanxuewei.shiro_demo.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lanxuewei Create in 2018/4/26 17:00
 * Description: 自定义 Realm 实现
 */
public class MyRealm implements Realm {

    private final static Logger logger = LoggerFactory.getLogger(MyRealm.class);

    @Override
    public String getName() {
        return "MyRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        //仅支持UsernamePasswordToken类型的Token
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();                //得到用户名
        String password = new String((char[]) token.getCredentials());  //得到密码
        if (!"zhang".equals(username)) {
            throw new UnknownAccountException();        //用户名错误
        }
        if (!"123".equals(password)) {
            throw new IncorrectCredentialsException();  //密码错误
        }
        //身份验证成功，返回一个AuthenticationInfo实现
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}