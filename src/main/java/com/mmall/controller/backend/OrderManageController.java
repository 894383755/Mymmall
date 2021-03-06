package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {
	@Autowired
	private IUserService iUserService;
	@Autowired
	private IOrderService iOrderService;
	/**
	 * 查询订单列表
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * 
	 */
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse orderList(HttpSession session, @RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iOrderService.manageList(pageNum, pageSize);
	}
	/**
	 * 查询订单详细
	 * @param session
	 * @param orderNo
	 * @return
	 * 
	 */
	@RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isNotSuccess()){
            return ServiceResponse.creatByError("无权限操作");
        }
        return iOrderService.manageDetail(orderNo);
    }


	/**
	 * 按订单号查询
	 * @param session
	 * @param orderNo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * 
	 */
    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isNotSuccess()){
            return ServiceResponse.creatByError("无权限操作");
        }
        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }


    /**
     * 发货
     * @param session
     * @param orderNo
     * @return
     * 
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServiceResponse<String> orderSendGoods(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isNotSuccess()){
            return ServiceResponse.creatByError("无权限操作");
        }
        return iOrderService.manageSendGoods(orderNo);
    }
}
