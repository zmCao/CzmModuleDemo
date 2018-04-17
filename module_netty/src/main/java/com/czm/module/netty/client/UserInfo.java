package com.czm.module.netty.client;

import org.msgpack.annotation.Message;

import java.util.Date;
@Message
public class UserInfo {
    public int getAge() {
        return age;
    }

    public void setAge(int bage) {
        this.age = bage;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date irthday) {
        this.birthday = irthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int age;
    public Date birthday;
    public String name;
}
