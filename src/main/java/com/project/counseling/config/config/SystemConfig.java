package com.project.counseling.config.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 项目整体配置信息
 * @date 2023/9/21 16:42
 */
@Component
@ConfigurationProperties(prefix = "system")
public class SystemConfig {

    /**
     * 网络访问日志开关
     */
    private static boolean requestLog;

    public static boolean isRequestLog() {
        return requestLog;
    }

    public void setRequestLog(boolean requestLog) {
        SystemConfig.requestLog = requestLog;
    }
}