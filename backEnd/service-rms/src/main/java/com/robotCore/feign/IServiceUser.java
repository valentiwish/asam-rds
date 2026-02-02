package com.robotCore.feign;

import com.robotCore.common.base.vo.OrganizationVO;
import com.robotCore.common.base.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "service-user", fallbackFactory = IServiceUserFallBackFactory.class)
public interface IServiceUser {

    @PostMapping("/human/getUserVOByLoginName")
    UserVO getUserVOByLoginName(@RequestParam("loginName") String loginName);

    @PostMapping("/human/findById")
    public Object findById(@RequestParam("id") Long id);

    @PostMapping("/organization/getDeptById")
    OrganizationVO getDeptById(@RequestParam("id") String id);

    @PostMapping("/organization/getDeptByCode")
    OrganizationVO getDeptByCode(@RequestParam("code") String code);

    @PostMapping("/organization/getDeptByName")
    OrganizationVO getDeptByName(@RequestParam("name") String name);

    @PostMapping("/organization/getOrgTreeById")
    Object getOrgTreeById(@RequestParam("orgId") String orgId);

    @PostMapping("/organization/getOrgTree")
    Object getOrgTree();

    @PostMapping("/human/findByTeamId2")
    List<UserVO> findByTeamId(@RequestParam("teamId") String teamId);

    @PostMapping("/account/findAccountsByRoleId")
    Object findAccountsByRoleId(@RequestBody Long roleId);

    @PostMapping("/role/findRolesByLoginName")
    Object findRolesByLoginName(@RequestParam("loginName") String loginName);
}
