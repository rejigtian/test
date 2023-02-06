package com.huiwan.base.util.trace;

import java.util.List;

public class LogJson{
    public String checkId;
    public boolean netIsConnect;
    public boolean netIsWifi;
    public String netIp;
    public String localConfig;
    public String updateConfig;
    public String startTime;
    public int packetSize;
    public int maxTTL;
    public String method;
    public List<Object> records;

    public LogJson(String checkId, boolean netIsConnect, boolean netIsWifi, String netIp,
                   String localConfig, String updateConfig, String startTime, int packetSize,
                   int maxTTL, String method, List<Object> records) {
        this.checkId = checkId;
        this.netIsConnect = netIsConnect;
        this.netIsWifi = netIsWifi;
        this.netIp = netIp;
        this.localConfig = localConfig;
        this.updateConfig = updateConfig;
        this.startTime = startTime;
        this.packetSize = packetSize;
        this.maxTTL = maxTTL;
        this.method = method;
        this.records = records;
    }
}
