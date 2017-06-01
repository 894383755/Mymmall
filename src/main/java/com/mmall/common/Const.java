package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	public static final String CURRENT_USER = "currentUser";
	public static final String EMAIL = "emali";
	public static final String USERNAME = "username";
	public interface Cart{
		int CHECKED  = 1;//选中状态
		int UN_CHECKED = 0;//未选中状态
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
		String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
	}
	public interface ProductListOrderBy{
		Set<String> PRICE_ASE_DESC = Sets.newHashSet("price_desc","price_asc");
	}
	public interface Role{
		int ROLE_CUSTOMER = 0;//用户
		int ROLE_ADMIN = 1;//管理员
	}
	public enum productStatusEnum{
		ON_SALE(1,"在线");
		
		private int code;
		private String value;
		
		private productStatusEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
		
		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
		
	}
}
