package cn.smbms.dao.role;

import java.util.List;

import cn.smbms.pojo.Role;

public interface RoleDaoMapper {
	
	public List<Role> getRoleList()throws Exception;

}
