package com.robotCore.robot.controller;

import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotBindAttrDTO;
import com.robotCore.robot.dto.RobotChargePileAttrDTO;
import com.robotCore.robot.entity.RobotAttribute;
import com.robotCore.robot.entity.RobotChargePile;
import com.robotCore.robot.entity.RobotDms;
import com.robotCore.robot.service.RobotAttributeService;
import com.robotCore.robot.service.RobotChargePileService;
import com.robotCore.robot.service.RobotDmsService;
import com.robotCore.scheduing.common.enums.RobotDmsAttributeTypeEnum;
import com.robotCore.scheduing.common.enums.RobotMapPointAttrEnum;
import com.robotCore.scheduing.entity.RobotWaybillDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Des: 绑定机器人属性列表的控制器类
 * @author: zxl
 * @date: 2023/6/28
 **/
@Slf4j
@RestController
@Api(value = "RobotAttributeController", description = "绑定机器人属性列表的控制器类")
@RequestMapping(value = "attribute")
public class RobotAttributeController extends BaseController {

    @Autowired
    private RobotAttributeService robotAttributeService;

    @Autowired
    private RobotDmsService robotDmsService;

    @Autowired
    private RobotChargePileService robotChargePileService;

    @ApiOperation(value = "设置机器人地图点位的属性值", notes = "设置机器人地图点位的属性值")
    @PostMapping(value = "/bindAttribute")
    public Object bindAttribute(@RequestBody RobotBindAttrDTO dto) {
        if (ObjectUtil.isNotEmpty(dto)) {
            boolean flag = true;
            for (int i = 0; i < dto.getVehicleIds().size(); i++) {
                RobotAttribute model = new RobotAttribute();
                model.setAttributeCode(dto.getCode());
                model.setAttributeName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName());
                model.setVehicleId(dto.getVehicleIds().get(i));
                model.setPoint(dto.getPoint());

                //校验该站点上是否已经绑定了相同属性值
                List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(dto.getVehicleIds().get(i), dto.getPoint(), dto.getCode());
                if (robotBindAttr.size() > 0) {
                    return R.error("该点位已经绑定了" + Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName() + ",请勿重复绑定!" );
                }
                UserVO curUser = getCurUser();//当前创建人
                boolean b = robotAttributeService.save(model,curUser.getId(),curUser.getUserName());
                flag = flag && b;
            }
            return flag ? R.ok() : R.error("绑定地图该点位的属性值失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "绑定光通信站点", notes = "绑定光通信站点")
    @PostMapping(value = "/bindDmsPoint")
    public Object bindDmsPoint(@RequestBody RobotBindAttrDTO dto) {
        if (ObjectUtil.isNotEmpty(dto)) {
            boolean flag = true;
            for (int i = 0; i < dto.getVehicleIds().size(); i++) {
                //绑定机器人点位属性值
                RobotAttribute model = new RobotAttribute();
                model.setAttributeCode(dto.getCode());
                model.setAttributeName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName());
                model.setVehicleId(dto.getVehicleIds().get(i));
                model.setPoint(dto.getPoint());

                //校验该站点上是否已经绑定了相同属性值
                List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(dto.getVehicleIds().get(i), dto.getPoint(), dto.getCode());
                if (robotBindAttr.size() > 0) {
                    return R.error("该点位已经绑定了" + Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName() + ",请勿重复绑定!" );
                }
                UserVO curUser = getCurUser();//当前创建人
                boolean attributeFlag = robotAttributeService.save(model,curUser.getId(),curUser.getUserName());
                flag = flag && attributeFlag;
            }
            //增加光通信站
            RobotDms robotDms = new RobotDms();
            robotDms.setDmsIp(dto.getDmsIp());
            robotDms.setDmsName(dto.getDmsName());
            robotDms.setDmsPoint(dto.getPoint());
            if (dto.getCode() == 5){
                robotDms.setDmsType(RobotDmsAttributeTypeEnum.DMS_POINT.getName());
            } else {
                robotDms.setDmsType(RobotDmsAttributeTypeEnum.DMS_PARK_POINT.getName());
            }
            boolean dmsFlag = robotDmsService.save(robotDms);
            return flag && dmsFlag? R.ok() : R.error("绑定地图该点位的属性值失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "绑定智能充电桩", notes = "绑定智能充电桩")
    @PostMapping(value = "/bindChargePile")
    public Object bindChargePile(@RequestBody RobotChargePileAttrDTO dto) {
        if (ObjectUtil.isNotEmpty(dto)) {
            boolean flag = true;
            for (int i = 0; i < dto.getVehicleIds().size(); i++) {
                //绑定机器人点位属性值
                RobotAttribute model = new RobotAttribute();
                model.setAttributeCode(dto.getCode());
                model.setAttributeName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName());
                model.setVehicleId(dto.getVehicleIds().get(i));
                model.setPoint(dto.getPoint());

                //校验该站点上是否已经绑定了相同属性值
                List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(dto.getVehicleIds().get(i), dto.getPoint(), dto.getCode());
                if (robotBindAttr.size() > 0) {
                    return R.error("该点位已经绑定了" + Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(dto.getCode())).getName() + ",请勿重复绑定!" );
                }
                UserVO curUser = getCurUser();//当前创建人
                boolean attributeFlag = robotAttributeService.save(model,curUser.getId(),curUser.getUserName());
                flag = flag && attributeFlag;
            }
            //增加智能充电桩
            RobotChargePile chargePile = new RobotChargePile();
            chargePile.setChargePileIp(dto.getChargePileIp());
            chargePile.setChargePileName(dto.getChargePileName());
            chargePile.setChargePilePoint(dto.getPoint());
            boolean chargePileFlag = robotChargePileService.save(chargePile);
            return flag && chargePileFlag? R.ok() : R.error("绑定地图该点位的属性值失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="根据Id删除已经绑定的属性值")
    @ApiOperation(value = "根据Id删除已经绑定的属性值")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            //根据id获取当前要删除的属性
            RobotAttribute robotAttribute = robotAttributeService.getById(id);
            //获取属性是光通信的列表
            List<RobotDms> dmsList = robotDmsService.findList(robotAttribute.getPoint(), robotAttribute.getAttributeCode());
            boolean b1 = true;
            boolean b2 = true;
            boolean b3 = false;
            if (dmsList.size() > 0) {
                List<String> ids = new ArrayList<>();
                for (RobotDms robotDms : dmsList) {
                    ids.add(robotDms.getId());
                }
                b1 = robotDmsService.removeByIds(ids);
            }
            //获取属性是智能充电桩的列表
            List<RobotChargePile> chargePileList = robotChargePileService.findListByAttribute(null, null, null, robotAttribute.getPoint());
            if (chargePileList.size() > 0) {
                List<String> ids = new ArrayList<>();
                for (RobotChargePile chargePile : chargePileList) {
                    ids.add(chargePile.getId());
                }
                b2 = robotChargePileService.removeByIds(ids);
            }
            if (b1 && b2) {
                List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(null, robotAttribute.getPoint(), robotAttribute.getAttributeCode());
                if (robotBindAttr.size() > 0) {
                    List<String> ids = new ArrayList<>();
                    for (RobotAttribute attribute : robotBindAttr) {
                        ids.add(attribute.getId());
                    }
                    b3 = robotAttributeService.removeByIds(ids);
                }
                return b3 ? R.ok() : R.error("删除属性失败");
            } else {
                return R.error("删除光通信模块信息失败");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
