package com.mmall.service.Impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;


@Service("iuserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserMapper userMapper;
	

	public ServiceResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0){
			return ServiceResponse.creatByError("用户不存在");
		}
		User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
		if(user == null){
			return ServiceResponse.creatByError("密码错误");
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServiceResponse.creatBySuccess("用户登录成功",user);
	}


	public ServiceResponse<String> register(User user) {
		//检查账号
		ServiceResponse<String> response = this.checkValid(user.getUsername(), Const.USERNAME);
		if(response.isSuccess() == false){
			return response;
		}
		//检查邮箱
		response = this.checkValid(user.getUsername(), Const.EMAIL);
		if(response.isSuccess() == false){
			return response;
		}
		//注册管理类型
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//对密码进行md5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		int resultCount = userMapper.insert(user);
		if(resultCount == 0){
			return ServiceResponse.creatByError("创建账号失败");
		}
		return ServiceResponse.creatBySuccess("创建成功");
	}


	public ServiceResponse<String> checkValid(String str, String type) {
		if(StringUtils.isNotBlank(type) == false){
			return ServiceResponse.creatByError("输入为空或错误");
		}
		if(Const.USERNAME.equals(type)){
			if(userMapper.checkUsername(str) > 0)
				return ServiceResponse.creatByError("用户名已存在");
		}else if(Const.EMAIL.equals(type)){
			if(userMapper.checkEmail(str) > 0)
				return ServiceResponse.creatByError("邮箱已被注册");
		}
		
		return ServiceResponse.creatBySuccess("未存在");
	}
	
	

}
