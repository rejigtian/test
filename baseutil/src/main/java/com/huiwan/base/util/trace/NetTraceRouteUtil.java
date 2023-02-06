package com.huiwan.base.util.trace;

import android.util.Log;

import com.huiwan.platform.ThreadUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetTraceRouteUtil {

    private static final String TAG = "NetTraceRoute";
    private static final String MATCH_TRACE_IP = "(?<=From )(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
    private static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
    private static final String MATCH_PING_TIME = "(?<=time=).*?ms";
    private static SingleResult result;
    private static boolean isSuccess = false;
    private static boolean isError = false;

    /**
     * 监控NetPing的日志输出到Service
     */
    public interface NetTraceRouteListener {
        void onNetTraceUpdated(String log);
        void onNetTraceFinished(SingleResult result);
        void onError(String log);
    }

    public static void startTraceRouteAsync(final String host, final NetTraceRouteListener listener) {
        ThreadUtil.runInOtherThread(new Runnable() {
            @Override
            public void run() {
                startTraceRouteSync(host, listener);
            }
        });
    }

    /**
     * 执行指定host的traceroute
     */
    public static void startTraceRouteSync(String host, NetTraceRouteListener listener) {
        Log.i(TAG, "startTraceRouteSync: " + host);
        TraceTask trace = new TraceTask(host, 1);
        execTrace(trace, listener);
    }

    /**
     * 执行ping命令，返回ping命令的全部控制台输出
     */
    private static String execPing(PingTask ping) {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("ping -c 1 -s 1024 " + ping.getHost());
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            reader.close();
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * 通过ping命令模拟执行traceroute的过程
     */
    private static void execTrace(TraceTask trace, final NetTraceRouteListener mNetTraceRouteListener) {
        Pattern patternTrace = Pattern.compile(MATCH_TRACE_IP);
        Pattern patternIp = Pattern.compile(MATCH_PING_IP);
        Pattern patternTime = Pattern.compile(MATCH_PING_TIME);
        List<Record> records = new ArrayList<>();
        Process process = null;
        BufferedReader reader = null;
        boolean finish = false;
        final StringBuilder totalLog = new StringBuilder();
        isError = false;
        try {
            // 通过ping的跳数控制，取得相应跳输的ip地址，然后再次执行ping命令读取时间
            while (!finish && trace.getHop() < 40) {
                int id = 0;
                String ip = "null";
                String Avg = "timeout";
                // 先发出ping命令获得某个跳数的ip地址
                String str = "";
                // -c 1 同时发送消息次数 －t是指跳数
                String command = "ping -c 1 -t " + trace.getHop() + " "
                        + trace.getHost();

                process = Runtime.getRuntime().exec(command);
                reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str += line;
                }
                reader.close();
                process.waitFor();

                Matcher m = patternTrace.matcher(str);

                // 如果成功获得trace:IP，则再次发送ping命令获取ping的时间
                final StringBuilder log = new StringBuilder(1024);
                if (m.find()) {
                    String pingIp = m.group();
                    PingTask pingTask = new PingTask(pingIp);

                    String status = execPing(pingTask);
                    if (status.length() == 0) {
                        log.append("unknown host or network error\n");
                        isError = true;
                        finish = true;
                    } else {
                        Matcher matcherTime = patternTime.matcher(status);
                        if (matcherTime.find()) {
                            String time = matcherTime.group();
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            log.append("\t\t");
                            log.append(time);
                            log.append("\t");
                            id = trace.getHop();
                            ip = pingIp;
                            Avg = time;
                        } else {
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            log.append("\t\t timeout \t");
                            id = trace.getHop();
                            ip = pingIp;
                        }
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mNetTraceRouteListener != null)
                                    mNetTraceRouteListener.onNetTraceUpdated(log.toString());
                            }
                        });
                        trace.setHop(trace.getHop() + 1);
                    }
                } else {
                    Matcher matchPingIp = patternIp.matcher(str);
                    if (matchPingIp.find()) {
                        String pingIp = matchPingIp.group();
                        Matcher matcherTime = patternTime.matcher(str);
                        if (matcherTime.find()) {
                            String time = matcherTime.group();
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            log.append("\t\t");
                            log.append(time);
                            log.append("\t");
                            id = trace.getHop();
                            ip = pingIp;
                            Avg = time;
                            isSuccess = true;
                            ThreadUtil.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mNetTraceRouteListener != null)
                                        mNetTraceRouteListener.onNetTraceUpdated(log.toString());
                                }
                            });
                        }
                        finish = true;
                    } else {
                        if (str.length() == 0) {
                            log.append("unknown host or network error\t");
                            isError = true;
                            finish = true;
                        } else {
                            log.append(trace.getHop());
                            log.append("\t\t timeout \t");
                            id = trace.getHop();
                            trace.setHop(trace.getHop() + 1);
                        }
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mNetTraceRouteListener != null)
                                    mNetTraceRouteListener.onNetTraceUpdated(log.toString());
                            }
                        });
                    }
                }// else no match traceIPPattern
                totalLog.append("\n").append(log);
                if (!isError) {
                    records.add(new Record(id, ip, Avg));
                }
            }// while
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mNetTraceRouteListener != null) {
                    if (records.size() <= 0) {
                        //如果出错，则初始化为错误信息
                        result = new SingleResult(totalLog.toString());
                    } else {
                        result = new SingleResult(trace.getHost(), isSuccess, records);
                        isSuccess = false;
                    }
                    mNetTraceRouteListener.onNetTraceFinished(result);
                }
            }
        });
    }

    /**
     * Ping任务
     */
    private static class PingTask {

        private String host;
        private static final String MATCH_PING_HOST_IP = "(?<=\\().*?(?=\\))";

        String getHost() {
            return host;
        }

        PingTask(String host) {
            super();
            this.host = host;
            Pattern p = Pattern.compile(MATCH_PING_HOST_IP);
            Matcher m = p.matcher(host);
            if (m.find()) {
                this.host = m.group();
            }
        }
    }

    /**
     * 生成trace任务
     */
    private static class TraceTask {
        private final String host;
        private int hop;

        TraceTask(String host, int hop) {
            super();
            this.host = host;
            this.hop = hop;
        }

        String getHost() {
            return host;
        }

        int getHop() {
            return hop;
        }

        void setHop(int hop) {
            this.hop = hop;
        }
    }
}
