package util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;
/**
 * Created by steam on 2017/7/11.
 */


public class JsonUtil {

    /**
     * 把ResultSet集合转换成JsonArray数组
     * @param rs
     * @return
     * @throws Exception
     */
    public static JSONArray formatRsToJsonArray(ResultSet rs)throws Exception{
        ResultSetMetaData md=rs.getMetaData();
        int num=md.getColumnCount();
        JSONArray array=new JSONArray();
        while(rs.next()){
            JSONObject mapOfColValues=new JSONObject();
            for(int i=1;i<=num;i++){

                Object strVal=rs.getObject(i);
                if (strVal instanceof Date) {
                    mapOfColValues.put(md.getColumnName(i), DateUtil.formatDate((Date)strVal,"yyyy-MM-dd"));
                }else{
                    mapOfColValues.put(md.getColumnName(i),strVal);
                }
            }
            array.add(mapOfColValues);
        }
        return array;
    }
}