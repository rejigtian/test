package com.example.test.generic;

import java.util.Calendar;
import java.util.Date;

/**
 * Author：Jay On 2019/5/11 22:41
 * <p>
 * Description: 获取泛型类型测试类
 */
public class GenericType<T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static boolean isAdult(String certnum){
        Date date =new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(certnum.substring(6,10))+18,Integer.parseInt(certnum.substring(10,12)),Integer.parseInt(certnum.substring(12,14)));
        Date birthdate = calendar.getTime();
        return birthdate.before(date);
    }
    public static int getAge(String certnum){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) - Integer.parseInt(certnum.substring(6,10));
    }

    public static void main(String[] args) {
       System.out.println(getAge("420881199606165119"));
    }
}
