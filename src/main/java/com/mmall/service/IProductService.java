package com.mmall.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
	ServiceResponse<Product> saveOrUpdataProduct(Product product);
	
	ServiceResponse<Product> setSaleStatus(Integer productId, Integer status);
	
	ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId); 
	
	ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);
}
