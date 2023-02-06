package com.huiwan.base.util.trace;

public  class Record{
    public int id;
    public String ip;
    public String avg;

    public Record(int id, String ip, String avg) {
        this.id = id;
        this.ip = ip;
        this.avg = avg;
    }
}
