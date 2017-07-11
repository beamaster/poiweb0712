package util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by steam on 2017/7/11.
 */
public class DbUtil {
    private String dbUrl="jdbc:mysql://localhost:3307/imooc?useUnicode=true&characterEncoding=utf8";
    private String dbUserName="root";
    private String dbPassword="root";
    private String jdbcName="com.mysql.jdbc.Driver";


    public Connection getCon()throws Exception{
        Class.forName(jdbcName);
        Connection con= DriverManager.getConnection(dbUrl,dbUserName,dbPassword);
        return con;
    }

    public void closeCon(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }
}
