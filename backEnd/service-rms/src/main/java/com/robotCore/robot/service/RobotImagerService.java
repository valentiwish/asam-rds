package com.robotCore.robot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;
import com.robotCore.robot.entity.RobotImager;
import com.robotCore.robot.entityVo.ImagerByAddVO;
import com.robotCore.robot.entityVo.ImagersAddVO;

import java.util.List;

public interface RobotImagerService extends IService<RobotImager> {
    IPage<RobotImager> findPageList(JPAPage varBasePage,String robotId, String imagerIp, String imagerName);

    boolean save(RobotImager model, Long userId, String userName);

    List<RobotImager> findListAll();

    List<ImagerByAddVO> getAddImager();

    RobotImager setImagerValue(ImagerByAddVO imagerByAddVO, ImagersAddVO imagersAddVO);

    List<RobotImager> findList(String id, String imagerName);
}
