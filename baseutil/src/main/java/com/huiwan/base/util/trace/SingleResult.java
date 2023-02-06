package com.huiwan.base.util.trace;

import java.util.ArrayList;
import java.util.List;

public class SingleResult {
    public String host;
    public boolean isSuccess;
    public List<Record> records = new ArrayList<>();
    public String info;

    public SingleResult(String host, boolean isSuccess, List<Record> records) {
        this.host = host;
        this.isSuccess = isSuccess;
        this.records = records;
    }
    //如果不需要存储每步record，则用该构造函数，仅存储1个string
    public SingleResult(String msg){
        info = msg;
    }
}
