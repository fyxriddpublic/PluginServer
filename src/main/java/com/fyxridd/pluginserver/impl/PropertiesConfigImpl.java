package com.fyxridd.pluginserver.impl;

import com.fyxridd.pluginserver.Config;
import org.apache.ibatis.io.Resources;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 从资源文件夹下的'config.properties'文件中读取配置
 */
@Component
public class PropertiesConfigImpl implements Config{
    private int port;

    public PropertiesConfigImpl() {
        Properties p = new Properties();
        try {
            p.load(new InputStreamReader(Resources.getResourceAsStream("config.properties"), Charset.forName("utf-8")));

            port = Integer.parseInt(p.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return port;
    }
}
