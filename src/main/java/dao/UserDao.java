package dao;

import entity.PageBean;
import entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



/**
 * Created by steam on 2017/7/11.
 */
public class UserDao {
    public ResultSet userList(Connection con, PageBean pageBean)throws Exception{
        StringBuffer sb=new StringBuffer("select * from t_user");
        if(pageBean!=null){
            sb.append(" limit ?,?");
        }
        PreparedStatement pstmt=con.prepareStatement(sb.toString());
        if(pageBean!=null){
            pstmt.setInt(1, pageBean.getStart());
            pstmt.setInt(2, pageBean.getRows());
        }
        return pstmt.executeQuery();
    }

    public int userCount(Connection con)throws Exception{
        String sql="select count(*) as total from t_user";
        PreparedStatement pstmt=con.prepareStatement(sql);
        ResultSet rs=pstmt.executeQuery();
        if(rs.next()){
            return rs.getInt("total");
        }else{
            return 0;
        }
    }



    public int userAdd(Connection con, User user)throws Exception{
        String sql="insert into t_user values(null,?,?,?,?,?)";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getPhone());
        pstmt.setString(3, user.getEmail());
        pstmt.setString(4, user.getQq());

        //java.util.date转为 java.sql.date
        pstmt.setDate(5, new java.sql.Date(user.getBirth() .getTime()));

        return pstmt.executeUpdate();
    }
}
