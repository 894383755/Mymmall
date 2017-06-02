package com.mmall.service.Impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;

@Service
public class ShippingServiceImpl implements IShippingService {
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	@Override
	public ServiceResponse add(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int resCount = 	shippingMapper.insert(shipping);
		if(resCount >0 ){
			Map<String,Integer> result =  Maps.newHashMap();
			result.put("shippingId", shipping.getId());
			return ServiceResponse.creatBySuccess("新建地址成功",result);
		}
		return ServiceResponse.creatByError("新建地址失败");
	}
	
	@Override
	public ServiceResponse del(Integer userId, Integer shippingId){
		int resCount = 	shippingMapper.deleteByShippingIdUserId(userId, shippingId);
		if(resCount >0 ){
			return ServiceResponse.creatBySuccess("删除地址成功");
		}
		return ServiceResponse.creatByError("删除地址失败");
	}
	
	@Override
	public ServiceResponse updata(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int resCount = 	shippingMapper.updateByShipping(shipping);
		if(resCount >0 ){
			return ServiceResponse.creatBySuccess("更新地址成功");
		}
		return ServiceResponse.creatByError("更新地址失败");
	}
	
	@Override
	public ServiceResponse select(Integer userId, Integer shippingId){
		Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
		if(shipping == null ){
			return ServiceResponse.creatByError("查找地址失败");
		}
		return ServiceResponse.creatBySuccess("查找地址成功",shipping);
	}
	
	@Override
	public ServiceResponse list(Integer userId, int pageNum, int pageSize){
		PageHelper.startPage(pageNum,pageSize);
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(shippingList);
		return ServiceResponse.creatBySuccess("查找地址列表成功",pageInfo);
	}
}
