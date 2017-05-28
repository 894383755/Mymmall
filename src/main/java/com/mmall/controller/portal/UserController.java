package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;


@Controller
@RequestMapping("/user/")
public class UserController {
	
	@Autowired
	private IUserService iUserService;
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<User> login(String username, String password, HttpSession session){
		ServiceResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess())
			session.setAttribute(Const.CURRENT_USER, response.getData());
		 return response;
	}
	/**
	 * 登出接口 
	 * @param session
	 * @return
	 * 
	 */
	@RequestMapping(value = "logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<User> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServiceResponse.creatBySuccess();
	}
	
	/**
	 * 注册功能
	 * @param user
	 * @param session
	 * @return
	 * 
	 */
	@RequestMapping(value = "register.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> register(User user,HttpSession session){
		return iUserService.register(user);
	}
	
	/**
	 * 验证部分数据是否存在
	 * @param str 需要验证的内容
	 * @param type 需要验证了类型
	 * @return 
	 * 
	 */
	@RequestMapping(value = "check_Valid.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> checkValid(String str, String type){
		return iUserService.checkValid(str, type);
	}
	
	/**
	 * 获取用户信息
	 * @param session
	 * @return
	 * 
	 */
	@RequestMapping(value = "get_User_Info.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<User> getUserInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("用户未登录");
		}
		return ServiceResponse.creatBySuccess(user);
	}
	
	/**
	 * 忘记密码，得到问题
	 * @param username
	 * @return
	 * 
	 */
	@RequestMapping(value = "forget_Get_Question.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> forgetGetQuestion(String username){
		return iUserService.selectQuestion(username);
	}
	
	/**
	 * 忘记密码，验证回答
	 * @param username
	 * @param question
	 * @param answer
	 * @return
	 * 
	 */
	@RequestMapping(value = "forget_Check_Answer.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer){
		return iUserService.checkAnswer(username, question, answer);
	}
	/**
	 * 忘记密码，修改密码
	 * @param username
	 * @param password
	 * @param forgetToken
	 * @return
	 * 
	 */
	@RequestMapping(value = "forget_Reset_Password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
		return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
	}
	
	/**
	 * 修改密码（登录状态）
	 * @param session
	 * @param passwordOld
	 * @param passwordNew
	 * @return
	 * 
	 */
	@RequestMapping(value = "reset_Password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iUserService.resetPasswrod(user, passwordOld, passwordNew);
	}
	
	/**
	 * 修改个人信息，信箱需要验证
	 * @param session
	 * @param user
	 * @return
	 *
	 */
	@RequestMapping(value = "updata_Information.do", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse<User> updataInformation(HttpSession session, User user){
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServiceResponse.creatByError("未登录");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServiceResponse<User> response = iUserService.updataInformation(user);
		if(response.isSuccess()){
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		return  response;
	}
}
