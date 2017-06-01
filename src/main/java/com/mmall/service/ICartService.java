package com.mmall.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.vo.CartVo;

public interface ICartService {

	ServiceResponse add(Integer userId, Integer productId, Integer count);

	ServiceResponse<CartVo> updata(Integer userId, Integer productId, Integer count);

	ServiceResponse<CartVo> deleteProduct(Integer userId, String productIds);

	ServiceResponse<CartVo> list(Integer userId);

	ServiceResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked, Integer productId);

	ServiceResponse<Integer> getCartProductCount(Integer userId);
	
	
}
