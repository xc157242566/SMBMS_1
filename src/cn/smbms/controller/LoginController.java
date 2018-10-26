package cn.smbms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
@Controller
public class LoginController {
	@Autowired
	private UserService userService ;	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value="/login.do",method=RequestMethod.POST)
	public String doPost(String userCode,String userPassword,HttpServletRequest request){
		System.out.println("login ============ " );
		
		User user = userService.login(userCode,userPassword);
		if(null != user){//登录成功
			//放入session
			request.getSession().setAttribute(Constants.USER_SESSION, user);			
	//		response.sendRedirect("jsp/frame.jsp");
			return "redirect:jsp/frame";
		}else{		
			request.setAttribute("error", "用户名或密码不正确");
			return "login";
		}
	}
	
	
//	@ExceptionHandler		//异常处理
//	public String exception(HttpServletRequest request,Exception ex){
//		request.setAttribute("error", ex.getMessage());
//		return "error";
//	}
}
