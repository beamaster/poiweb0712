package util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by steam on 2017/7/11.
 */
public class ExcelUtil {
    /**
     * 填充数据到普通的excel文件中
     * @param rs
     * @param wb
     * @param headers
     * @throws Exception
     */
    public static void fillExcelData(ResultSet rs, Workbook wb, String[] headers)throws Exception{
        Sheet sheet=wb.createSheet();
        Row row=sheet.createRow(0);

        //先填充行头 : "编号","姓名","电话","Email","QQ","出生日期"
        for(int i=0;i<headers.length;i++){
            row.createCell(i).setCellValue(headers[i]);
        }

        //再填充数据
        int rowIndex=1;
        while(rs.next()){
            row=sheet.createRow(rowIndex++);
            for(int i=0;i<headers.length;i++){
                Object objVal=rs.getObject(i+1);
                if (objVal instanceof Date) {
                    row.createCell(i).setCellValue(DateUtil.formatDate((Date)objVal,"yyyy-MM-dd"));
                }else{
                    row.createCell(i).setCellValue(objVal.toString());
                }
            }
        }
    }

    /**
     * 填充数据到模板excel文件
     * @param rs
     * @param templateFileName
     * @return
     * @throws Exception
     */
    public static Workbook fillExcelDataWithTemplate(ResultSet rs,String templateFileName)throws Exception{
        //首先:从本地磁盘读取模板excel文件,然后读取第一个sheet
        InputStream inp=ExcelUtil.class.getResourceAsStream("/template/"+templateFileName);
        POIFSFileSystem fs=new POIFSFileSystem(inp);
        Workbook wb=new HSSFWorkbook(fs);
        Sheet sheet=wb.getSheetAt(0);

        //开始写入数据到模板中: 需要注意的是,因为行头以及设置好,故而需要跳过行头
        int cellNums=sheet.getRow(0).getLastCellNum();
        int rowIndex=1;
        while(rs.next()){
            Row row=sheet.createRow(rowIndex++);
            for(int i=0;i<cellNums;i++){
                Object objVal=rs.getObject(i+1);
                if (objVal instanceof Date) {
                    row.createCell(i).setCellValue(DateUtil.formatDate((Date)objVal,"yyyy-MM-dd"));
                }else{
                    row.createCell(i).setCellValue(objVal.toString());
                }
            }
        }
        return wb;
    }

    /**
     * 处理单元格格式的简单方式
     * @param hssfCell
     * @return
     */
    public static String formatCell(HSSFCell hssfCell){
        if(hssfCell==null){
            return "";
        }else{
            if(hssfCell.getCellType()==HSSFCell.CELL_TYPE_BOOLEAN){
                return String.valueOf(hssfCell.getBooleanCellValue());
            }else if(hssfCell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
                return String.valueOf(hssfCell.getNumericCellValue());
            }else{
                return String.valueOf(hssfCell.getStringCellValue());
            }
        }
    }

    /**
     * 处理单元格格式的第二种方式: 包括如何对单元格内容是日期的处理
     * @param cell
     * @return
     */
    public static String formatCell2(HSSFCell cell) {
        if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {

            //针对单元格式为日期格式
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
            }
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    /**
     * 处理单元格格式的第三种方法:比较全面
     * @param cell
     * @return
     */
    public static String formatCell3(HSSFCell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:

                //日期格式的处理
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                }

                return String.valueOf(cell.getNumericCellValue());

            //字符串
            case HSSFCell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            // 公式
            case HSSFCell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();

            // 空白
            case HSSFCell.CELL_TYPE_BLANK:
                return "";

            // 布尔取值
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() + "";

            //错误类型
            case HSSFCell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue() + "";
        }

        return "";
    }
}
