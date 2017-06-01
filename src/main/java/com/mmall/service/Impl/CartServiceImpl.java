package com.mmall.service.Impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	
	@Override
	public ServiceResponse<CartVo> add(Integer userId, Integer productId, Integer count){
		if(userId == null || count == null)
			return ServiceResponse.creatByError("参数错误");
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if(cart == null){//购物车没有该产品
			Cart cartItem = new Cart();
			cartItem.setQuantity(count);
			cartItem.setChecked(Const.Cart.CHECKED);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			cartMapper.insert(cartItem);
		}else{//购物车有该产品
			count = cart.getQuantity() + 1;
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		return this.list(userId);
	}
	
	@Override
	public ServiceResponse<CartVo> updata(Integer userId, Integer productId, Integer count){
		if(userId == null || count == null)
			return ServiceResponse.creatByError("参数错误");
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if(cart != null){
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKeySelective(cart);
		return this.list(userId);
	}
	
	@Override
	public ServiceResponse<CartVo> deleteProduct(Integer userId, String productIds){
		if(userId == null )
			return ServiceResponse.creatByError("参数错误");
		List<String> productList = Splitter.on(',').splitToList(productIds);
		if(productList == null){
			return ServiceResponse.creatByError("删除错误，无删除对象");
		}
		cartMapper.deleteByUserIdProductIds(userId, productList);
		return this.list(userId);
	}
	
	@Override
	public ServiceResponse<CartVo> list(Integer userId){
		if(userId == null )
			return ServiceResponse.creatByError("参数错误");
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServiceResponse.creatBySuccess("查询成功",cartVo);
	}
	
	@Override
	public ServiceResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked, Integer productId){
		if(userId == null )
			return ServiceResponse.creatByError("参数错误");
		cartMapper.checkedOrUncheckedProduct(userId, null, checked);
		return this.list(userId);
	}
	
	@Override
	public ServiceResponse<Integer> getCartProductCount(Integer userId){
		if(userId == null )
			return ServiceResponse.creatByError("参数错误");
		Integer count = cartMapper.selectCartProductCheckedStatusByUserId(userId);
		return ServiceResponse.creatBySuccess("查询成功",count);
	}
	
	
	
	private CartVo getCartVoLimit(Integer userId){
		CartVo cartVo = new CartVo();//购物车总的信息
		List<Cart> cartList = cartMapper.selectCartByUserId(userId);
		List<CartProductVo> cartProductVos = Lists.newArrayList();
		BigDecimal cartTotalPrice = new BigDecimal("0");
		if(CollectionUtils.isNotEmpty(cartList)){
			for(Cart cart: cartList){
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cart.getId());
				cartProductVo.setUserId(cart.getUserId());
				cartProductVo.setProductId(cart.getProductId());
				Product product = productMapper.selectByPrimaryKey(cart.getProductId());
				if(product != null){
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
				}
				int buyLimitCount;
				if(product.getStatus() >= cart.getProductId()){//判断库存是否充足
					buyLimitCount = cart.getProductId();
					cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
				}else{
					cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					buyLimitCount = product.getStock();
					Cart cartForQuantity = new Cart();
					cartForQuantity.setId(cart.getId());
					cartForQuantity.setQuantity(buyLimitCount);
					cartMapper.updateByPrimaryKeySelective(cartForQuantity);
				}
				cartProductVo.setQuantity(buyLimitCount);
				cartProductVo.setProductPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
				cartProductVo.setProductChecked(cart.getChecked());
				if(cart.getChecked() == Const.Cart.CHECKED){
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductPrice().doubleValue());
				}
				cartProductVos.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVos);
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		return cartVo;
	}
	private boolean getAllCheckedStatus(Integer userId){
		if(userId == null){
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
	}
	
}
