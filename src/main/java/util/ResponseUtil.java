package util;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by steam on 2017/7/11.
 */
public class ResponseUtil {
    /**
     * 将数据写回页面 用于jquery-ajax的异步交互
     * @param response
     * @param o
     * @throws Exception
     */
    public static void write(HttpServletResponse response,Object o)throws Exception{
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.print(o.toString());
        out.flush();
        out.close();
    }

    /**
     * 将excel文件写回客户端浏览器 用于下载
     * @param response
     * @param wb
     * @param fileName
     * @throws Exception
     */
    public static void export(HttpServletResponse response, Workbook wb, String fileName)throws Exception{
        response.setHeader("Content-Disposition", "attachment;filename="+new String(fileName.getBytes("utf-8"),"iso8859-1"));
        response.setContentType("application/ynd.ms-excel;charset=UTF-8");
        OutputStream out=response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }

}
