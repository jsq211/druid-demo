//package com.jsq.demo.common.utils;
//
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.ExcelReader;
//import com.alibaba.excel.ExcelWriter;
//import com.alibaba.excel.annotation.ExcelIgnore;
//import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
//import com.alibaba.excel.annotation.ExcelProperty;
//import com.alibaba.excel.annotation.write.style.ColumnWidth;
//import com.alibaba.excel.annotation.write.style.ContentRowHeight;
//import com.alibaba.excel.annotation.write.style.HeadRowHeight;
//import com.alibaba.excel.read.metadata.ReadSheet;
//import com.alibaba.excel.write.metadata.WriteSheet;
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Lists;
//import com.jsq.demo.test.DemoData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;
//
//import java.io.File;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.*;
//
//
//
///**
// * Excel工具类 导出导入
// * 基于 alibaba.easyexcel进行开发
// * 似乎是不支持多线程写入导出
// * @author jsq
// */
//public class ExcelUtils {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);
//    /**
//     * 导出到excel 单列sheet
//     * @param fileName 导出文件名
//     * @param list 导出数据 注意：每次导出的文件类型必须相同 且必须使用对应注解
//     * 对应导出文件需要注解为 字段名称 {@link ExcelProperty} 需要导出字段 注解中需标注表头信息
//     *              {@link ExcelIgnore} 忽略该注解对应字段
//     *              忽略所有未添加 ExcelProperty 注解的字段 {@link ExcelIgnoreUnannotated}
//     */
//    public static void writeSingleSheet(String fileName, List list) {
//        fileName = fileName + LocalDate.now() + ".xlsx";
//        // 通过读取list中的属性去填写信息头
//        if (CollectionUtil.isNotEmpty(list)){
//            ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build();
//            // 这里注意 如果同一个sheet只要创建一次
//            WriteSheet writeSheet = EasyExcel.writerSheet(fileName).build();
//            // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
//            excelWriter.write(list, writeSheet);
//            /// 千万别忘记finish 会帮忙关闭流
//            excelWriter.finish();
//        }
//    }
//
//    /**
//     * 导出到excel 多列sheet
//     * @param fileName 导出文件名
//     * @param list 导出数据 注意：每次导出的文件类型必须相同 且必须使用对应注解
//     * 对应导出文件需要注解为 字段名称 {@link ExcelProperty} 需要导出字段 注解中需标注表头信息
//     *              {@link ExcelIgnore} 忽略该注解对应字段
//     *              忽略所有未添加 ExcelProperty 注解的字段 {@link ExcelIgnoreUnannotated}
//     *              {@link ColumnWidth}、{@link HeadRowHeight}、{@link ContentRowHeight}指定宽度或高度
//     */
//    public synchronized static void writeMultiSheet(String fileName, List list,int pageSize) {
//        String writeFileName = fileName + LocalDate.now() + ".xlsx";
//        // 通过读取list中的属性去填写信息头
//        Long time = System.currentTimeMillis();
//        System.out.println("--------------------------start in :"+ time);
//        if (CollectionUtil.isNotEmpty(list)){
//            List<List<DemoData>> data = CollectionUtil.getSubList(list,pageSize);
//            ExcelWriter excelWriter = EasyExcel.write(writeFileName, DemoData.class).build();
//            for (int i = 0; i < data.size(); i++) {
//                List<DemoData> subData = data.get(i);
//                writerSheets(i,fileName,subData,excelWriter);
//            }
//            /// 千万别忘记finish 会帮忙关闭流
//            excelWriter.finish();
//            Long end = System.currentTimeMillis();
//            System.out.println("--------------------------spend time: "+(end - time));
//        }
//    }
//
//    /**
//     * 线程写入多个表格
//     * @param sheetNo sheet列表
//     * @param sheetName 表格名称
//     * @param data 数据
//     * @param excelWriter excel对象
//     */
//    private static void  writerSheets(int sheetNo, String sheetName, List data, ExcelWriter excelWriter){
//        try {
//            if (CollectionUtil.isNotEmpty(data)){
//                String name =  sheetName + "-" + sheetNo;
//                // 分页写入
//                WriteSheet writeSheet =  EasyExcel.writerSheet(sheetNo, name).build();
//                excelWriter.write(data, writeSheet);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void simpleRead(String fileName) {
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
//        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
//        List<DemoData> demoDataList = Lists.newArrayList();
//        for (Map<Integer, String> data : listMap) {
//            // 返回每条数据的键值对 表示所在的列 和所在列的值
//            DemoData demoData = new DemoData();
//            String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2NvdW50SWQiOiI4MTk2MzIiLCJ0ZW5hbnRHcm91cElkIjoiMiIsInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsicmVhZCJdLCJsb2dpbk5hbWUiOiJhZG1pbiIsImxvZ2luQ29kZSI6IkpYVklWTyIsInRlbmFudENvZGUiOiJKWFZJVk8iLCJleHAiOjE1NzkxMTEyMjMsImp0aSI6IjNhODk2YTNjLTA2NWMtNDY2Ny05Y2E2LTFkMmY4MGJiMWNiMSIsImNsaWVudF9pZCI6InVpIn0.Vy2X-KVz8h7O6_j1eDWQUT8SX7dFQ5LOBepP4Z8YObcYEMxIOpV21H9T-V0REtGbqGyODVE5BgPgaTlBwwpArtR3X-FyaEV-0N6ZB1eQRFV1_4IjcqYXNov2jf6yVd9GMTFTV_4PC84GCK2O8rcaZUskAF3pWPL8myxCZEnu8wCbzSaJU59K9sHWwt9iZyHPzuNEB-t09ESb33GBsaUaPiV5Ry5THnUWmHbAoklSXtJbxT9SiaYDI3tXruaC8OgJ3336mNAzriBBusHFtffAzTuw8LL7ujyLwr1xa38V2SD0PpB6VMbGbbO9ISGaXhWgy2R9mwo19RZmU1qOuejw0g";
//            String api = data.get(3);
//            demoData.setApi(api);
//            String json = data.get(4);
//            demoData.setJson(json);
//            String result = HttpClient.httpPostJsonByAccessToken(api,token,json);
//            demoData.setResult(result);
//            demoDataList.add(demoData);
//            LOGGER.info("读取到数据:{}", JSON.toJSONString(data));
//        }
//        String name =  "E:\\test\\test\\采购入库单.xlsx";
//        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
//        EasyExcel.write(name, DemoData.class).sheet("模板").doWrite(demoDataList);
//        LOGGER.info("结束。。。。。。");
//    }
//
//    public static void main(String[] args) {
////        ExcelUtils.simpleRead("E:\\test\\test\\CGRKD.xlsx");
//    }
//}
