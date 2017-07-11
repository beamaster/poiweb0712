package util;

/**
 * Created by steam on 2017/7/11.
 */
public class StringUtil {
    public static boolean isEmpty(String str){
        if("".equals(str)||str==null){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isNotEmpty(String str){
        if(!"".equals(str)&&str!=null){
            return true;
        }else{
            return false;
        }
    }
}
