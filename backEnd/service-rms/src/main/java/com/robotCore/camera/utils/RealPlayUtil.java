package com.robotCore.camera.utils;

import com.netsdk.common.SavePath;
import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.awt.*;

/**
 * @Des: 视频实时预览
 * @author: zxl
 * @date: 2023/9/4
 **/
public class RealPlayUtil {

    // 预览句柄
    public static NetSDKLib.LLong m_hPlayHandleOne = new NetSDKLib.LLong(0);

    public static NetSDKLib.LLong m_hPlayHandleTwo = new NetSDKLib.LLong(0);

    /**
     * 开始普通视频预览
     * @param channel
     * @param stream
     * @param realPlayWindow
     * @return
     */
    public static boolean startRealPlayOne(int channel, int stream, Panel realPlayWindow) {

        // 若未登录，先登录。
        if (LoginUtil.m_hLoginHandle.longValue() == 0) {
            return false;
        }

        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            m_hPlayHandleOne = LoginUtil.netsdk.CLIENT_RealPlayEx(LoginUtil.m_hLoginHandle, channel, Native.getComponentPointer(realPlayWindow), stream);
            if(m_hPlayHandleOne.longValue() == 0) {
                System.err.println("开始实时预览失败，错误码" + ToolKits.getErrorCodePrint());
                return false;
            } else {
                System.out.println("Success to start realplay");
                LoginUtil.netsdk.CLIENT_SetRealDataCallBackEx(m_hPlayHandleOne, CbfRealDataCallBackEx.getInstance(),null, 31);
            }
        }
        return true;
    }

    /**
     * 暂停普通预览
     */
    public static boolean stopRealPlayOne() {
        if(m_hPlayHandleOne.longValue() == 0) {
            return false;
        }
        boolean bRet = LoginUtil.netsdk.CLIENT_StopRealPlayEx(m_hPlayHandleOne);
        if(bRet) {
            m_hPlayHandleOne.setValue(0);
        }
        return bRet;
    }

    /**
     * 暂停预览
     * @param m_hPlayHandle
     */
    public static boolean stopRealPlay(NetSDKLib.LLong m_hPlayHandle) {
        if(m_hPlayHandle.longValue() == 0) {
            return false;
        }
        boolean bRet = LoginUtil.netsdk.CLIENT_StopRealPlayEx(m_hPlayHandle);
        if(bRet) {
            m_hPlayHandle.setValue(0);
        }
        return bRet;
    }

    /**
     * 保存普通预览数据
     */
    public static boolean saveRealPlayOne() {
        if(m_hPlayHandleOne.longValue() == 0) {
            return false;
        }
        String strFileName = SavePath.getSavePath().getSaveRecordFilePath();
        return LoginUtil.netsdk.CLIENT_SaveRealData(m_hPlayHandleOne, strFileName);
    }

    /**
     * 暂停保存普通预览数据
     * @return
     */
    public static boolean stopSaveRealPlayOne() {
        if (m_hPlayHandleOne.longValue() == 0) {
            return false;
        }
        return LoginUtil.netsdk.CLIENT_StopSaveRealData(m_hPlayHandleOne);
    }

    /**
     * 实时预览数据回调函数--扩展(pBuffer内存由SDK内部申请释放)
     */
    private static class CbfRealDataCallBackEx implements NetSDKLib.fRealDataCallBackEx {
        private CbfRealDataCallBackEx() {
        }

        private static class CallBackHolder {
            private static CbfRealDataCallBackEx instance = new CbfRealDataCallBackEx();
        }

        public static CbfRealDataCallBackEx getInstance() {
            return CbfRealDataCallBackEx.CallBackHolder.instance;
        }

        @Override
        public void invoke(NetSDKLib.LLong lRealHandle, int dwDataType, Pointer pBuffer,
                           int dwBufSize, int param, Pointer dwUser) {
            int bInput=0;
            if(0 != lRealHandle.longValue())
            {
                switch(dwDataType) {
                    case 0:
                        System.out.println("码流大小为" + dwBufSize + "\n" + "码流类型为原始音视频混合数据");
                        break;
                    case 1:
                        //标准视频数据

                        break;
                    case 2:
                        //yuv 数据

                        break;
                    case 3:
                        //pcm 音频数据

                        break;
                    case 4:
                        //原始音频数据

                        break;
                    default:
                        break;
                }
            }
        }
    }


}
