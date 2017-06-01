package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;

@Controller
@RequestMapping("/car/")
public class CarController {
	@Autowired
	private ICartService iCartService;
	/**
	 * 增加购物车中商品
	 * @param session
	 * @param count 增加的数量
	 * @param productId 商品的id
	 * @return
	 * 未测试
	 */
	@RequestMapping("add.do")
	@ResponseBody
	public ServiceResponse add(HttpSession session,Integer count, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.add(user.getId(), productId, count);
	}
	/**
	 * 修改购物车中的商品的数量
	 * @param session
	 * @param count
	 * @param productId
	 * @return
	 * 未测试
	 */
	@RequestMapping("updata.do")
	@ResponseBody
	public ServiceResponse updata(HttpSession session,Integer count, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.add(user.getId(), productId, count);
	}
	/**
	 * 删除购物车中的商品
	 * @param session
	 * @param productIds 多个商品id之间用,分割
	 * @return
	 * 未测试
	 */
	@RequestMapping("deleteProduct.do")
	@ResponseBody
	public ServiceResponse deleteProduct(HttpSession session, String productIds){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.deleteProduct(user.getId(), productIds);
	}
	
	/**
	 * 查询购物车内容
	 * @param session
	 * @return
	 * 未测试
	 */
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse list(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.list(user.getId());
	}
	/**
	 * 全选商品
	 * @param session
	 * @return
	 * 未测试
	 */
	@RequestMapping("select_all.do")
	@ResponseBody
	public ServiceResponse selectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED,null);
	}
	/**
	 * 全反选
	 * @param session
	 * @return
	 * 未测试
	 */
	@RequestMapping("un_select_all.do")
	@ResponseBody
	public ServiceResponse unSelectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED,null);
	}
	/**
	 * 单个选
	 * @param session
	 * @param productId
	 * @return
	 * 未测试
	 */
	@RequestMapping("select.do")
	@ResponseBody
	public ServiceResponse Select(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, productId);
	}
	/**
	 * 单个不选
	 * @param session
	 * @param productId
	 * @return
	 * 未测试
	 */
	@RequestMapping("un_select.do")
	@ResponseBody
	public ServiceResponse unSelectAll(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, productId);
	}
	/**
	 * 得到购物车所有商品数量
	 * @param session
	 * @param productId
	 * @return
	 * 未测试
	 */
	@RequestMapping("get_cart_product_count.do")
	@ResponseBody
	public ServiceResponse getCartProductCount(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}else 
			return iCartService.getCartProductCount(user.getId());
	}
}
