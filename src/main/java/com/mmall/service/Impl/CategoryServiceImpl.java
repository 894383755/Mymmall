package com.mmall.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Override
	public ServiceResponse<Category> addCategory(String categoryName, Integer parenId){
		if(parenId == null || StringUtils.isBlank(categoryName)){
			return ServiceResponse.creatByError("参数错误");
		}
		Category category = new Category();
		category.setId(parenId);
		category.setName(categoryName);
		category.setParentId(parenId);
		category.setStatus(true);
		if(categoryMapper.insert(category) <= 0){
			 ServiceResponse.creatByError("添加品类失败");
		}
		return ServiceResponse.creatBySuccess("添加品类成功");
	}

	@Override
	public ServiceResponse<Category> updataCategoryName(String categoryName, Integer parenId) {
		if(parenId == null || StringUtils.isBlank(categoryName)){
			return ServiceResponse.creatByError("参数错误");
		}
		Category category = new Category();
		category.setId(parenId);
		category.setName(categoryName);
		if(categoryMapper.updateByPrimaryKeySelective(category) <= 0){
			 ServiceResponse.creatByError("更新品类失败");
		}
		return ServiceResponse.creatBySuccess("更新品类成功");
	}
}
