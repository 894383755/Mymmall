package com.mmall.controller.backend;

import java.util.List;

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
@RequestMapping("/manage/category/")
public class CategoryManageController {
	
	@Autowired
	private ICategoryService iCategoryService;
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 创建一个新的商品分类
	 * @param session
	 * @param categoryName
	 * @param categoryId
	 * @return
	 * 
	 */
	@RequestMapping("add_categroy.do")
	@ResponseBody
	public ServiceResponse<Category> addCategory(HttpSession session, String categoryName,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.addCategory(categoryName,categoryId);
	}
	
	/**
	 * 修改商品类的名字
	 * @param session
	 * @param categoryName
	 * @param categoryId
	 * @return
	 * 
	 */
	@RequestMapping("set_categroy_name.do")
	@ResponseBody
	public ServiceResponse<Category> setCategroyName(HttpSession session, String categoryName, Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.updataCategoryName(categoryName, categoryId);
	}
	
	/**
	 * 查询同级节点
	 * @param session
	 * @param categoryId
	 * @return
	 * 
	 */
	@RequestMapping("get_category.do")
	@ResponseBody
	public ServiceResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.getChildrenParallelCategory(categoryId);
	}
	
	/**
	 * 查询本节点和孩子节点的id
	 * @param session
	 * @param categoryId
	 * @return
	 * 
	 */
	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServiceResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess() == false){
			return ServiceResponse.creatByError("无权限管理"); 
		}
		return iCategoryService.selectCategoryAndDeepChildrenCategory(categoryId);
	}
}
