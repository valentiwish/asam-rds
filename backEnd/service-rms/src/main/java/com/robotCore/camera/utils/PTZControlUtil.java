package com.robotCore.camera.utils;

import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.netsdk.lib.enumeration.ENUMERROR;
import com.netsdk.lib.structure.NET_IN_PTZBASE_MOVEABSOLUTELY_INFO;
import com.sun.jna.ptr.IntByReference;

/**
 * @Des: 云台控制工具类
 * @author: zxl
 * @date: 2023/9/4
 **/
public class PTZControlUtil {

    /**
     * 云台控制--向上移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean upControlPtz(int nChannelID, int lParam1, int lParam2) {
        // 开始向上移动，若超过角度则会变为左右移动
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向上移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_UP_CONTROL,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            // 若未登录，
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向下移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean downControlPtz(int nChannelID, int lParam1, int lParam2) {
        // 开始向下移动，若超过角度则会变为左右移动
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向下移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType. NET_PTZ_DOWN_CONTROL,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向左移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean leftControlPtz(int nChannelID, int lParam1, int lParam2) {
        // 开始向左移动，若超过角度则会变为左右移动
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向左移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType. NET_PTZ_LEFT_CONTROL,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向右移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean rightControlPtz(int nChannelID, int lParam1, int lParam2) {
        // 开始向y右移动，若超过角度则会变为左右移动
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向右移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType. NET_PTZ_RIGHT_CONTROL,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向左上移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean ptzControlLeftUpStart(int nChannelID, int lParam1, int lParam2) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向左上移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTTOP,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向左下移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean ptzControlLeftDownStart(int nChannelID, int lParam1, int lParam2) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向左上移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTDOWN,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向右上移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean ptzControlRightUpStart(int nChannelID, int lParam1, int lParam2) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向左上移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTTOP,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--向右下移动
     * @param nChannelID 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 垂直/水平 移动速度 (1-8)
     */
    public static boolean ptzControlRightDownStart(int nChannelID, int lParam1, int lParam2) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始向左上移动..., 当前速度为：" + lParam2);
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTDOWN,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 获取云台精确位置信息
     * @return
     */
    public static boolean ptzQueryPreciseControlStatus() {
        int nType = NetSDKLib.NET_DEVSTATE_PTZ_LOCATION;
        NetSDKLib.NET_PTZ_LOCATION_INFO ptzLocationInfo = new NetSDKLib.NET_PTZ_LOCATION_INFO();
        IntByReference intByRetLen = new IntByReference();


        ptzLocationInfo.write();
        boolean bRet = LoginUtil.netsdk.CLIENT_QueryDevState(LoginUtil.m_hLoginHandle, nType, ptzLocationInfo.getPointer(), ptzLocationInfo.size(), intByRetLen, 3000);
        ptzLocationInfo.read();

        if (bRet) {
            System.out.println("xParam:" + ptzLocationInfo.stuAbsPosition.nPosX);
            System.out.println("yParam:" + ptzLocationInfo.stuAbsPosition.nPosY);
            System.out.println("zoomParam:" + ptzLocationInfo.stuAbsPosition.nZoom);

            System.out.println("nPTZPan:" + ptzLocationInfo.nPTZPan);
            System.out.println("nPTZTilt:" + ptzLocationInfo.nPTZTilt);
            System.out.println("nZoomMapValue:" + ptzLocationInfo.nZoomMapValue);

        } else {
            System.out.println("QueryDev Failed!" + ToolKits.getErrorCodePrint() + " " + ENUMERROR.getErrorMessage());
        }

        return bRet;
    }

    /**
     * 精确绝对移动
     * @param m_hLoginHandle 登录句柄
     * @param nChannelID 通道号
     * @param xParam X轴转角（0-3600.部分设备支持负数，请以实际测试为准）
     * @param yParam Y轴转角（早期云台支持0-900， 即90度转角，现在很多设备已支持负转角，请以实际测试为准）
     * @param zoomParam 倍率映射值
     * @return 运行是否成功
     */
    public static boolean ptzControlBaseMoveAbsolutely(NetSDKLib.LLong m_hLoginHandle, int nChannelID, int xParam, int yParam, int zoomParam) {
        NET_IN_PTZBASE_MOVEABSOLUTELY_INFO stuBaseMoveInfo = new NET_IN_PTZBASE_MOVEABSOLUTELY_INFO();
        stuBaseMoveInfo.nZoomFlag = 1;
        stuBaseMoveInfo.stuPosition.nPosX = xParam;
        stuBaseMoveInfo.stuPosition.nPosY = yParam;
        stuBaseMoveInfo.stuPosition.nZoom = zoomParam;

        stuBaseMoveInfo.write();
        if (!LoginUtil.netsdk.CLIENT_DHPTZControlEx2(m_hLoginHandle, nChannelID, NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_BASE_MOVE_ABSOLUTELY,
                0,0,0,0,stuBaseMoveInfo.getPointer())) {
            System.out.println("Move Absolutely Failed!" + ToolKits.getErrorCodePrint());
            return false;
        } else {
            System.out.println("Move Absolutely Succeed!");
            return true;
        }
    }

    /**
     * 转至预置点
     * @param nChannelID 通道id
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 预置点编号
     */
    public static boolean presetPointControlPtz(int nChannelID, int lParam1, int lParam2) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始转至预置点" + lParam2 + "移动");
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_POINT_MOVE_CONTROL,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 转至预置点并抓图
     * @param nChannelID 通道id
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 预置点编号
     */
    public static boolean toPresetPointAndCpControl(int nChannelID, int lParam1, int lParam2, int lParm3) {
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            System.out.println("开始转至预置点" + lParam2 + ",并抓图");
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_GOTOPRESETSNAP,
                    lParam1, lParam2, 0, 0);
            return true;
        } else {
            System.err.println("登录句柄不存在！");
            return false;
        }
    }

    /**
     * 云台控制--停止移动
     * @param nChannelID 通道id 默认为0
     */
    public static boolean stopUpControl(int nChannelID) {
        // 若未登录，先登录。
        if (LoginUtil.m_hLoginHandle.longValue() == 0) {
            return false;
        }
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            LoginUtil.netsdk.CLIENT_DHPTZControlEx(LoginUtil.m_hLoginHandle, nChannelID,
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_UP_CONTROL,
                    0, 0, 0, 1);
            System.out.println("停止移动，操作完成");
        }
        return true;
//        // 退出
//        logout();
//        System.out.println("退出登录...");
//        // 释放资源
//        LoginModule.cleanup();
    }

}
