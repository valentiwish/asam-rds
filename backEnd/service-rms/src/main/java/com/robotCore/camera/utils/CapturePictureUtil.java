package com.robotCore.camera.utils;

import com.netsdk.common.SavePath;
import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @Des:  抓图功能
 * @author: zxl
 * @date: 2023/9/4
 **/
public class CapturePictureUtil {

    //抓图功能的回调函数
    private static fCaptureReceiveCB m_CaptureReceiveCB = new fCaptureReceiveCB();

    /**
     * 封装抓图方法
     * @param chn 通道id
     * @param mode 请求一帧 默认 0
     * @param interval 时间单位秒：默认 0
     */
    public static boolean capturePicture(int chn, int mode, int interval) {

        // 若未登录，先登录。
        if (LoginUtil.m_hLoginHandle.longValue() == 0) {
           return false;
        }

        // 登录成功，截图
        if (LoginUtil.m_hLoginHandle.longValue() != 0) {
            snapPicture(chn, mode, interval);
        }
        try {
            synchronized (fCaptureReceiveCB.class) {
                // 默认等待 3s, 防止设备断线时抓拍回调没有被触发，而导致死等
                fCaptureReceiveCB.class.wait(3000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--> " + Thread.currentThread().getName() + " CLIENT_SnapPictureEx Success." + System.currentTimeMillis());
        return true;
    }

    /**
     * 抓图方法
     * @param chn 通道号
     * @param mode 默认 0
     * @param interval 默认0
     * @return 成功返回ture
     */
    private static boolean snapPicture(int chn, int mode, int interval) {
        setSnapRevCallBack(m_CaptureReceiveCB);

        // send caputre picture command to device
        NetSDKLib.SNAP_PARAMS stuSnapParams = new NetSDKLib.SNAP_PARAMS();
        stuSnapParams.Channel = chn; // channel
        stuSnapParams.mode = mode; // capture picture mode
        stuSnapParams.Quality = 3; // picture quality
        stuSnapParams.InterSnap = interval; // timer capture picture time interval
        stuSnapParams.CmdSerial = 0; // request serial
        IntByReference reserved = new IntByReference(0);
        if (!LoginUtil.netsdk.CLIENT_SnapPictureEx(LoginUtil.m_hLoginHandle, stuSnapParams, reserved)) {
            System.err.printf("CLIENT_SnapPictureEx Failed!" + ToolKits.getErrorCodePrint());
            return false;
        } else {
            System.out.println("CLIENT_SnapPictureEx success");
        }
        return true;
    }

    /**
     * 保存图片
     */

    private static class fCaptureReceiveCB implements NetSDKLib.fSnapRev {
        BufferedImage bufferedImage = null;
        public void invoke(NetSDKLib.LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser) {
            if (pBuf != null && RevLen > 0) {
                String strFileName = SavePath.getSavePath().getSaveCapturePath();
                System.out.println("strFileName = " + strFileName);
                byte[] buf = pBuf.getByteArray(0, RevLen);
                ByteArrayInputStream byteArrInput = new ByteArrayInputStream(buf);
                try {
                    bufferedImage = ImageIO.read(byteArrInput);
                    if (bufferedImage == null) {
                        return;
                    }
                    ImageIO.write(bufferedImage, "jpg", new File(strFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 抓图回调函数
     * @param cbSnapReceive
     */
    private static void setSnapRevCallBack(NetSDKLib.fSnapRev cbSnapReceive) {
        LoginUtil.netsdk.CLIENT_SetSnapRevCallBack(cbSnapReceive, null);
    }

}
