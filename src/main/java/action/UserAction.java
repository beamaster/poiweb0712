package action;

import com.opensymphony.xwork2.ActionSupport;
import dao.UserDao;
import entity.PageBean;
import entity.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import util.*;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by steam on 2017/7/11.
 */
public class UserAction extends ActionSupport{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String page;
    private String rows;
    private String id;
    private User user;
    private String delId;

    private File userUploadFile;
    private String url;

    public String getPage() {
        return page;
    }
    public void setPage(String page) {
        this.page = page;
    }
    public String getRows() {
        return rows;
    }
    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getDelId() {
        return delId;
    }
    public void setDelId(String delId) {
        this.delId = delId;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public File getUserUploadFile() {
        return userUploadFile;
    }
    public void setUserUploadFile(File userUploadFile) {
        this.userUploadFile = userUploadFile;
    }

    DbUtil dbUtil=new DbUtil();
    UserDao userDao=new UserDao();

    //获取用户列表
    public String list()throws Exception{
        Connection con=null;
        PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
        try{
            con=dbUtil.getCon();
            JSONObject result=new JSONObject();
            JSONArray jsonArray= JsonUtil.formatRsToJsonArray(userDao.userList(con, pageBean));
            int total=userDao.userCount(con);
            result.put("rows", jsonArray);
            result.put("total", total);
            ResponseUtil.write(ServletActionContext.getResponse(),result);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //导出用户  : 普通excel导出
    public String export()throws Exception{
        Connection con=null;
        try {
            con=dbUtil.getCon();
            Workbook wb=new HSSFWorkbook();
            String headers[]={"编号","姓名","电话","Email","QQ","出生日期"};
            ResultSet rs=userDao.userList(con, null);
            ExcelUtil.fillExcelData(rs, wb, headers);
            ResponseUtil.export(ServletActionContext.getResponse(), wb, "用户excel表.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //用户导出 : 采用预先设置好的excel模板文件进行导出
    public String export2()throws Exception{
        Connection con=null;
        try {
            con=dbUtil.getCon();
            ResultSet rs=userDao.userList(con, null);
            Workbook wb=ExcelUtil.fillExcelDataWithTemplate(rs, "用户模板文件.xls");
            ResponseUtil.export(ServletActionContext.getResponse(), wb, "利用模版导出用户excel表.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                dbUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //excel文件导入,批量导入数据
    public String upload()throws Exception{
        //此时的Workbook应该是从 客户端浏览器上传过来的 uploadFile了,其实跟读取本地磁盘的一个样
        POIFSFileSystem fs=new POIFSFileSystem(new FileInputStream(userUploadFile));
        HSSFWorkbook wb=new HSSFWorkbook(fs);
        HSSFSheet hssfSheet=wb.getSheetAt(0);

        if(hssfSheet!=null){
            //遍历excel,从第二行开始 即 rowNum=1,逐个获取单元格的内容,然后进行格式处理,最后插入数据库
            for(int rowNum=1;rowNum<=hssfSheet.getLastRowNum();rowNum++){
                HSSFRow hssfRow=hssfSheet.getRow(rowNum);
                if(hssfRow==null){
                    continue;
                }

                User user=new User();
                user.setName(ExcelUtil.formatCell(hssfRow.getCell(0)));
                user.setPhone(ExcelUtil.formatCell(hssfRow.getCell(1)));
                user.setEmail(ExcelUtil.formatCell(hssfRow.getCell(2)));
                user.setQq(ExcelUtil.formatCell(hssfRow.getCell(3)));

                //对于单元格日期需要进行特殊处理
                user.setBirth(DateUtil.formatString(ExcelUtil.formatCell2(hssfRow.getCell(4)), "yyyy-MM-dd"));
                Connection con=null;
                try{
                    con=dbUtil.getCon();
                    userDao.userAdd(con, user);
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    dbUtil.closeCon(con);
                }
            }
        }
        JSONObject result=new JSONObject();
        result.put("success", "true");
        ResponseUtil.write(ServletActionContext.getResponse(), result);
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
