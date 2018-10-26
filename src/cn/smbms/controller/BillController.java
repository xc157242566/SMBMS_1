package cn.smbms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
@Controller
public class BillController {
	@Autowired
	private ProviderService providerService;
	@Autowired
	private BillService billService;
	
	public void setBillService(BillService billService) {
		this.billService = billService;
	}

	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}

	@RequestMapping("/jsp/billquery")
	private String query(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="queryProductName",required=false)String queryProductName,
			@RequestParam(value="queryProviderId",required=false)String queryProviderId,
			@RequestParam(value="queryIsPayment",required=false)String queryIsPayment )
			throws ServletException, IOException {	
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList("","");
		request.setAttribute("providerList", providerList);
		if(StringUtils.isNullOrEmpty(queryProductName)){
			queryProductName = "";
		}
		List<Bill> billList = new ArrayList<Bill>();
		Bill bill = new Bill();
		if(StringUtils.isNullOrEmpty(queryIsPayment)){
			bill.setIsPayment(0);
		}else{
			bill.setIsPayment(Integer.parseInt(queryIsPayment));
		}
		
		if(StringUtils.isNullOrEmpty(queryProviderId)){
			bill.setProviderId(0);
		}else{
			bill.setProviderId(Integer.parseInt(queryProviderId));
		}
		bill.setProductName(queryProductName);
		billList = billService.getBillList(bill);
		request.setAttribute("billList", billList);
		request.setAttribute("queryProductName", queryProductName);
		request.setAttribute("queryProviderId", queryProviderId);
		request.setAttribute("queryIsPayment", queryIsPayment);		
		return "jsp/billlist";
		
	}
	@RequestMapping(value="/jsp/billmodify/{id}.html",method=RequestMethod.GET)
	private String getBillById(HttpServletRequest request,
			@PathVariable("id")String id){		
		if(!StringUtils.isNullOrEmpty(id)){			
			Bill bill = null;
			bill = billService.getBillById(id);
			request.setAttribute("bill", bill);
			return "jsp/billmodify";
		}
		return "redirect:billquery";
	}
	@RequestMapping(value="/jsp/billmodify",method=RequestMethod.POST)
	private String modify(HttpServletRequest request,
			Bill bill){		
		System.out.println("modify===============");
		bill.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setModifyDate(new Date());
		boolean flag = false;
		flag = billService.modify(bill);
		if(flag){
			return "redirect:billquery";
		}else{
			return "jsp/billmodify";
		}
	}
	@RequestMapping(value="/jsp/billadd",method=RequestMethod.POST)
	private String add(HttpServletRequest request,
			Bill bill){
		bill.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setCreationDate(new Date());
		boolean flag = false;
		flag = billService.add(bill);
		System.out.println("add flag -- > " + flag);
		if(flag){
			return "redirect:billquery";
		}else{
			return "jsp/billadd";
		}	
	}
	@RequestMapping("/jsp/billgetproviderlist")
	@ResponseBody
	public Object getProviderlist(HttpServletResponse response){
		System.out.println("getproviderlist ========================= ");
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList("","");
	//	return JSON.toJSONString(providerList);
		return providerList;
		
	}
	
	@RequestMapping("/jsp/billdelbill")
	@ResponseBody
	public Object delBill(HttpServletRequest request,@RequestParam("billid")String id){
		 
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(!StringUtils.isNullOrEmpty(id)){
			boolean flag = billService.deleteBillById(id);
			if(flag){//删除成功
				resultMap.put("delResult", "true");
			}else{//删除失败
				resultMap.put("delResult", "false");
			}
		}else{
			resultMap.put("delResult", "notexit");
		}
		return resultMap;
	}
	@RequestMapping("/jsp/billinfo")
	@ResponseBody
	public Object getBillById(@RequestParam("billid")String id ) {		
			Bill bill = null;
			bill = billService.getBillById(id);
			return bill;
		}
}
