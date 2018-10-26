package cn.smbms.service.bill;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.smbms.dao.bill.BillDaoMapper;
import cn.smbms.pojo.Bill;
@Service
public class BillServiceImpl implements BillService {
	@Autowired
	private BillDaoMapper billDao;
	
	public void setBillDao(BillDaoMapper billDao) {
		this.billDao = billDao;
	}
	@Override
	@Transactional
	public boolean add(Bill bill) {
		boolean flag = false;
			try {
				if(billDao.add(bill) > 0)
					flag = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return flag;
	}

	@Override
	public List<Bill> getBillList(Bill bill) {
		List<Bill> billList = null;
		System.out.println("query productName ---- > " + bill.getProductName());
		System.out.println("query providerId ---- > " + bill.getProviderId());
		System.out.println("query isPayment ---- > " + bill.getIsPayment());
		try {
		
			billList = billDao.getBillList( bill);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return billList;
	}

	@Override
	@Transactional
	public boolean deleteBillById(String delId) {
		boolean flag = false;
		try {
			if(billDao.deleteBillById( delId) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public Bill getBillById(String id) {
		Bill bill = null;
		try{
			bill = billDao.getBillById( id);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			bill = null;
		}
		return bill;
	}

	@Override
	@Transactional
	public boolean modify(Bill bill) {
		boolean flag = false;
		try {
			if(billDao.modify(bill) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

}
