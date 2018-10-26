package cn.smbms.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;

@Controller
public class ProviderController {
	
	@Autowired
	private ProviderService providerService;
	
		public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}

		@RequestMapping("/jsp/providerquery")
		private String query(HttpServletRequest request, HttpServletResponse response,
				@RequestParam(value="queryProName",required=false)String queryProName,
				@RequestParam(value="queryProCode",required=false)String queryProCode)
					throws ServletException, IOException {
			
			if(StringUtils.isNullOrEmpty(queryProName)){
				queryProName = "";
			}
			if(StringUtils.isNullOrEmpty(queryProCode)){
				queryProCode = "";
			}
			List<Provider> providerList = new ArrayList<Provider>();
			 
			providerList = providerService.getProviderList(queryProName,queryProCode);
			request.setAttribute("providerList", providerList);
			request.setAttribute("queryProName", queryProName);
			request.setAttribute("queryProCode", queryProCode);
			return "jsp/providerlist";
		}
		@RequestMapping("/jsp/addprovider")
		private String add(HttpServletRequest request,MultipartFile companyLP,Provider provider){
			System.out.println("addprovider-------->");
			provider.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
			provider.setCreationDate(new Date());
			if(!companyLP.isEmpty()){
				 //获取用户上传的文件的文件名
				String name = companyLP.getOriginalFilename();
				 //获取文件的后缀
				String suffix = name.substring(name.lastIndexOf("."));
				//设置文件大小
				long size = companyLP.getSize();
				//使用随机数+当前时间毫秒数+后缀 生成新的文件名
				 Random ran = new Random();
				 String newName = ran.nextInt(1000000)+""+System.currentTimeMillis()+suffix;
				  //获取当前项目用于保存文件的文件夹的绝对路径
				 String savePath = request.getServletContext().getRealPath("idpic");
				//根据保存路径和新文件名创建一个用于保存的文件对象
				 File fiel = new File(savePath,newName);
				 try {
					companyLP.transferTo(fiel);
					provider.setCompanyLicPicPath(newName);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			boolean flag = false;			
			flag = providerService.add(provider);
			if(flag){
				return "redirect:providerquery";
			}else{
				return "jsp/addprovider";
			}
		}
		@RequestMapping(value="/jsp/providermodify/{id}.html",method=RequestMethod.GET)
		private String providermodify(HttpServletRequest request,
				@PathVariable("id")String id)
				{		
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			if(!StringUtils.isNullOrEmpty(id)){		
				Provider provider = null;
				provider = providerService.getProviderById(id);
				request.setAttribute("provider", provider);				
				return "jsp/providermodify";
			
			}
			return "redirect:providerquery";
		}
		
		@RequestMapping(value="/jsp/providermodify",method=RequestMethod.POST)
		private String modify(HttpServletRequest request,
				Provider provider){
			provider.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
			provider.setModifyDate(new Date());
			boolean flag = false;
			flag = providerService.modify(provider);
			if(flag){
				return "redirect:providerquery";
			}else{
				return "jsp/providermodify";
			}
		}
		@RequestMapping("/jsp/providerdelprovider")
		@ResponseBody
		public Object delProvider(@RequestParam("proid")String id,HttpServletRequest request)
				throws ServletException, IOException {
			HashMap<String, String> resultMap = new HashMap<String, String>();
			if(!StringUtils.isNullOrEmpty(id)){
				int flag = providerService.deleteProviderById(id);
				if(flag == 0){//删除成功
					resultMap.put("delResult", "true");
				}else if(flag == -1){//删除失败
					resultMap.put("delResult", "false");
				}else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
					resultMap.put("delResult", String.valueOf(flag));
				}
			}else{
				resultMap.put("delResult", "notexit");
			}
			return resultMap;
		}
		@RequestMapping("/jsp/providerinfo")
		@ResponseBody
		public Object getProviderById(@RequestParam("proid")String id){
			Provider provider = null;
			provider = providerService.getProviderById(id);
			return provider;
		}
}
