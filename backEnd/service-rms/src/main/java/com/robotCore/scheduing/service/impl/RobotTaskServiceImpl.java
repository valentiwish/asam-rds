package com.robotCore.scheduing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotPathPlanDTO;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotDmsEditorService;
import com.robotCore.scheduing.common.utils.HttpClientUtil;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.entity.RobotTask;
import com.robotCore.scheduing.mapper.RobotTaskDao;
import com.robotCore.scheduing.service.RobotTaskService;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.scheduing.vo.RobotTaskVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.robotCore.common.constant.Constant.STATE_VALID;

;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/5
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RobotTaskServiceImpl extends ServiceImpl<RobotTaskDao, RobotTask> implements RobotTaskService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RobotDmsEditorService robotDmsEditorService;

    @Value("${data.pathPlanningAlgorithmUrl}")
    private String algorithmUrl;

    /**
     * $占位符识别规则：
     * - $ 后接：字母/数字/下划线
     * - 示例：$jackUpPoint, $dmsPoint_1
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$[A-Za-z0-9_]+");

    @Override
    public IPage<RobotTask> findPageList(IPage<RobotTask> varPage, RobotTask robotTask) {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(robotTask)) {
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotTask.getId()), RobotTask::getId, robotTask.getId());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotTask.getTaskName()), RobotTask::getTaskName, robotTask.getTaskName());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotTask.getEnabledState()), RobotTask::getEnabledState, robotTask.getEnabledState());
        }
        wrapper.lambda().orderByAsc(RobotTask::getCreateTime);
        return this.baseMapper.selectPage(varPage, wrapper);
    }

    @Override
    public List<RobotTaskVO> getTaskInfo() {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(RobotTask::getCreateTime);
        List<RobotTaskVO> robotTaskVOS = new ArrayList<>();
        List<RobotTask> robotTaskList = list(wrapper);
        for (RobotTask robotTask : robotTaskList) {
            RobotTaskVO vo = new RobotTaskVO();
            vo.setTaskId(robotTask.getId());
            vo.setTaskName(robotTask.getTaskName());
            //获取并解析任务参数列表
            String taskParameterListStr = robotTask.getTaskParameter();
            List<TaskParameterDTO> taskParameterList = JSONObject.parseArray(taskParameterListStr, TaskParameterDTO.class);
            vo.setTaskParameter(taskParameterList);
            robotTaskVOS.add(vo);
        }
        return robotTaskVOS;
    }

    /**
     * 获取最新创建的运单
     *
     * @return
     */
    @Override
    public RobotTask getNewlyCreated() {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByDesc(RobotTask::getCreateTime).last("limit 1");
        List<RobotTask> list = list(wrapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据任务序号获取任务
     *
     * @param taskNumber
     * @return
     */
    @Override
    public RobotTask getTaskByNumber(String taskNumber) {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(taskNumber), RobotTask::getTaskNumber, taskNumber);
        List<RobotTask> list = list(wrapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 把外部传过来的数据转换成任务执行需要的参数列表
     *
     * @param reqParameterList
     * @return
     */
    @Override
    public List<TaskParameterDTO> taskParameterDataConvert(List<RobotTaskRequestDTO.SubParameter> reqParameterList) {
        List<TaskParameterDTO> taskParameterDTOS = new ArrayList<>();
        for (RobotTaskRequestDTO.SubParameter parameter : reqParameterList) {
            TaskParameterDTO parameterDTO = new TaskParameterDTO();
            parameterDTO.setVariableName(parameter.getName());
            parameterDTO.setDefaultValue(parameter.getValue());
            parameterDTO.setDataType(parameter.getType());
            taskParameterDTOS.add(parameterDTO);
        }
        return taskParameterDTOS;
    }

    @Override
    public boolean save(RobotTask model, Long userId, String userName) {
        if (model.getId() == null) {
            //设置默认的启用状态为false
            model.setEnabledState(0);
            //设置优先级
            if (model.getPriority() == null) {
                model.setPriority(1);
            }
            model.setCreateId(userId);
            model.setCreateUser(userName);
            //设置版本号
            if (model.getVersion() == null) {
                model.setVersion(1);
            }
            //设置任务序号，默认为task00001
            RobotTask newlyCreated = getNewlyCreated();
            if (newlyCreated == null) {
                model.setTaskNumber("task00001");
            } else {
                String str = newlyCreated.getTaskNumber();
                String numberStr = str.substring(str.length() - 5);
                // 字符串数字解析为整数,最新编号自增1
                int serialNumber = Integer.parseInt(numberStr) + 1;
                //将整数格式化为5位数字
                String newSerialNumber = String.format("task" + "%05d", serialNumber);
                model.setTaskNumber(newSerialNumber);
            }
            model.setCreateTime(new Timestamp(new Date().getTime()));
        } else {
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
            if (model.getVersion() == null) {
                model.setVersion(1);
            } else {
                Integer versionNo = model.getVersion() + 1;
                model.setVersion(versionNo);
            }
        }
        return saveOrUpdate(model);
    }

    @Override
    public List<RobotTask> getCheckList(String id, String name) {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id), RobotTask::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(name), RobotTask::getTaskName, name);
        wrapper.lambda().eq(RobotTask::getState, STATE_VALID).orderByDesc(RobotTask::getId);
        return list(wrapper);
    }

    @Override
    public String getTaskParameterList(String taskId) {
        QueryWrapper<RobotTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(taskId), RobotTask::getId, taskId);
        List<RobotTask> list = list(wrapper);
        return list.get(0).getTaskParameter();
    }

    /**
     * 封装请求执行算法的机器人名称以及起始点数据
     *
     * @param vehicle
     * @param startSite
     * @param endSite
     * @return
     */
    @Override
    public RobotPathPlanDTO.RobotPath encapsulateData(String vehicle, String groupName, String startSite, String endSite) {
        RobotPathPlanDTO.RobotPath robotPath = new RobotPathPlanDTO.RobotPath();
        robotPath.setRobotName(vehicle);
        robotPath.setGroupName(groupName);
        robotPath.setStartPoint(startSite);
        robotPath.setEndPoint(endSite);
        return robotPath;
    }

    /**
     * 请求执行算法 leo update
     *
     * @param pathVOList    多机器人地图的融合路径列表
     * @param robotPathList 多机器人路径信息
     * @param lockInfoList  锁定信息
     * @return 算法执行结果
     */
    @Override
    public AlgorithmRequestResDTO startPathPlanningTask(List<RobotPathVO> pathVOList, List<RobotPathPlanDTO.RobotPath> robotPathList, List<RobotPathPlanDTO.LockInfo> lockInfoList) throws Exception {
        RobotPathPlanDTO pathPlanDTO = new RobotPathPlanDTO();
        pathPlanDTO.setPathList(pathVOList);
        pathPlanDTO.setAgents(robotPathList);

        //如果两个机器人发生死锁
        pathPlanDTO.setLock(lockInfoList);

        RobotPathPlanDataDTO data = new RobotPathPlanDataDTO();
        data.setData(pathPlanDTO);
        String jsonData = JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect);
        String resStr = null;
        try {
            resStr = HttpClientUtil.clientPost(algorithmUrl, HttpMethod.POST, jsonData);
        } catch (Exception e) {
            throw new Exception("请求路径规划算法执行失败！");
        }
        return JSONObject.parseObject(resStr, AlgorithmRequestResDTO.class);
    }

    /**
     * <请求路径规划算法-指定路径导航> leo add
     *
     * @param pathVOList    多机器人地图的融合路径列表
     * @param pointSet      场景中全部运行中机器人的地图融合站点集合（用于过滤无效区域点的 mask ）
     * @param robotPathList 多机器人路径信息
     * @return 算法执行结果
     */
    @Override
    public AlgorithmRequestResDTO executePathPlanningTask(List<RobotPathVO> pathVOList, Set<String> pointSet, List<RobotPathPlanDTO.RobotPath> robotPathList) {
        RobotPathPlanDTO pathPlanDTO = new RobotPathPlanDTO();
        // 为路径规划DTO设置请求的融合地图
        pathPlanDTO.setPathList(pathVOList);
        // 为路径规划DTO设置请求的 机器人-起点-终点 信息
        pathPlanDTO.setAgents(robotPathList);

        // 获取运行场景中的管控区域列表（经 mask 过滤）
        List<List<String>> focusAreaInfo = getFocusAreaContainPointsList(pointSet);
        // 为路径规划DTO设置所需区域参数
        pathPlanDTO.setConAreas(focusAreaInfo);

        // 定义路径规划所需DTO对象并为其赋值
        RobotPathPlanDataDTO robotPathPlanDataDTO = new RobotPathPlanDataDTO();
        robotPathPlanDataDTO.setData(pathPlanDTO);
        // 将DTO对象转换为算法所需的Json字符串
        String robotPathPlanJsonData = JSON.toJSONString(robotPathPlanDataDTO, SerializerFeature.DisableCircularReferenceDetect);
        // 定义算法返回的Json字符串
        String resJsonStr;
        try {
            resJsonStr = HttpClientUtil.clientPost(algorithmUrl, HttpMethod.POST, robotPathPlanJsonData);
        } catch (Exception e) {
            log.error("请求路径规划算法执行失败！", e);
            return null;
        }
        // 解析算法返回的Json字符串并返回
        return JSONObject.parseObject(resJsonStr, AlgorithmRequestResDTO.class);
    }

    /**
     * <获取运行场景中的管控区域列表: 外层为区域列表 | 内层为该区域包含的点列表> leo add
     *
     * @param pointSet 多机器人地图的融合站点集合（充当过滤无效区域点的 mask ）
     * @return 算法所需管控区域（过滤无效区域点）
     */
    private List<List<String>> getFocusAreaContainPointsList(Set<String> pointSet) {
        /*
         * 1.合并全部类型的管控区域
         */
        // 1.1 获取一般管控区域列表
        List<RobotDmsEditor> editorList = robotDmsEditorService.findAreasByType(RobotDmsEditor.AreaType.FOCUS);
        // 1.2 获取自动门区域列表
        List<RobotDmsEditor> doorAreaList = robotDmsEditorService.findAreasByType(RobotDmsEditor.AreaType.AUTO_DOOR);
        // 1.3 获取电梯区域列表
        List<RobotDmsEditor> elevatorAreaList = robotDmsEditorService.findAreasByType(RobotDmsEditor.AreaType.ELEVATOR);
        // 1.4 合并以上管控区域
        editorList.addAll(doorAreaList);
        editorList.addAll(elevatorAreaList);

        // 2. 处理空结果
        if (editorList.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 解析并过滤无效站点，返回 List<List<String>> 形式的区域请求参数
        return editorList.stream()
                .map(editor -> {
                    // 处理空JSON情况
                    String jsonStr = editor.getAreaContainPoints();
                    if (StringUtils.isEmpty(jsonStr)) {
                        return Collections.<String>emptyList();
                    }
                    // 解析JSON并过滤非法站点
                    List<String> parsedPoints = JSON.parseArray(jsonStr, String.class);
                    return Optional.ofNullable(parsedPoints)
                            .orElse(Collections.emptyList())
                            .stream()
                            // 过滤保留有效站点
                            .filter(pointSet::contains)
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取当前任务中指定的机器人
     *
     * @param id
     * @return
     */
    @Override
    public String getRobotName(String id) throws Exception {
        //获取任务内容
        String taskContent = getById(id).getTaskContent();
        if (taskContent == null || "".equals(taskContent)) {
            throw new Exception("请检查任务是否输入！");
        }
        String substring = taskContent.substring(1);
        String taskStr = substring.substring(0, substring.length() - 1);
        RobotTaskDTO model = JSON.parseObject(taskStr, RobotTaskDTO.class);
        RobotTaskDTO.SubParameter[] parameters = model.getParameters();
        String vehicle = null;
        for (RobotTaskDTO.SubParameter parameter : parameters) {
            if ("vehicle".equals(parameter.getId())) {
                vehicle = parameter.getValue();
            }
        }
        return vehicle;
    }

    @Override
    public boolean jackLoad(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_OTHER_PORT)) {
            //连接机器人其它端口（顶升和顶降）
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, ProtocolConstant.JACK_LOAD);
        return result.isSuccess();
    }

    @Override
    public boolean jackUnload(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_OTHER_PORT)) {
            //连接机器人其它端口（顶升和顶降）
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, ProtocolConstant.JACK_UPLOAD);
        return result.isSuccess();
    }

    /**
     * <通用JSON占位符替换方法>
     *
     * - 支持“字符串内部 $xxx 替换”可选开关（默认打开）
     * - 不做任何业务语义解析（不识别 action/task/param 等）
     * - 支持任意层级嵌套 JSON Object / Array
     *
     * @param rawJson               原始 JSON 字符串（任意结构）
     * @param dragMap               占位符映射表
     * @param enableInlineReplace   是否开启“字符串内部替换”（默认true）
     * @return 替换后的 JSON 字符串
     */
    public String refactorJson(String rawJson,
                                      Map<String, String> dragMap,
                                      boolean enableInlineReplace) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            throw new IllegalArgumentException("rawJson 不能为空");
        }
        if (dragMap == null || dragMap.isEmpty()) {
            return rawJson;
        }

        Object root = parseJson(rawJson);
        Object replaced = processValue(root, dragMap, enableInlineReplace);
        return JSON.toJSONString(replaced);
    }

    /**
     * 重载：默认开启“字符串内部替换”
     */
    public String refactorJson(String rawJson, Map<String, String> dragMap) {
        return refactorJson(rawJson, dragMap, true);
    }

    /**
     * 解析 JSON（Object/Array）
     */
    private Object parseJson(String json) {
        String s = json.trim();
        if (s.startsWith("{")) {
            return JSON.parseObject(s);
        }
        if (s.startsWith("[")) {
            return JSON.parseArray(s);
        }
        throw new IllegalArgumentException("非法 JSON 字符串（必须以 { 或 [ 开头）");
    }

    /**
     * 递归处理任意 JSON 节点
     */
    private Object processValue(Object value,
                                       Map<String, String> dragMap,
                                       boolean enableInlineReplace) {
        if (value instanceof JSONObject) {
            return processObject((JSONObject) value, dragMap, enableInlineReplace);
        }
        if (value instanceof JSONArray) {
            return processArray((JSONArray) value, dragMap, enableInlineReplace);
        }
        if (value instanceof String) {
            return processString((String) value, dragMap, enableInlineReplace);
        }
        return value;
    }

    private JSONObject processObject(JSONObject obj,
                                            Map<String, String> dragMap,
                                            boolean enableInlineReplace) {
        JSONObject result = new JSONObject();
        Set<String> keys = obj.keySet();
        for (String key : keys) {
            Object v = obj.get(key);
            result.put(key, processValue(v, dragMap, enableInlineReplace));
        }
        return result;
    }

    private JSONArray processArray(JSONArray array,
                                          Map<String, String> dragMap,
                                          boolean enableInlineReplace) {
        JSONArray result = new JSONArray();
        for (Object v : array) {
            result.add(processValue(v, dragMap, enableInlineReplace));
        }
        return result;
    }

    /**
     * 字符串替换规则：
     * - enableInlineReplace=false：仅当整个字符串以 $ 开头时替换
     *   "$abc" -> dragMap.get("abc")
     *
     * - enableInlineReplace=true：字符串内部所有 $xxx 都替换（默认）
     */
    private Object processString(String value,
                                        Map<String, String> dragMap,
                                        boolean enableInlineReplace) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 严格模式：只替换整串 "$xxx"
        if (!enableInlineReplace) {
            if (value.startsWith("$")) {
                String key = value.substring(1);
                if (dragMap.containsKey(key)) {
                    return dragMap.get(key);
                }
            }
            return value;
        }

        // 默认模式：替换字符串内部所有 $xxx
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String token = matcher.group();
            String key = token.substring(1);
            String replacement = dragMap.get(key);

            if (replacement == null) {
                // 没有映射：保持原样
                matcher.appendReplacement(sb, Matcher.quoteReplacement(token));
            } else {
                // 有映射：替换
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * <构造仙工自定义动作任务链> leo add
     *
     * @param name        任务链名称
     * @param stringValue 自定义动作的JSON字符串
     * @return 重构后的任务链DTO对象
     */
    @Override
    public RobotTaskListDTO reconstructRobotTaskSeer(String name, String stringValue) {
        // 创建最外层DTO对象
        RobotTaskListDTO dto = new RobotTaskListDTO();

        // 设置顶层name字段
        dto.setName(name);

        // 创建单个TaskDTO对象
        TaskDTO task = new TaskDTO();
        task.setTaskId(0);
        task.setDesc(name + "描述");  // 拼接描述字段
        task.setChecked(true);

        // 创建ActionGroup对象
        TaskDTO.ActionGroup actionGroup = new TaskDTO.ActionGroup();
        actionGroup.setActionGroupName("group 1");
        actionGroup.setActionGroupId(0);
        actionGroup.setChecked(false);

        // 创建Action对象
        TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();
        action.setActionName("move_action");
        action.setPluginName("MoveFactory");
        action.setIgnoreReturn(false);
        action.setOvertime(0);
        action.setExternalOverId(-1);
        action.setNeedResult(false);
        action.setSleepTime(0);
        action.setActionId(0);

        // 创建参数列表
        TaskDTO.ActionGroup.Action.Param skillParam = new TaskDTO.ActionGroup.Action.Param();
        skillParam.setKey("skill_name");
        skillParam.setStringValue("Action");  // 固定值

        TaskDTO.ActionGroup.Action.Param bodyParam = new TaskDTO.ActionGroup.Action.Param();
        bodyParam.setKey("body");
        bodyParam.setStringValue(stringValue);  // 使用传入的stringValue

        // 将参数添加到action
        action.setParams(Arrays.asList(skillParam, bodyParam));

        // 将action添加到actionGroup
        actionGroup.setActions(Arrays.asList(action));

        // 将actionGroup添加到task
        task.setActionGroups(Arrays.asList(actionGroup));

        // 将task添加到DTO
        dto.setTasks(Arrays.asList(task));

        // 设置loop标志
        dto.setLoop(false);

        return dto;
    }

    /**
     * <构造广义自定义动作任务链> leo add
     * <p>
     * 方法已被更新：
     * - 入参 jsonString 现在是 refactorJson() 处理后的结果（不在此方法内做 $ 替换）
     * - 自动按 commands 顺序生成 actionGroupName: "group 1", "group 2", ...
     * - actionGroupId 从 0 递增，与 commands 下标一致
     * - 采用严格模板：CUSTOM_ACTION.body 必须是 JSON 对象/数组（禁止 JSON 字符串）
     * - 支持三种指令类型：CUSTOM_ACTION / GOTO_POSE / WAIT_DI
     * - commands 可任意次数、任意顺序组合
     *
     * @param name       任务链名称（写入 dto.name）
     * @param jsonString refactorJson 后的 JSON 字符串（原 customizedActionsRawJson 替换 $ 后的结果）
     * @return 重构后的任务链DTO对象
     */
    @Override
    public RobotTaskListDTO reconstructRobotTask(String name, String jsonString) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("jsonString 不能为空（应为 refactorJson 处理后的 JSON）");
        }

        JSONObject root;
        try {
            root = JSON.parseObject(jsonString);
        } catch (Exception e) {
            throw new IllegalArgumentException("jsonString 必须是合法 JSON Object: " + e.getMessage(), e);
        }

        String desc = root.getString("desc");
        if (desc == null) desc = "";

        JSONArray commands = root.getJSONArray("commands");
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("jsonString 缺少非空数组: commands");
        }

        RobotTaskListDTO dto = new RobotTaskListDTO();
        dto.setName(name);
        dto.setLoop(false);

        TaskDTO task = new TaskDTO();
        task.setTaskId(0);
        task.setDesc(desc);
        task.setChecked(false);

        List<TaskDTO.ActionGroup> actionGroups = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            Object item = commands.get(i);
            if (!(item instanceof JSONObject)) {
                throw new IllegalArgumentException("commands[" + i + "] 必须是 JSON Object");
            }
            JSONObject cmd = (JSONObject) item;

            CommandType type = parseCommandType(cmd, i);

            TaskDTO.ActionGroup group;
            switch (type) {
                case CUSTOM_ACTION:
                    group = buildCustomActionGroup(cmd, i);
                    break;
                case GOTO_POSE:
                    group = buildGotoPoseGroup(cmd, i);
                    break;
                case WAIT_DI:
                    group = buildWaitDIGroup(cmd, i);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的指令类型: " + type);
            }
            actionGroups.add(group);
        }

        task.setActionGroups(actionGroups);
        dto.setTasks(Collections.singletonList(task));
        return dto;
    }

    // ============================
    // 3) 指令类型解析
    // ============================

    private enum CommandType {
        CUSTOM_ACTION,
        GOTO_POSE,
        WAIT_DI
    }

    private CommandType parseCommandType(JSONObject cmd, int index) {
        String t = cmd.getString("type");
        if (t == null || t.trim().isEmpty()) {
            t = cmd.getString("commandType");
        }
        if (t == null || t.trim().isEmpty()) {
            throw new IllegalArgumentException("commands[" + index + "] 缺少字段: type（或 commandType）");
        }

        String norm = t.trim().toUpperCase(Locale.ROOT);
        switch (norm) {
            case "CUSTOM_ACTION":
            case "CUSTOM":
            case "ACTION":
                return CommandType.CUSTOM_ACTION;

            case "GOTO_POSE":
            case "GOTO":
            case "GOTO_SPECIFIED_POSE":
            case "GOTOSPECIFIEDPOSE":
                return CommandType.GOTO_POSE;

            case "WAIT_DI":
            case "WAIT":
            case "WAITDI":
                return CommandType.WAIT_DI;

            default:
                throw new IllegalArgumentException("commands[" + index + "] type 不支持: " + t
                        + "（支持: CUSTOM_ACTION / GOTO_POSE / WAIT_DI）");
        }
    }

    // ============================
    // 4) 三类指令 -> 固定动作模板
    // ============================

    private TaskDTO.ActionGroup buildCustomActionGroup(JSONObject cmd, int index) {
        // 严格：只允许 JSON 对象/数组
        String bodyString = readBodyAsJsonStringStrict(cmd, "body", index);

        TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();
        action.setActionName("move_action");
        action.setPluginName("MoveFactory");
        fillCommonActionDefaults(action);

        TaskDTO.ActionGroup.Action.Param p1 = new TaskDTO.ActionGroup.Action.Param();
        p1.setKey("skill_name");
        p1.setStringValue("Action");

        TaskDTO.ActionGroup.Action.Param p2 = new TaskDTO.ActionGroup.Action.Param();
        p2.setKey("body");
        p2.setStringValue(bodyString);

        action.setParams(Arrays.asList(p1, p2));

        // ✅ 按顺序生成: group 1 / group 2 ...
        return buildSingleActionGroup("group " + (index + 1), index, action);
    }

    private TaskDTO.ActionGroup buildGotoPoseGroup(JSONObject cmd, int index) {
        String targetName = cmd.getString("targetName");
        if (targetName == null || targetName.trim().isEmpty()) {
            targetName = cmd.getString("target_name"); // 兼容
        }
        if (targetName == null || targetName.trim().isEmpty()) {
            throw new IllegalArgumentException("commands[" + index + "] (GOTO_POSE) 缺少字段: targetName（或 target_name）");
        }

        TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();
        action.setActionName("move_action");
        action.setPluginName("MoveFactory");
        fillCommonActionDefaults(action);

        TaskDTO.ActionGroup.Action.Param p1 = new TaskDTO.ActionGroup.Action.Param();
        p1.setKey("skill_name");
        p1.setStringValue("GotoSpecifiedPose");

        TaskDTO.ActionGroup.Action.Param p2 = new TaskDTO.ActionGroup.Action.Param();
        p2.setKey("target_name");
        p2.setStringValue(targetName);

        action.setParams(Arrays.asList(p1, p2));

        return buildSingleActionGroup("group " + (index + 1), index, action);
    }

    private TaskDTO.ActionGroup buildWaitDIGroup(JSONObject cmd, int index) {
        Integer diIndex = readRequiredInteger(cmd, "index", index);
        Boolean diValue = readRequiredBoolean(cmd, "value", index);

        TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();
        action.setActionName("action_waitDI");
        action.setPluginName("TaskManager");
        fillCommonActionDefaults(action);

        TaskDTO.ActionGroup.Action.Param p1 = new TaskDTO.ActionGroup.Action.Param();
        p1.setKey("index");
        p1.setInt32Value(diIndex);

        TaskDTO.ActionGroup.Action.Param p2 = new TaskDTO.ActionGroup.Action.Param();
        p2.setKey("value");
        p2.setBoolValue(diValue);

        action.setParams(Arrays.asList(p1, p2));

        return buildSingleActionGroup("group " + (index + 1), index, action);
    }

    // ============================
    // 5) 不变项默认值
    // ============================

    private void fillCommonActionDefaults(TaskDTO.ActionGroup.Action action) {
        action.setIgnoreReturn(false);
        action.setOvertime(0);
        action.setExternalOverId(-1);
        action.setNeedResult(false);
        action.setSleepTime(0);
        action.setActionId(0);
    }

    private TaskDTO.ActionGroup buildSingleActionGroup(String groupName, int groupId, TaskDTO.ActionGroup.Action action) {
        TaskDTO.ActionGroup group = new TaskDTO.ActionGroup();
        group.setActionGroupName(groupName); // "group 1" / "group 2" ...
        group.setActionGroupId(groupId);     // 0 / 1 / 2 ...
        group.setChecked(false);
        group.setActions(Collections.singletonList(action));
        return group;
    }

    // ============================
    // 6) 字段读取工具（严格 body）
    // ============================

    /**
     * 严格版：CUSTOM_ACTION.body 必须是 JSON 对象/数组
     * - 允许: { ... } 或 [ ... ]
     * - 禁止: 字符串（避免双重转义与 $ 替换失效问题）
     */
    private String readBodyAsJsonStringStrict(JSONObject cmd, String bodyField, int index) {
        Object body = cmd.get(bodyField);
        if (body == null) {
            throw new IllegalArgumentException("commands[" + index + "] (CUSTOM_ACTION) 缺少字段: " + bodyField);
        }
        if (body instanceof JSONObject || body instanceof JSONArray) {
            return JSON.toJSONString(body);
        }
        throw new IllegalArgumentException(
                "commands[" + index + "] (CUSTOM_ACTION) body 必须是 JSON对象/数组，不允许使用字符串"
        );
    }

    private Integer readRequiredInteger(JSONObject cmd, String field, int index) {
        Object v = cmd.get(field);
        if (v == null) {
            throw new IllegalArgumentException("commands[" + index + "] 缺少字段: " + field);
        }
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) {
            String s = ((String) v).trim();
            if (s.isEmpty()) throw new IllegalArgumentException("commands[" + index + "] 字段为空: " + field);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("commands[" + index + "] " + field + " 不是合法整数: " + s);
            }
        }
        throw new IllegalArgumentException("commands[" + index + "] " + field + " 类型不支持，必须为整数或可转整数的字符串");
    }

    private Boolean readRequiredBoolean(JSONObject cmd, String field, int index) {
        Object v = cmd.get(field);
        if (v == null) {
            throw new IllegalArgumentException("commands[" + index + "] 缺少字段: " + field);
        }
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) {
            String s = ((String) v).trim().toLowerCase(Locale.ROOT);
            if ("true".equals(s)) return true;
            if ("false".equals(s)) return false;
            throw new IllegalArgumentException("commands[" + index + "] " + field + " 不是合法布尔值: " + v);
        }
        throw new IllegalArgumentException("commands[" + index + "] " + field + " 类型不支持，必须为布尔或 'true/false' 字符串");
    }
}
