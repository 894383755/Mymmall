package com.mmall.controller.portal;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.Impl.OrderServiceImpl;

@Controller
@RequestMapping("/order/")
public class OrderController {
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 支付入口
	 * @param session
	 * @param request
	 * @param orderNo
	 * @return
	 */
	@RequestMapping("pay.do")
	@ResponseBody
	public ServiceResponse pay(HttpSession session, HttpServletRequest request, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		String path = request.getSession().getServletContext().getRealPath("upload");
		return iOrderService.pay(user.getId(), orderNo, path);
	}
}
