package com.asam.human.service.impl;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.HumUser;
import com.asam.human.entity.UserVO;
import com.asam.human.mapper.HumUserDao;
import com.asam.human.service.HumUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.asam.common.constant.Constant.STATE_VALID;


@Service
public class HumUserServiceImpl extends ServiceImpl<HumUserDao, HumUser> implements HumUserService {

	@Autowired
	private HumUserDao humUserDao;

	@Override
	public IPage<HumUser> findPageList(JPAPage varBasePage, String name, String jobNumber, String sex, List<String> orgIds) {
		QueryWrapper<HumUser> wrapper = new QueryWrapper<>();
		wrapper.lambda().like(ObjectUtil.isNotEmpty(name),HumUser::getUserName, name);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(jobNumber),HumUser::getJobNumber, jobNumber);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(sex),HumUser::getSex, sex);
		wrapper.lambda().in(ObjectUtil.isNotEmpty(orgIds),HumUser::getOrgId, orgIds);
		wrapper.lambda().notIn(HumUser::getId, new ArrayList<>(Arrays.asList("1","2","3")));
		wrapper.lambda().eq(HumUser::getState, STATE_VALID).orderByDesc(HumUser::getCreateTime);
		IPage<HumUser> varPage = BasePage.getMybaitsPage(varBasePage);
		return page(varPage, wrapper);
	}

	@Override
	public List<HumUser> findList(String id,String loginName,String jobNumber,String userPhone,String cardId,String email,String orgId,Collection<String> orgIds,Collection<String> ids) {
		QueryWrapper<HumUser> wrapper = new QueryWrapper<>();
		wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),HumUser::getId, id);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(loginName),HumUser::getLoginName, loginName);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(jobNumber),HumUser::getJobNumber, jobNumber);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(userPhone),HumUser::getUserPhone, userPhone);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(cardId),HumUser::getCardId, cardId);
		wrapper.lambda().like(ObjectUtil.isNotEmpty(email),HumUser::getEmail, email);
		wrapper.lambda().eq(ObjectUtil.isNotEmpty(orgId),HumUser::getOrgId, orgId);
		wrapper.lambda().in(ObjectUtil.isNotEmpty(ids),HumUser::getId, ids);
		wrapper.lambda().in(ObjectUtil.isNotEmpty(orgIds),HumUser::getOrgId, orgIds);
		wrapper.lambda().notIn(HumUser::getId, new ArrayList<>(Arrays.asList("1","2","3")));
		wrapper.lambda().eq(HumUser::getState, STATE_VALID).orderByDesc(HumUser::getCreateTime);
		return list(wrapper);
	}


	@Override
	public boolean save(HumUser model, Long userId,String userName) {
		if(model.getId()==null){
			model.setCreateId(userId);
			model.setCreateUser(userName);
			model.setCreateTime(new Date());
		}else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Date());
		}
		return saveOrUpdate(model);
	}

	@Override
	public UserVO getUserByLoginName(String loginName){
		return humUserDao.findByLoginName(loginName);
	}

	@Override
	public void updateByOrgId(String orgCode, String orgName, String orgId) {
		humUserDao.updateByOrgId(orgCode,orgName,orgId);
	}
}
