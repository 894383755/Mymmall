package com.mmall.controller.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.Impl.OrderServiceImpl;

@Controller
@RequestMapping("/order/")
public class OrderController {
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 创建订单
	 * @param session
	 * @param shippingId
	 * @return
	 * 
	 */ 
	@RequestMapping("create.do")
	@ResponseBody
	public ServiceResponse create(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.createOrder(user.getId(), shippingId);
	}
	
	/**
	 * 取消订单
	 * @param session
	 * @param shippingId
	 * @return
	 * 
	 */
	@RequestMapping("cancel.do")
	@ResponseBody
	public ServiceResponse cancel(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.cancel(user.getId(), orderNo);
	}
	
	/**
	 * 获得已经选中的购物详情
	 * @param session
	 * @param shippingId
	 * @return
	 * 
	 */
	@RequestMapping("get_order_cart_product.do")
	@ResponseBody
	public ServiceResponse getOrderCartProduct(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.getOrderCartProduct(user.getId());
	}
	/**
	 * 用户查询订单详细
	 * @param session
	 * @param orderNo
	 * @return
	 * 
	 */
	@RequestMapping("detail.do")
	@ResponseBody
	public ServiceResponse detail(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.getOrderDetail(user.getId(), orderNo);
	}
	/**
	 * 用户查询订单列表
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * 
	 */
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse list(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10")int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
	}
	
	
	
	/**
	 * 支付入口
	 * @param session
	 * @param request
	 * @param orderNo
	 * @return
	 * 未测试
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
	
	/**
	 * 支付宝回调
	 * @param request
	 * @return
	 * 未测试
	 */
	@RequestMapping("alipay_callback.do")
	@ResponseBody
	public String alipayCallback(HttpServletRequest request){
		Map<String,String> params = Maps.newHashMap();
		Map<String, String[]> requestParameters = request.getParameterMap();
		for(Entry<String, String[]> iter : requestParameters.entrySet()){
			String name = iter.getKey();
			String [] values = iter.getValue();
			StringBuilder valueStr = new StringBuilder();
			for(String value: values){
				valueStr.append(value + ",");
			}
			valueStr.deleteCharAt(valueStr.length()-1);
			params.put(name, valueStr.toString());
		}
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
		//验证回调正确性
		params.remove("sign_type");
		try {
			boolean alipayRSACheckedv2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8",Configs.getSignType());
			if(alipayRSACheckedv2 == false){
				return Const.AlipayCallback.RESPONSE_FAILED;
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝回调异常");
			e.printStackTrace();
		}
		// TODO 验证数据正确性
		
		ServiceResponse serviceResponse = iOrderService.aliCallback(params);
		if(serviceResponse.isSuccess()){
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAILED;
	}
	
	/**
	 * 查询支付状态
	 * @param session
	 * @param orderNo
	 * @return
	 * 未测试
	 */
	@RequestMapping("query_order_pay_status.do")
	@ResponseBody
	public ServiceResponse queryOrderPayStatus(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		return iOrderService.queryOrderPayStatus(user.getId(), orderNo);
	}
}
