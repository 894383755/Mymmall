package com.mmall.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

@Service("iuserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserMapper userMapper;
	

	public ServiceResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0){
			return ServiceResponse.creatByError("用户不存在");
		}
		//to do 对密码进行加密
		User user = userMapper.selectLogin(username, password);
		if(user == null){
			return ServiceResponse.creatByError("密码错误");
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServiceResponse.creatBySuccess("用户登录成功",user);
	}
	
	

}
