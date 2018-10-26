package cn.smbms.service.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.smbms.dao.role.RoleDaoMapper;
import cn.smbms.pojo.Role;
@Service
public class RoleServiceImpl implements RoleService{
	@Autowired
	private RoleDaoMapper roleDao;
	
	public void setRoleDao(RoleDaoMapper roleDao) {
		this.roleDao = roleDao;
	}
	@Override
	public List<Role> getRoleList() {
		List<Role> roleList = null;
		try {
			roleList = roleDao.getRoleList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roleList;
	}
	
}
