package com.huiwan.base.util.trace;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * 用于将traceroute结果解析成json格式
 * Created by Salieri on 2020-08-06.
 */
public class LogJsonTypeAdapter extends TypeAdapter<LogJson> {

    @Override
    public void write(JsonWriter out, LogJson value) throws IOException {
        out.beginObject();
        out.name("CheckId").value(value.checkId);
        out.name("NetIsConnect").value(value.netIsConnect);
        out.name("NetIsWifi").value(value.netIsWifi);
        out.name("NetIp").value(value.netIp);
        out.name("LocalConfig").value(value.localConfig);
        out.name("UpdateConfig").value(value.updateConfig);
        out.name("StartTime").value(value.startTime);
        out.name("PacketSize").value(value.packetSize);
        out.name("MaxTTL").value(value.maxTTL);
        out.name("Method").value((value.method));
        out.name("Records").beginArray();
        for(Object result1 : value.records){
            out.beginObject();
            if(result1 instanceof SingleResult){
                SingleResult result = (SingleResult) result1;
                if(result.info ==null){
                    out.name("Host").value(result.host);
                    out.name("IsSuccess").value((result.isSuccess));
                    out.name("Hops").beginArray();
                    for(Record record : result.records){
                        out.beginObject();
                        out.name("id").value(record.id);
                        out.name("ip").value(record.ip);
                        out.name("Avg").value(record.avg);
                        out.endObject();
                    }
                    out.endArray();
                }
                else{
                    if (result.isSuccess){
                        out.name("Msg").value(result.info);
                    } else {
                        out.name("ErrorMsg").value(result.info);
                    }
                }

            }
            else{
                out.name("Error").value(result1.toString());
            }

            out.endObject();
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public LogJson read(JsonReader in) throws IOException {
        return null;
    }
}
