package com.robotCore.feign;

import com.robotCore.common.base.vo.OrganizationVO;
import com.robotCore.common.base.vo.UserVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
public class IServiceUserFallBackFactory implements FallbackFactory<IServiceUser> {

	@Override
	public IServiceUser create(Throwable cause) {
		return new IServiceUser() {
			@Override
			public UserVO getUserVOByLoginName(String loginName) {
				cause.getMessage();
				return null;
			}
			@Override
			public Object findById(Long id) {
				return "open health failed.-------------" + cause.getMessage();
			}
			@Override
			public OrganizationVO getDeptById(String id) {
				cause.getMessage();
				return null;
			}

			@Override
			public OrganizationVO getDeptByCode(String code) {
				cause.getMessage();
				return null;
			}

			@Override
			public OrganizationVO getDeptByName(String name) {
				cause.getMessage();
				return null;
			}

			@Override
			public Object getOrgTreeById(String orgId) {
				cause.getMessage();
				return null;
			}

			@Override
			public Object getOrgTree() {
				cause.getMessage();
				return null;
			}

			@Override
			public List<UserVO> findByTeamId(String teamId) {
				return null;
			}

			@Override
			public Object findAccountsByRoleId(@RequestBody Long roleId) {
				cause.getMessage();
				return null;
			}

			@Override
			public Object findRolesByLoginName(@RequestParam("loginName") String loginName) {
				cause.getMessage();
				return null;
			}
		};
	}


}
