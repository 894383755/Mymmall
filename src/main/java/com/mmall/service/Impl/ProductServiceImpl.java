package com.mmall.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	@Autowired
	private ProductMapper productMapper;

	@Override
	public ServiceResponse<Product> saveOrUpdataProduct(Product product) {
		if(product == null){
			return ServiceResponse.creatByError("产品参数不正确");
		}
		if(StringUtils.isBlank(product.getSubImages())){
			return ServiceResponse.creatByError("未登录");
		}
		 String[] subImageArry = product.getSubImages().split(",");
		if( subImageArry.length <= 0){
			return ServiceResponse.creatByError("未登录");
		}
		product.setMainImage(subImageArry[0]);
		if(product.getId() == null){
			if(productMapper.updateByPrimaryKeySelective(product) <= 0){
				return ServiceResponse.creatBySuccess("更新产品成功");
			}
			return ServiceResponse.creatByError("更新产品失败");
		}
		if(productMapper.insert(product) <= 0 ){
			return ServiceResponse.creatBySuccess("新建产品成功");
		}
		return ServiceResponse.creatByError("新建产品失败");
	}

	@Override
	public ServiceResponse<Product> setSaleStatus(Integer productId, Integer status) {
		if(productId == null || status == null){
			return ServiceResponse.creatByError("参数错误");
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		if(productMapper.updateByPrimaryKeySelective(product) <= 0){
			return ServiceResponse.creatByError("更新失败");
		}
		return ServiceResponse.creatBySuccess("更新成功");
	}
	
}
