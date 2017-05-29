package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
	
	@Autowired
	private ICategoryService iCategoryService;
	@Autowired
	private IUserService iUserService;
	
	@RequestMapping("add_categroy.do")
	@ResponseBody
	public ServiceResponse<Category> addCategory(HttpSession session, String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.addCategory(categoryName,parentId);
	}
	
	@RequestMapping("set_categroy_name.do")
	@ResponseBody
	public ServiceResponse<Category> setCategroyName(HttpSession session, String categoryName, Integer parentId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.addCategory(categoryName,parentId);
	}
}
