package com.mmall.service.Impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Product;
import com.mmall.pojo.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;


@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ShippingMapper shippingMapper;
	
	@Override
	public ServiceResponse createOrder(Integer userId, Integer shippingId){
		//获得购物车列表
		List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
		//计算总价
		ServiceResponse serviceResponse = this.getCartOrderItem(userId, cartList);
		if(serviceResponse.isNotSuccess()){
			return serviceResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serviceResponse.getData();
		BigDecimal bigDecimal = this.getOrderTotalPrice(orderItemList);
		//生成订单
		Order order = this.assembleOrder(userId, shippingId, bigDecimal);
		if(order == null){
			return ServiceResponse.creatByError("生成订单错误");
		}else if(CollectionUtils.isEmpty(orderItemList)){
			return ServiceResponse.creatByError("订单为空");
		}
		for(OrderItem orderItem : orderItemList){
			orderItem.setOrderNo(order.getOrderNo());
		}
		//批量插入
		orderItemMapper.batchInsert(orderItemList);
		//减少库存
		this.reduceProductStock(orderItemList);
		//清空购物车
		this.clearCart(cartList);
		OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
		return ServiceResponse.creatBySuccess(orderVo);
	}
	private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }
	
	 private ShippingVo assembleShippingVo(Shipping shipping){
	        ShippingVo shippingVo = new ShippingVo();
	        shippingVo.setReceiverName(shipping.getReceiverName());
	        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
	        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
	        shippingVo.setReceiverCity(shipping.getReceiverCity());
	        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
	        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
	        shippingVo.setReceiverZip(shipping.getReceiverZip());
	        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
	        return shippingVo;
	    }
	private void clearCart(List<Cart> cartList){
		for(Cart cart : cartList){
			cartMapper.deleteByPrimaryKey(cart.getId());
		}
	}
	 private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
	        OrderItemVo orderItemVo = new OrderItemVo();
	        orderItemVo.setOrderNo(orderItem.getOrderNo());
	        orderItemVo.setProductId(orderItem.getProductId());
	        orderItemVo.setProductName(orderItem.getProductName());
	        orderItemVo.setProductImage(orderItem.getProductImage());
	        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
	        orderItemVo.setQuantity(orderItem.getQuantity());
	        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

	        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
	        return orderItemVo;
	    }
	private void reduceProductStock(List<OrderItem> orderItems){
		for(OrderItem orderItem : orderItems){
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
			product.setStock(product.getStock() - orderItem.getQuantity());
			productMapper.updateByPrimaryKeySelective(product);
		}
		return ;
	}
	
	private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment){
		Order order = new Order();
		long orderNo = this.generateOrderNo();
		order.setOrderNo(orderNo);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		order.setPostage(0);
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPayment(payment);
		order.setUserId(userId);
		order.setShippingId(shippingId);
		// TODO 发货时间  付款时间
		int rowCount = orderMapper.insert(order);
		if(rowCount <= 0){
			return null;
		}else return order;
	}
	private long generateOrderNo(){
		long currentTime = System.currentTimeMillis();
		return currentTime+new Random().nextInt(100);
	}
	
	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
		BigDecimal bigDecimal = new BigDecimal('0');
		for(OrderItem orderItem : orderItemList){
			bigDecimal = BigDecimalUtil.add(bigDecimal.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		return bigDecimal;
	}
	
	private ServiceResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart>cartList){
		List<OrderItem> orderItems = Lists.newArrayList();
		if(CollectionUtils.isEmpty(cartList)){
			return ServiceResponse.creatByError("购物车为空");
		}
		for(Cart cart : cartList){
			OrderItem orderItem = new OrderItem();
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if(Const.productStatusEnum.ON_SALE.getCode() != product.getStatus()){
				return ServiceResponse.creatByError("产品已下架");
			}else if(cart.getQuantity() > product.getStock()){
				return ServiceResponse.creatByError("库存不足");
			}
			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
			orderItems.add(orderItem);
		}
		return ServiceResponse.creatBySuccess("获取成功",orderItems);
	}
	
	@Override
	public ServiceResponse cancel(Integer userId, Long orderNo){
		Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
		if(order == null){
			return ServiceResponse.creatByError("订单不存在");
		}
		if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
			return ServiceResponse.creatByError("订单已支付，无法退款");
		}
		order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
		int count = orderMapper.deleteByPrimaryKey(order.getId());
		if(count <= 0){
			return ServiceResponse.creatByError("更新失败");
		}
		return ServiceResponse.creatBySuccess("更新成功");
	}
	@Override
	public ServiceResponse getOrderCartProduct(Integer userId){
		OrderProductVo orderProductVo = new OrderProductVo();
		List<Cart> carts = cartMapper.selectCartByUserId(userId);
		ServiceResponse serviceResponse = this.getCartOrderItem(userId, carts);
		if(serviceResponse.isNotSuccess()){
			return serviceResponse;
		}
		List<OrderItem> orderItemList =( List<OrderItem> ) serviceResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        return ServiceResponse.creatBySuccess("获取成功", orderProductVo);
	}
	
	@Override
	public ServiceResponse getOrderDetail(Integer userId, Long orderNo){
		Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
		if(order == null){
			
			return ServiceResponse.creatByError("订单不存在");
		}
		List<OrderItem> orderItems;
		if(userId == null){
			orderItems = orderItemMapper.getByOrderNo(order.getOrderNo());
		}else{
			orderItems = orderItemMapper.getByOrderNoUserId(orderNo, userId);
		}
		OrderVo orderVo = this.assembleOrderVo(order, orderItems);
		return ServiceResponse.creatBySuccess("获取成功", orderVo);
	}
	
	@Override
	public ServiceResponse getOrderList(Integer userId, int pageNum, int pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<Order> orders = orderMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(orders);
		return ServiceResponse.creatBySuccess("获取成功", pageInfo);
	}
	
	private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId){
		List<OrderVo> orderVos = Lists.newArrayList();
		for(Order order : orderList){
			List<OrderItem> orderItems = orderItemMapper.getByOrderNoUserId(order.getOrderNo(), userId);
			OrderVo orderVo = this.assembleOrderVo(order, orderItems);
			orderVos.add(orderVo);
		}
		return orderVos;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

    // 支付宝当面付2.0服务
    //private static AlipayTradeService   tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    //private static AlipayTradeService   tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    //private static AlipayMonitorService monitorService;
	
	static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        //tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        //tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
//        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
//            .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
//            .setFormat("json").build();
    }
	
	
	@Override
	public ServiceResponse pay(Integer userId, Long orderNo, String path){
		Map<String, String> resultMap = Maps.newHashMap();
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if(order == null){
			return ServiceResponse.creatByError("用户没有订单");
		}
		resultMap.put("orderMapper", String.valueOf(order.getOrderNo()));
		
		
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall品牌旗舰门店当面付扫码消费,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("订单商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItems = orderItemMapper.getByOrderNoUserId(orderNo,userId);
        for(OrderItem orderItem : orderItems){
        	// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail

        	GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), 
        			orderItem.getProductName(), BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100)).longValue(), 
        			orderItem.getQuantity());
        	// 创建好一个商品后添加至商品明细列表
        	goodsDetailList.add(goodsDetail);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(new Properties().getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                
                //创建二维码
                File file = new File(path);
                if(!file.exists()){
                	file.setWritable(true);
                	file.mkdirs();
                }
                
                // 需要修改为运行机器上的路径
                
                //String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",response.getOutTradeNo());
                String filePath = String.format(file.getParentFile()+"/qr-%s.png",response.getOutTradeNo());
                File filenow = new File(filePath);
                System.out.println(filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                log.info("filePath:" + filePath);
                 //ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                return ServiceResponse.creatBySuccess("支付成功",resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServiceResponse.creatByError("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServiceResponse.creatByError("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.creatByError("不支持的交易状态，交易返回异常!!!");
        }
	}
	
	@Override
	public ServiceResponse aliCallback(Map<String,String> params){
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		Order order = orderMapper.selectByOrderNo(orderNo);
		if(order == null){
			return ServiceResponse.creatByError("回调订单出错");
		}
		if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServiceResponse.creatBySuccess("回调之前已经成功");
		}
		if(Const.AlipayCallback.TREAD_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			orderMapper.updateByPrimaryKeySelective(order);
		}
		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformStatus(tradeStatus);
		payInfo.setPlatformNumber(tradeNo);
		payInfoMapper.insert(payInfo);
		return ServiceResponse.creatBySuccess("回调成功");
	}
	
	@Override
	public ServiceResponse queryOrderPayStatus(Integer userId, Long orderNo){
		Order order = orderMapper.selectByOrderNo(orderNo);
		if(order == null){
			return ServiceResponse.creatByError("无订单");
		}
		if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServiceResponse.creatBySuccess("已支付",true);
		}
		return ServiceResponse.creatByError("未支付",false);
	}
	
	// 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    
 // 测试当面付2.0查询订单
    @Override
    public ServiceResponse queryOrderStatusByAli(Long orderNo) {
    	String outTradeNo = orderNo.toString();
        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
            .setOutTradeNo(outTradeNo);

        //AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        AlipayF2FQueryResult result = new AlipayTradeServiceImpl.ClientBuilder().build().queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                //更新本地支付状态
                Order order = orderMapper.selectByOrderNo(orderNo);
                order.setStatus(Const.OrderStatusEnum.PAID.getCode());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServiceResponse.creatBySuccess("查询订单支付成功");

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                return ServiceResponse.creatByError("查询返回该订单支付失败或被关闭");

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                return ServiceResponse.creatByError("系统异常，订单支付状态未知");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.creatByError("不支持的交易状态，交易返回异常");
        }
    }
    
 // 测试当面付2.0退款
    @Override
    public ServiceResponse refundOrder(Long orderNo) {
    	Order order = orderMapper.selectByOrderNo(orderNo);
        // (必填) 外部订单号，需要退款交易的商户外部订单号
        String outTradeNo = orderNo.toString();
        
        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        String refundAmount = order.getPayment().toString();

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = "";

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = "正常退款";

        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        String storeId = "test_store_id";

        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
            .setOutTradeNo(outTradeNo).setRefundAmount(refundAmount).setRefundReason(refundReason)
            .setOutRequestNo(outRequestNo).setStoreId(storeId);

		AlipayF2FRefundResult result = new AlipayTradeServiceImpl.ClientBuilder().build().tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                return ServiceResponse.creatBySuccess("支付宝退款成功");

            case FAILED:
                log.error("支付宝退款失败!!!");
                return ServiceResponse.creatByError("支付宝退款失败");

            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                return ServiceResponse.creatByError("系统异常，订单退款状态未知");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.creatByError("不支持的交易状态，交易返回异常");
        }
    }
    
    
    
    
    
    
    
    @Override
    public ServiceResponse manageList(int pageNum, int pageSize){
    	PageHelper.startPage(pageNum, pageSize);
    	List<Order> orders = orderMapper.selectAllOrder();
    	List<OrderVo> orderVos = this.assembleOrderVoList(orders,null);
    	PageInfo  pageInfo = new PageInfo(orderVos);
    	pageInfo.setList(orderVos);
    	return ServiceResponse.creatBySuccess("查询成功", pageInfo);
    }
    @Override
    public ServiceResponse<OrderVo> manageDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            return ServiceResponse.creatBySuccess(orderVo);
        }
        return ServiceResponse.creatByError("订单不存在");
    }


    @Override
    public ServiceResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);

            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServiceResponse.creatBySuccess(pageResult);
        }
        return ServiceResponse.creatByError("订单不存在");
    }

    @Override
    public ServiceResponse<String> manageSendGoods(Long orderNo){
        Order order= orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServiceResponse.creatBySuccess("发货成功");
            }
        }
        return ServiceResponse.creatByError("订单不存在");
    }

    
    
    
    
    
    
}
