package cn.smbms.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
/**
 * 拦截器
 */
import cn.smbms.pojo.User;
import cn.smbms.tools.Constants;

public class SessionInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		User user = (User)request.getSession().getAttribute(Constants.USER_SESSION);
		if(user!=null){
			return true;
		}else{
			response.sendRedirect(request.getServletContext().getContextPath()+"/error.jsp");
			return false;
		}
	}
	
}
