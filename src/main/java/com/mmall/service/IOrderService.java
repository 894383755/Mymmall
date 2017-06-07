package com.mmall.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.vo.OrderVo;

public interface IOrderService {

	ServiceResponse pay(Integer userId, Long orderNo, String path);

	ServiceResponse aliCallback(Map<String, String> params);

	ServiceResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServiceResponse createOrder(Integer userId, Integer shippingId);

	ServiceResponse cancel(Integer userId, Long orderNo);

	ServiceResponse getOrderCartProduct(Integer userId);

	ServiceResponse getOrderDetail(Integer userId, Long orderNo);

	ServiceResponse getOrderList(Integer userId, int pageNum, int pageSize);

	ServiceResponse manageList(int pageNum, int pageSize);

	ServiceResponse<String> manageSendGoods(Long orderNo);

	ServiceResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

	ServiceResponse<OrderVo> manageDetail(Long orderNo);

	ServiceResponse queryOrderStatusByAli(Long orderNo);
	
	ServiceResponse refundOrder(Long orderNo);

}
