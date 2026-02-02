package com.robotCore.common.utils;

import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Description: Excel工具类
 * @Author: zhangqi
 * @Create: 2021/4/14 15:21
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static final String XLS = ".xls";
    public static final String BIG_XLS = ".XLS";
    public static final String XLSX = ".xlsx";
    public static final String BIG_XLSX = ".XLSX";

    public static List readExcelFileToDTO(MultipartFile file) throws IOException {
        return readExcelFileToDTO(file, 0);
    }

    public static List readExcelFileToDTO(MultipartFile file, Integer sheetId) throws IOException {
        //将文件转成workbook类型
        Workbook workbook = buildWorkbook(file);
        //第一个表
        return readSheetToDTO(workbook.getSheetAt(sheetId));
    }

    public static List readSheetToDTO(Sheet sheet) throws IOException {
        List<Map<String, Object>> sheetValue = changeSheetToMapList(sheet);
        return sheetValue;
    }

    //类型转换
    private static Workbook buildWorkbook(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename.endsWith(XLS) || filename.endsWith(BIG_XLS)) {
            return new HSSFWorkbook(file.getInputStream());
        } else if (filename.endsWith(XLSX) || filename.endsWith(BIG_XLSX)) {
            return new XSSFWorkbook(file.getInputStream());
        } else {
            throw new IOException("unknown file format: " + filename);
        }
    }

    private static List<Map<String, Object>> changeSheetToMapList(Sheet sheet) {
        List<Map<String, Object>> result = new ArrayList<>();
       /* int rowNumber = sheet.getPhysicalNumberOfRows();
        // 第一行作为表头
        String[] titles = getSheetRowValues(sheet.getRow(0));
        for (int i = 1; i < rowNumber; i++) {
            String[] values = getSheetRowValues(sheet.getRow(i));
            Map<String, String> valueMap = new HashMap<>();
            for (int j = 0; j < titles.length; j++) {
                valueMap.put(titles[j], values[j]);
            }
            result.add(valueMap);
        }*/
        Row keyRow = null;
        Row row = null;
        Cell cell = null;
        Cell keyCell = null;
        for (int j = sheet.getFirstRowNum() + 1 ; j <= sheet.getLastRowNum(); j++) {
            keyRow = sheet.getRow(sheet.getFirstRowNum());
            row = sheet.getRow(j);
            if (row == null || row.getFirstCellNum() == j ) {
                continue;
            }
            Cell indexCell = row.getCell(0);
            if(ObjectUtils.isEmpty(indexCell)) {
                continue;
            }
            Map<String, Object> dataMap = new HashMap<>();
            for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                cell = row.getCell(y);
                keyCell = keyRow.getCell(y);
                if(!ObjectUtils.isEmpty(keyCell)) {
                    if(ObjectUtils.isEmpty(cell)) {
                        dataMap.put(keyCell.getStringCellValue(),cell);
                    } else {
                        String cellVaule  = getValueOnCell(cell);
                        dataMap.put(keyCell.getStringCellValue(),cellVaule);
                    }
                }
            }
            result.add(dataMap);
        }
        return result;
    }

    private static <T> T buildDTOByClass(Class<T> clazz, Map<String, String> valueMap) {
        try {
            T dto = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                ApiModelProperty desc = field.getAnnotation(ApiModelProperty.class);
                String value = valueMap.get(desc.value());
                if (value != null) {
                    if(field.getType().getName().equalsIgnoreCase("java.lang.Integer")) {
                        Method method = clazz.getMethod(getSetMethodName(field.getName()), field.getType());
                        Integer age = Integer.parseInt(value.substring(0,value.indexOf(".")));
                        method.invoke(dto, age);
                    }else{
                        Method method = clazz.getMethod(getSetMethodName(field.getName()), field.getType());
                        method.invoke(dto, value);
                    }
                }
            }

            return dto ;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    private static String getSetMethodName(String name) {
        String firstChar = name.substring(0, 1);
        return "set" + firstChar.toUpperCase() + name.substring(1);
    }

    private static String[] getSheetRowValues(Row row) {
        if (row == null) {
            return new String[]{};
        } else {
            int cellNumber = row.getLastCellNum();
            List<String> cellValueList = new ArrayList<>();
            for (int i = 0; i < cellNumber; i++) {
                cellValueList.add(getValueOnCell(row.getCell(i)));
            }
            return cellValueList.toArray(new String[0]);
        }
    }

    private static String getValueOnCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return cell.getBooleanCellValue() ? "true" : "false";
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default: return "";
        }
    }

    /**
     * 导出Excel
     *
     * @param workbook 要导出的excel保存路径
     * @param sheetNum sheet的位置，0表示第一个表格中的第一个sheet
     * @param sheetTitle sheet的名称
     * @param list      要导出的数据集合
     * @param fieldMap  中英文字段对应Map，即要导出的excel表头
     * @param <T>
     */
    public static <T> void export(HSSFWorkbook workbook, int sheetNum, String sheetTitle, List<T> list, LinkedHashMap<String, String> fieldMap) {
        try {
            logger.info("sheet"+ sheetNum+"数据填充");
            //创建一个WorkBook,对应一个Excel文件
//            HSSFWorkbook wb = new HSSFWorkbook();
            //在Workbook中，创建一个sheet，对应Excel中的工作薄（sheet）
//            HSSFSheet sheet = wb.createSheet("sheet1");
            HSSFSheet sheet = workbook.createSheet();
            workbook.setSheetName(sheetNum, sheetTitle);
            HSSFCellStyle style = workbook.createCellStyle();
//            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //创建单元格，并设置值表头 设置表头居中
//            HSSFCellStyle style = wb.createCellStyle();
//            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // 填充工作表
            fillSheet(sheet, list, fieldMap, style);
        } catch (Exception e) {
            logger.error("Excel数据填充失败：" + e.getMessage());
        }
    }

    /**
     * 根据字段名获取字段对象
     * @param fieldName 字段名
     * @param clazz     包含该字段的类
     * @return 字段
     */
    public static Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();
        // 如果本类中存在该字段，则返回
        for (Field field : selfFields) {
            //如果本类中存在该字段，则返回
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        // 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            //递归
            return getFieldByName(fieldName, superClazz);
        }
        // 如果本类和父类都没有，则返回空
        return null;
    }

    /**
     * 根据字段名获取字段值
     * @param fieldName 字段名
     * @param o         对象
     * @return 字段值
     * @throws Exception 异常
     */
    public static Object getFieldValueByName(String fieldName, Object o) throws Exception {
        Object value = null;
        //根据字段名得到字段对象
        Field field = getFieldByName(fieldName, o.getClass());
        //如果该字段存在，则取出该字段的值
        if (field != null) {
            field.setAccessible(true);//类中的成员变量为private,在类外边使用属性值，故必须进行此操作
            value = field.get(o);//获取当前对象中当前Field的value
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }
        return value;
    }

    /**
     * 根据带路径或不带路径的属性名获取属性值,即接受简单属性名，
     * 如userName等，又接受带路径的属性名，如student.department.name等
     *
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param o                 对象
     * @return 属性值
     * @throws Exception 异常
     */
    public static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {
        Object value = null;
        // 将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            // 根据数组中第一个连接属性名获取连接属性对象，如student.department.name
            Object fieldObj = getFieldValueByName(attributes[0], o);
            //截取除第一个属性名之后的路径
            String subFieldNameSequence = fieldNameSequence
                    .substring(fieldNameSequence.indexOf(".") + 1);
            //递归得到最终的属性对象的值
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;

    }

    /**
     * 向工作表中填充数据
     *
     * @param sheet    excel的工作表名称
     * @param list     数据源
     * @param fieldMap 中英文字段对应关系的Map
     * @param style    表格中的格式
     * @throws Exception 异常
     */
    public static <T> void fillSheet(HSSFSheet sheet, List<T> list, LinkedHashMap<String, String> fieldMap, HSSFCellStyle style) throws Exception {
        // 定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[fieldMap.size()];
        String[] cnFields = new String[fieldMap.size()];
        // 填充数组
        int count = 0;
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
        //在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 填充表头
        for (int i = 0; i < cnFields.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(cnFields[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i) * 7/4;
            sheet.setColumnWidth(i,width);
        }
        // 填充内容
        for (int index = 0; index < list.size(); index++) {
            row = sheet.createRow(index + 1);
            // 获取单个对象
            T item = list.get(index);
            for (int i = 0; i < enFields.length; i++) {
                Object objValue = getFieldValueByNameSequence(enFields[i], item);
                String fieldValue = objValue == null ? "" : objValue.toString();
                row.createCell(i).setCellValue(fieldValue);
            }
        }
    }
}
