package com.mmall.service.Impl;

import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;


@Service("iuserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserMapper userMapper;
	
	@Override
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

	@Override
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

	@Override
	public ServiceResponse<String> checkValid(String str, String type) {
		if(StringUtils.isBlank(type) ){
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

	@Override
	public ServiceResponse<String> selectQuestion(String username) {
		ServiceResponse<String> response = this.checkValid(username, Const.USERNAME);
		if(response.isSuccess()){
			return ServiceResponse.creatByError("用户不存在");
		}
		String question = userMapper.selectQuestionByUsername(username);
		if(StringUtils.isNotBlank(question) == false){
			return ServiceResponse.creatByError("找回密码问题是空的");
		}
		return ServiceResponse.creatBySuccess(question);
	}

	@Override
	public ServiceResponse<String> checkAnswer(String username, String question, String answer) {
		int response = userMapper.checkAnswer(username, question, answer);
		if(response <= 0){
			return ServiceResponse.creatByError("问题答案错误");
		}
		String forgetToken = UUID.randomUUID().toString();
		TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
		return ServiceResponse.creatBySuccess(forgetToken);
	}
	
	@Override
	public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
		if(StringUtils.isBlank(username)){
			return ServiceResponse.creatByError("账号不为空");
		}
		if(this.checkValid(username, Const.USERNAME).isSuccess()){
			return ServiceResponse.creatByError("账号错误");
		}
		if(StringUtils.isBlank(forgetToken)){
			return ServiceResponse.creatByError("token错误");
		}
		if(StringUtils.equals(forgetToken, TokenCache.getKey(TokenCache.TOKEN_PREFIX + username)) == false){
			return ServiceResponse.creatByError("账号错误，或者token失效");
		}
		if(userMapper.updatePasswordByUsername(username, MD5Util.MD5EncodeUtf8(passwordNew)) <= 0){
			return ServiceResponse.creatByError("更改失败");
		}
		return ServiceResponse.creatBySuccess("更新成功");
	}


	@Override
	public ServiceResponse<String> resetPasswrod(User user, String passwordOld, String passwordNew) {
		if(userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId()) <=0){
			return ServiceResponse.creatByError("旧密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		if(userMapper.updateByPrimaryKeySelective(user) <= 0){
			return ServiceResponse.creatByError("更新失败");
		}
		return ServiceResponse.creatBySuccess("更新成功");
	}

	@Override
	public ServiceResponse<User> updataInformation(User user) {
		if(userMapper.checkEmail(user.getEmail()) > 0){
			return ServiceResponse.creatByError("email已经存在");
		}
		User updataUser = new User();
		updataUser.setId(user.getId());
		updataUser.setEmail(user.getEmail());
		updataUser.setPhone(user.getPhone());
		updataUser.setQuestion(user.getQuestion());
		updataUser.setAnswer(user.getAnswer());
		if(userMapper.updateByPrimaryKeySelective(updataUser) <= 0){
			return ServiceResponse.creatByError("更新失败");
		}
		return ServiceResponse.creatBySuccess("更新成功",updataUser);
	}

	@Override
	public ServiceResponse<User> checkAdminRole(User user) {
		if(user == null || user.getRole().intValue() == Const.Role.ROLE_CUSTOMER){
			return ServiceResponse.creatByError("错误，不是管理员");
		}
		return ServiceResponse.creatBySuccess("是管理员");
	}
}
	
	