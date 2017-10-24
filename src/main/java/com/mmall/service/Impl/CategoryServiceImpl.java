package com.mmall.service.Impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
		category.setName(categoryName);
		category.setParentId(parenId);
		category.setStatus(true);
		if(categoryMapper.insert(category) <= 0){
			 ServiceResponse.creatByError("添加品类失败");
		}
		return ServiceResponse.creatBySuccess("添加品类成功");
	} 

	@Override
	public ServiceResponse<Category> updataCategoryName(String categoryName, Integer categoryId) {
		if(categoryId == null || StringUtils.isBlank(categoryName)){
			return ServiceResponse.creatByError("参数错误");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		if(categoryMapper.updateByPrimaryKeySelective(category) <= 0){
			 ServiceResponse.creatByError("更新品类失败");
		}
		return ServiceResponse.creatBySuccess("更新品类成功");
	}

	@Override
	public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		return ServiceResponse.creatBySuccess("提取成功", categoryList);
	}

	@Override
	public ServiceResponse<List<Integer>> selectCategoryAndDeepChildrenCategory(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildrenCategory(categorySet, categoryId);
		List<Integer> categoryIdList =Lists.newArrayList();
		if(categoryId != null){
			for(Category category : categorySet){
				categoryIdList.add(category.getId());
			}
		}
		return 	ServiceResponse.creatBySuccess("查询成功",categoryIdList);
	}
	private Set<Category> findChildrenCategory(Set<Category> categorySet, Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if(category == null){
			return categorySet;
		}
		categorySet.add(category);
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		for(Category categoryEcah : categoryList){
			findChildrenCategory(categorySet, categoryEcah.getId());
		}
		return categorySet;
	}
}
