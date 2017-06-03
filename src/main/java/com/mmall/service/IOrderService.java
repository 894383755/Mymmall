package com.mmall.service;

import com.mmall.common.ServiceResponse;

public interface IOrderService {

	ServiceResponse pay(Integer userId, Long orderNo, String path);

}
