package com.mmall.service;

import javax.servlet.http.HttpSession;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {

	ServiceResponse add(Integer userId, Shipping shipping);

	ServiceResponse del(Integer userId, Integer shippingId);

	ServiceResponse updata(Integer userId, Shipping shipping);

	ServiceResponse select(Integer userId, Integer shippingId);

	ServiceResponse list(Integer userId, int pageNum, int pageSize);

}
