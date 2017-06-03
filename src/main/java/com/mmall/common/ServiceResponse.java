package com.mmall.common;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//忽视掉为null的字段
public class ServiceResponse<T> implements Serializable {
	private static final long serialVersionUID = -9131677347828942101L;
	private int status;
	private String msg;
	private T data;
	//对内构造函数
	protected ServiceResponse(int status) {
		this.status = status;
	}
	protected ServiceResponse(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}
	protected ServiceResponse(int status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	protected ServiceResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}
	@JsonIgnore //使其不再序列化中
	public boolean isSuccess(){
		return this.status == ResponseCode.SUCCESS.getCode();
	}
	public boolean isNotSuccess(){
		return ! this.isSuccess();
	}
	public int getStatus() {
		return status;
	}
	public String getMsg() {
		return msg;
	}
	public T getData() {
		return data;
	}
	//对外创建方法
	public static <T> ServiceResponse<T> creatBySuccess(){
		return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	public static <T> ServiceResponse<T> creatBySuccess(String msg){
		return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	public static <T> ServiceResponse<T> creatBySuccess(T data){
		return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	public static <T> ServiceResponse<T> creatBySuccess(String msg, T data){
		return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}
	public static <T> ServiceResponse<T> creatByError(){
		return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
	}
	public static <T> ServiceResponse<T> creatByError(String msg){
		return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), msg);
	}
	public static <T> ServiceResponse<T> creatByError(T data){
		return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),data);
	}
	public static <T> ServiceResponse<T> creatByError(String msg, T data){
		return new ServiceResponse<T>(ResponseCode.ERROR.getCode(), msg, data);
	}
	public static <T> ServiceResponse<T> creatByError(int code, String msg){
		return new ServiceResponse<T>(code,msg);
	}
	public <T> ServiceResponse<T> setMsg(String msg) {
		this.msg = msg;
		return (ServiceResponse<T>) this;
	}
}
