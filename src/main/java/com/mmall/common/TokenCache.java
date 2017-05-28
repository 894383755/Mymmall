package com.mmall.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * 数据缓存
 * @author 王
 *
 */
public class TokenCache {
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
	private static LoadingCache<String, String> lodingCache = CacheBuilder.newBuilder()
			.initialCapacity(1000)//初始化最大值
			.maximumSize(10000)//最大值,超过使用lru算法
			.expireAfterWrite(12, TimeUnit.HOURS)//有效期为12小时
			.build(new CacheLoader<String, String>() {
				/**
				 * 默认数据加载实现
				 */
				@Override
				public String load(String arg0) throws Exception {
					return "null";
				}
			});
	public static void setKey(String key, String value){
		lodingCache.put(key, value);
	}
	
	public static String getKey(String key){
		String value = null;
		try{
			value = lodingCache.get(key);
			if("null".equals(value)){
				return null;
			}
			return value;
		}catch(Exception e){
			logger.error("localCatch is error",e);
		}
		return null;
	}
}
