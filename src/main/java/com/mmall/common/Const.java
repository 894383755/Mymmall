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
	public enum OrderStatusEnum{
		CANCELED(0,"已取消"),
		NO_PAY(10,"未支付"),
		PAID(20,"已支付"),
		SHIPPED(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");
		
		private String value;
		private int code;
		
		private OrderStatusEnum(int code, String value) {
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
	public interface AlipayCallback{
		String TREAD_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		String TREAD_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
	}
	public enum PayPlatformEnum{
		ALIPAY(1,"支付宝");
		private String value;
		private int code;
		
		private PayPlatformEnum(int code, String value) {
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
