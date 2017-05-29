package com.mmall.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.ProductDetailVo;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;

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

	@Override
	public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		if(productId == null){
			return ServiceResponse.creatByError("参数错误");
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServiceResponse.creatByError("id错误");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServiceResponse.creatBySuccess("查询成功",productDetailVo);
	}
	private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //图片的热加载
        //productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }
	
}
