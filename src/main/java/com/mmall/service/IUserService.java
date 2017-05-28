package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

public interface IUserService {
	ServiceResponse<User> login(String username, String password);
	
	ServiceResponse<String> register(User user);
	
	ServiceResponse<String> checkValid(String str, String type); 
}
