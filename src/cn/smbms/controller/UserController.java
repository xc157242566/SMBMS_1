package cn.smbms.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@RequestMapping("jsp/frame")
	public String frame(){
		return "jsp/frame";
	}
	@RequestMapping("jsp/logout.do")//退出
	public String out(HttpServletRequest request){
		request.getSession().removeAttribute(Constants.USER_SESSION);
		return "/login";
	}
	@RequestMapping("/jsp/userquery")
	public String userquery(
		@RequestParam(value="queryname",required=false)String queryUserName,
		@RequestParam(value="queryUserRole",required=false)String temp,
		@RequestParam(value="pageIndex",required=false)String pageIndex,
		HttpServletRequest request){
	
		int queryUserRole = 0;
		List<User> userList = null;
		//设置页面容量
    	int pageSize = Constants.pageSize;
    	//当前页码
    	int currentPageNo = 1;
		
		System.out.println("queryUserName servlet--------"+queryUserName);  
		System.out.println("queryUserRole servlet--------"+queryUserRole);  
		System.out.println("query pageIndex--------- > " + pageIndex);
		if(queryUserName == null){
			queryUserName = "";
		}
		if(temp != null && !temp.equals("")){
			queryUserRole = Integer.parseInt(temp);
		}
		
    	if(pageIndex != null){		
    		currentPageNo = Integer.valueOf(pageIndex);
    	}	
    	//总数量（表）	
    	int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
    	//总页数
    	PageSupport pages=new PageSupport();
    	pages.setCurrentPageNo(currentPageNo);
    	pages.setPageSize(pageSize);
    	pages.setTotalCount(totalCount);
    	
    	int totalPageCount = pages.getTotalPageCount();
    	
    	//控制首页和尾页
    	if(currentPageNo < 1){
    		currentPageNo = 1;
    	}else if(currentPageNo > totalPageCount){
    		currentPageNo = totalPageCount;
    	}
		userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);
		request.setAttribute("userList", userList);
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		request.setAttribute("roleList", roleList);
		request.setAttribute("queryUserName", queryUserName);
		request.setAttribute("queryUserRole", queryUserRole);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "jsp/userlist";
	}
	
	@RequestMapping(value="/jsp/useradd",method=RequestMethod.POST)
	public String add(HttpServletRequest request,User user ,
			@RequestParam("idpicss")MultipartFile[] idpicss){
		System.out.println("add()================");
		user.setCreationDate(new Date());
		user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		int i = 0;
		for(MultipartFile idp : idpicss){
			if(!(idp.isEmpty())){
				   //获取用户上传的文件的文件名
				String name = idp.getOriginalFilename();
				 //获取文件的后缀
				String suffix = name.substring(name.lastIndexOf("."));
				//设置文件大小
				long size = idp.getSize();
				  //使用随机数+当前时间毫秒数+后缀 生成新的文件名
		 		   Random ran = new Random();
				   String newName = ran.nextInt(1000000)+""+System.currentTimeMillis()+suffix;
				   //获取当前项目用于保存文件的文件夹的绝对路径
				   String savePath = request.getServletContext().getRealPath("idpic");
				   //根据保存路径和新文件名创建一个用于保存的文件对象
				   File file = new File(savePath,newName);
				   //保存文件
				   try {
					   idp.transferTo(file);
					   //添加到数据库
					   if(i== 0){
						   user.setIdpic(newName);
					   }else{
						   user.setIdpic2(newName);
					   }
					   i++;
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		if(userService.add(user)){
			return "redirect:userquery";
		}else{
			return "jsp/useradd";
		}
	}	
	@RequestMapping(value="jsp/usermodify/{id}.html",method=RequestMethod.GET)
	public String usermodify(HttpServletRequest request,
			@PathVariable("id")String id){
		System.out.println("usermodify------->");
		if(!StringUtils.isNullOrEmpty(id)){
			//调用后台方法得到user对象
			User user = userService.getUserById(id);
			request.setAttribute("user", user);
			return "jsp/usermodify";
		}
		return "redirect:userquery";
	}
	@RequestMapping(value="jsp/usermodify",method=RequestMethod.POST)
	public String modify(HttpServletRequest request, User user ){
		user.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());
		if(userService.modify(user)){
			return "redirect:userquery";
		}else{
			return "jsp/usermodify";
		}
	}
	
	@RequestMapping("/jsp/checkUserCode")
	@ResponseBody
	public Object userCodeExist(String userCode){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
			User user = userService.selectUserCodeExist(userCode);
			if(null != user){
				resultMap.put("userCode","exist");
			}else{
				resultMap.put("userCode", "notexist");
			}
	//	return JSON.toJSONString(resultMap);
			return resultMap;
	}
	@RequestMapping("/jsp/usergetrolelist")
	@ResponseBody
	public Object getRoleList(){
		
		List<Role> roleList = null;		
		roleList = roleService.getRoleList();
	//	return JSON.toJSONString(roleList);
		return roleList;
	}
	@RequestMapping("/jsp/userinfo")
	@ResponseBody
	public Object getUserById(@RequestParam("uid")String id){
		User user = userService.getUserById(id);
		return user;
	}
	@RequestMapping("/jsp/userpwdmodify")
	@ResponseBody
	public Object getPwdByUserId(HttpServletRequest request,@RequestParam("oldpassword")String oldpassword){
		Object o = request.getSession().getAttribute(Constants.USER_SESSION);
		Map<String, String> resultMap = new HashMap<String, String>();
		if(null == o ){//session过期
			resultMap.put("result", "sessionerror");
		}else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
			resultMap.put("result", "error");
		}else{
			String sessionPwd = ((User)o).getUserPassword();
			if(oldpassword.equals(sessionPwd)){
				resultMap.put("result", "true");
			}else{//旧密码输入不正确
				resultMap.put("result", "false");
			}
		}
		return resultMap;
	}
	@RequestMapping("/jsp/usersavepwds")
	public String updatePwd(HttpServletRequest request,String newpassword ){		
		Object o = request.getSession().getAttribute(Constants.USER_SESSION);
		boolean flag = false;
		if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
			flag = userService.updatePwd(((User)o).getId(),newpassword);
			if(flag){
				request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
				request.getSession().removeAttribute(Constants.USER_SESSION);//session注销				
				
			}else{
				request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
			}
		}else{
			request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
		}
		return "jsp/pwdmodify";
	}
	@RequestMapping("/jsp/userdeluser")
	@ResponseBody
	public Object delUser(HttpServletRequest request,@RequestParam("uid")String id){
		
		Integer delId = 0;
		try{
			delId = Integer.parseInt(id);
		}catch (Exception e) {
			// TODO: handle exception
			delId = 0;
		}
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(delId <= 0){
			resultMap.put("delResult", "notexist");
		}else{
			if(userService.deleteUserById(delId)){
				resultMap.put("delResult", "true");
			}else{
				resultMap.put("delResult", "false");
			}
		}
		return resultMap;
	}
	
}
