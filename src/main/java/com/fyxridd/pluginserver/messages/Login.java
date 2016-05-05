package com.fyxridd.pluginserver.messages;

import com.fyxridd.netty.common.Message;

public class Login implements Message{
    public static String NAME = "Login";

    @Required
    private String name;
    @Optional
    private String pwd;

    public Login() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
