package com.mmall.controller.portal;

import java.awt.AWTError;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;


@Controller
@RequestMapping("/shipping/")
public class ShippingController {
	@Autowired
	private IShippingService iShippingServiec;
	/**
	 * 增加购物地址
	 * @param session
	 * @param shipping
	 * @return
	 * 
	 */
	@RequestMapping("add.do")
	@ResponseBody
	public ServiceResponse add(HttpSession session, Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iShippingServiec.add(user.getId(), shipping);
	}
	/**
	 * 删除购物地址
	 * @param session
	 * @param shippingId
	 * @return
	 * 
	 */
	@RequestMapping("del.do")
	@ResponseBody
	public ServiceResponse del(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iShippingServiec.del(user.getId(), shippingId);
	}
	/**
	 * 更新购物地址
	 * @param session
	 * @param shipping
	 * @return
	 * 
	 */
	@RequestMapping("updata.do")
	@ResponseBody
	public ServiceResponse updata(HttpSession session, Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iShippingServiec.updata(user.getId(), shipping);
	}
	/**
	 * 获得某个购物地址
	 * @param session
	 * @param shippingId
	 * @return
	 * 
	 */
	@RequestMapping("select.do")
	@ResponseBody
	public ServiceResponse select(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iShippingServiec.select(user.getId(), shippingId);
	}
	/**
	 * 获得该用户所有的购物地址
	 * @param pageNum
	 * @param pageSize
	 * @param session
	 * @return
	 * 
	 */
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum, @RequestParam(value="pageSize",defaultValue="10")int pageSize,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iShippingServiec.list(user.getId(), pageNum, pageSize);
	}
}
