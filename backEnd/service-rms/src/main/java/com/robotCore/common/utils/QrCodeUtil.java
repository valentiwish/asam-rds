package com.robotCore.common.utils;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 二维码工具类
 * @Author: zhangqi
 * @Create: 2021/4/24 14:45
 */
public class QrCodeUtil {

    /**
     * 根据唯一编码生成二维码的图片流
     */
    public static InputStream getQrCodeImage(String content) {
        if(Objects.nonNull(content)) {
            //默认黑色
            int orColor = 0xFF000000;
            //白色背景
            int bgWhite = 0xFFFFFFFF;
            //二维码宽
            int width = 150;
            //二维码高
            int height = 150;
            Map<EncodeHintType, Object> hints = new HashMap<>();
            // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置编码方式
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 设置二维码四周白色区域的大小
            hints.put(EncodeHintType.MARGIN, 1);
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
                BitMatrix bm = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        image.setRGB(x, y, bm.get(x, y) ? orColor : bgWhite);
                    }
                }
                image.flush();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);
                InputStream input = new ByteArrayInputStream(os.toByteArray());
                return input;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
