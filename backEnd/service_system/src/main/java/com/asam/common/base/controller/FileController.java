
package com.asam.common.base.controller;

import com.entity.R;
import com.asam.config.MyConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/***
 * @Description:
 * @Author: Created by pjb on 2020/9/21 10:32
 * @Modifier: Modify by pjb on 2020/9/21 10:32
 */
@RestController
@RequestMapping(value = "/file")
@Slf4j
public class FileController {

    @Autowired
    private MyConfig myConfig;

    /***
     * @Description: 文件上传
     * @param file 文件
     * @Return: java.lang.Object
     * @Date: 2020/9/21 17:26
     */
    @RequestMapping(value = "/upload")
    public Object upload(MultipartFile file){
        try {
            if (file.isEmpty()){
                return R.error("文件为空");
            }
            String fileName = file.getOriginalFilename();
            String sufName = fileName.substring(fileName.lastIndexOf("."));
            log.info("上传的文件名为：" + fileName + " 后缀名为：" + sufName);
            String uploadPath = myConfig.getUploadPath();
            String path = uploadPath + fileName;
            File uploadFile = new File(path);
            //检测文件存储目录是否存在,如果不存在就创建
            if (!uploadFile.getParentFile().exists()){
                uploadFile.getParentFile().mkdirs();
            }
            file.transferTo(uploadFile);
            return R.ok("文件上传成功");
        } catch (IllegalStateException | IOException e){
            e.printStackTrace();
        }
        return R.error("文件上传失败");
    }

    /***
     * @Description: 下载 resources/template 下的文件
     * @param fileName 文件名
     * @param response response
     * @Return: java.lang.Object
     * @Date: 2020/9/21 17:26
     */
    @RequestMapping(value = "/download")
    public Object download(String folder, String fileName, HttpServletResponse response) throws Exception{
        if (StringUtils.isNotBlank(fileName)){
            String resPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            File file = new File(resPath + "template/"+ folder +"/" + fileName);
            if (file.exists()){
                response.setContentType("application/force-download");
                String showName = URLEncoder.encode(fileName, "UTF-8");
                response.addHeader("Content-Disposition", "attachment;fileName=" + showName);
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    response.reset();
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1){
                        os.write(buffer, 0, i);
                        os.flush();
                        i = bis.read(buffer);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if (null != bis){
                        bis.close();
                    }
                    if (null != fis){
                        fis.close();
                    }
                }
                return null;
            }else {
                return R.error("文件不存在");
            }
        }else {
            return R.error("文件名为空");
        }
    }

}
