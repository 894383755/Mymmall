package com.mmall.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private ICategoryService iCategoryService;

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
		if(product.getId() != null){//有id更新产品
			if(productMapper.updateByPrimaryKeySelective(product) > 0){
				return ServiceResponse.creatBySuccess("更新产品成功");
			}
			return ServiceResponse.creatByError("更新产品失败");
		}else{
			if(productMapper.insert(product) > 0 ){
				return ServiceResponse.creatBySuccess("新建产品成功");
			}
			return ServiceResponse.creatByError("新建产品失败");
		}
		
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

	@Override
	public ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		List<Product> productList = productMapper.selectList();
		List<ProductListVo> productListVo = Lists.newArrayList();
		for(Product product : productList){
			productListVo.add(assembleProductListVo(product));
		}
		PageInfo pageResult = new PageInfo(productList);
		pageResult.setList(productListVo);
		return ServiceResponse.creatBySuccess("查询成功",pageResult);
	}
	
	 private ProductListVo assembleProductListVo(Product product){
	        ProductListVo productListVo = new ProductListVo();
	        productListVo.setId(product.getId());
	        productListVo.setName(product.getName());
	        productListVo.setCategoryId(product.getCategoryId());
	        //productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
	        productListVo.setMainImage(product.getMainImage());
	        productListVo.setPrice(product.getPrice());
	        productListVo.setSubtitle(product.getSubtitle());
	        productListVo.setStatus(product.getStatus());
	        return productListVo;
	    }
	@Override
	public ServiceResponse<PageInfo> serchProduct(String productName,Integer productId, int pageNum,int pageSize){
		PageHelper.startPage(pageNum,pageSize);
		if(StringUtils.isBlank(productName)){
			return ServiceResponse.creatByError("参数错误");
		}
		productName = new StringBuilder().append("%").append(productName).append("%").toString();
		List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
		List<ProductListVo> productListVo = Lists.newArrayList();
		for(Product product : productList){
			productListVo.add(assembleProductListVo(product));
		}
		PageInfo pageResult = new PageInfo(productList);
		pageResult.setList(productListVo);
		return ServiceResponse.creatBySuccess("查询成功",pageResult);
	}
	
	@Override
	public ServiceResponse<ProductDetailVo> getProductDetail(Integer productId){
		if(productId == null){
			return ServiceResponse.creatByError("参数错误");
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServiceResponse.creatByError("id错误");
		}
		if(product.getStatus() != Const.productStatusEnum.ON_SALE.getCode()){
			return ServiceResponse.creatByError("产品未上线");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServiceResponse.creatBySuccess("查询成功",productDetailVo);
	}

	@Override
	public ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
			int pageNum,int pageSize, String orderBy) {
		if(StringUtils.isBlank(keyword) && categoryId == null){
			return ServiceResponse.creatByError("参数错误");
		}
		List<Integer> categoryIdList = new ArrayList<Integer>();
		if(categoryId != null){
			Category category = categoryMapper.selectByPrimaryKey(categoryId);
			if(category == null && StringUtils.isBlank(keyword)){//没有查询到
				PageHelper.startPage(pageNum,pageSize);
				List<ProductListVo> productListVosList = Lists.newArrayList();
				PageInfo pageInfo = new PageInfo(productListVosList);
				return ServiceResponse.creatBySuccess("没有该分类",pageInfo);
			}
			//查询子类所有分类id列表
			categoryIdList =  iCategoryService.selectCategoryAndDeepChildrenCategory(categoryId).getData();
		}
		if(StringUtils.isNotBlank(keyword)){
			keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
		}
		PageHelper.startPage(pageNum,pageSize);
		if(StringUtils.isNotBlank(orderBy)){
			if(Const.ProductListOrderBy.PRICE_ASE_DESC.contains(orderBy)){
				String [] orderByArray = orderBy.split("_");
				PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
			}
		}
		List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product product: productList) {
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);
		return ServiceResponse.creatBySuccess("获取成功",pageInfo);
	}
}
