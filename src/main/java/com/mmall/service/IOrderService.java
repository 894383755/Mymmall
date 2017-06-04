package com.mmall.service;

import java.util.Map;

import com.mmall.common.ServiceResponse;

public interface IOrderService {

	ServiceResponse pay(Integer userId, Long orderNo, String path);

	ServiceResponse aliCallback(Map<String, String> params);

	ServiceResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServiceResponse createOrder(Integer userId, Integer shippingId);

	ServiceResponse cancel(Integer userId, Long orderNo);

	ServiceResponse getOrderCartProduct(Integer userId);

	ServiceResponse getOrderDetail(Integer userId, Long orderNo);

	ServiceResponse getOrderList(Integer userId, int pageNum, int pageSize);

}
