package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotGroup;
import com.robotCore.robot.mapper.RobotGroupDao;
import com.robotCore.robot.service.RobotGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;



/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/17
 **/
@Slf4j
@Service
public class RobotGroupServiceImpl extends ServiceImpl<RobotGroupDao, RobotGroup> implements RobotGroupService {

    @Override
    public List<RobotGroup> findList() {
        QueryWrapper<RobotGroup> wrapper = new QueryWrapper<>();
        return list(wrapper);//返回list类型对象列表
    }

    @Override
    public List<RobotGroup> findList(String id, String name) {
        QueryWrapper<RobotGroup> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotGroup::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(name),RobotGroup::getGroupName, name);
        return list(wrapper);
    }

    @Override
    public boolean save(RobotGroup model, Long userId, String userName) {
        if(model.getId()==null){
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Timestamp(new Date().getTime()));
        }else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
        }
        return saveOrUpdate(model);
    }

}
