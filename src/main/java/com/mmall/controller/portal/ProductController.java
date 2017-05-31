package com.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;

@Controller
@RequestMapping("/product/")
public class ProductController {
	@Autowired
	private IProductService iproductService;
	
	@RequestMapping("detail.do")
	@ResponseBody
	public ServiceResponse<ProductDetailVo> detail(Integer productId){
		return iproductService.getProductDetail(productId);
	}
	
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse<PageInfo> list(@RequestParam(value = "keywrod",required = false) String keywrod, @RequestParam(value = "categoryId",required = false)Integer categoryId, 
			@RequestParam(value = "pageNum",defaultValue = "1")int pageNum, @RequestParam(value = "pageSize", defaultValue = "10")int pageSize, @RequestParam(value = "orderBy", defaultValue = "")String orderBy){
		return iproductService.getProductByKeywordCategory(keywrod, categoryId, pageNum, pageSize, orderBy);
	}
}
