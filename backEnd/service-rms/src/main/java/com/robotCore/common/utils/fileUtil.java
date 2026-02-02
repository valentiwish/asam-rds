package com.robotCore.common.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 与txt文件写入与读取
 * @Author: 范营营
 * @Created: 2021-01-07
 */
public class fileUtil {

   /*
   *读取txt文件
    */
    public static Map<String,String> readFile(String fileName) {
        Map<String, String> dataMap = new HashMap<>();
        try {
            ClassPathResource resource = new ClassPathResource("templates/"+fileName);
            BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                int tt = s.indexOf(":");
                String index = s.substring(0, tt);
                String name = s.substring(tt + 1);
                dataMap.put(index, name);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    /*
     *写入txt文件
     */
    public static void writeFile(Map<String, String> dataMap, String fileName){
        try {
            ClassPathResource resource = new ClassPathResource("templates/"+fileName);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile()),"utf-8"));
            bw.write("");//清空文件
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                bw.write(entry.getKey()+":"+entry.getValue()+"\r\n");
            }
            bw.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *清空txt文件
     */
    public  static void clearFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/"+fileName);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile()),"utf-8"));
            bw.write("");//清空文件
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
