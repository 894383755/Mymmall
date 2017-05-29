package com.mmall.service;

import java.util.List;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;

public interface ICategoryService {

	ServiceResponse<Category> addCategory(String categoryName, Integer parenId);
	
	ServiceResponse<Category> updataCategoryName(String categoryName, Integer parenId);
	
	ServiceResponse<List<Category>> getChildrenParallelCategory(Integer parenId);
	
	ServiceResponse<List<Integer>> selectCategoryAndDeepChildrenCategory(Integer parenId);
}
