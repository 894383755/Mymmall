package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	/**
	 * 商品新增，修改
	 * @param session
	 * @param product
	 * @return
	 * 未测试
	 */
	@RequestMapping("save.do")
	@ResponseBody
	public ServiceResponse ProductSave(HttpSession session, Product product){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.saveOrUpdataProduct(product);
	}
	/**
	 * 商品状态的修改
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 * 未测试
	 */
	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.setSaleStatus(productId, status);
	}
	/**
	 * 获取产品详情
	 * @param session
	 * @param productId
	 * @return
	 * 未测试
	 */
	@RequestMapping("detail.do")
	@ResponseBody
	public ServiceResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.manageProductDetail(productId);
	}
}
