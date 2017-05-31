package com.mmall.controller.backend;

import java.util.Map;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	
	@Autowired
	private IFileService iFileService;
	/**
	 * 商品新增，修改
	 * @param session
	 * @param product
	 * @return
	 * 
	 */
	@RequestMapping("save.do")
	@ResponseBody
	public ServiceResponse ProductSave(HttpSession session, Product product){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.saveOrUpdataProduct(product);
	}
	/**
	 * 商品状态的修改
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 * 
	 */
	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.setSaleStatus(productId, status);
	}
	/**
	 * 获取产品详情
	 * @param session
	 * @param productId
	 * @return
	 * 
	 */
	@RequestMapping("detail.do")
	@ResponseBody
	public ServiceResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.manageProductDetail(productId);
	}
	
	/**
	 * 后台商品列表
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * 
	 */
	@RequestMapping("list.do")
	@ResponseBody
	public ServiceResponse<PageInfo> getList(HttpSession session, @RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.getProductList(pageNum, pageSize);
	}
	
	/**
	 * 后台商品搜索
	 * @param session
	 * @param productName
	 * @param productId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * ProductId 测试出错
	 */
	@RequestMapping("search.do")
	@ResponseBody
	public ServiceResponse<PageInfo> productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value="pageNum",defaultValue="1")int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		return iProductService.serchProduct(productName, productId, pageNum, pageSize);
	}
	
	/**
	 * 文件上传
	 * @param file
	 * @param session
	 * @return
	 * 测试失败
	 */
	@RequestMapping("upload.do")
	@ResponseBody
	public ServiceResponse upload(@RequestParam("upload_file")MultipartFile file, HttpSession session){
		Map resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServiceResponse.creatByError("未登录");
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			return ServiceResponse.creatByError("无权限");
		}
		String path = session.getServletContext().getRealPath("upload");
		String targetFileName = iFileService.upload(file, path);
		Map fileMap = Maps.newHashMap();
		fileMap.put("uri", targetFileName);
		fileMap.put("url", path);
		return ServiceResponse.creatBySuccess("上传成功",fileMap);
	}
	/**
	 * 富文本上传
	 * @param file
	 * @param session
	 * @return
	 * 测试失败
	 */
	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richtextImgUpload(@RequestParam("upload_file")MultipartFile file, HttpSession session, HttpRequest request, HttpServletResponse response){
		Map resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			
			resultMap.put("succes", false);
			resultMap.put("msg", "请登录管理员帐号");
			return resultMap;
		}
		if(iUserService.checkAdminRole(user).isNotSuccess()){
			resultMap.put("succes", false);
			resultMap.put("msg", "无权限");
			return resultMap;
		}
		String path = session.getServletContext().getRealPath("upload");
		String targetFileName = iFileService.upload(file, path);
		resultMap.put("succes", true);
		resultMap.put("msg", "请登录管理员帐号");
		resultMap.put("file_path", path);
		response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
		return resultMap;
	}
}
