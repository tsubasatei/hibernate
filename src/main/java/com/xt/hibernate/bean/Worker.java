package com.xt.hibernate.bean;

/**
 * @author xt
 * @create 2019/4/3 17:01
 * @Desc
 */

public class Worker {
    private Integer id;
    private String username;
    private Pay pay;

    public Worker() {
    }

    public Worker(String username, Pay pay) {
        this.username = username;
        this.pay = pay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", pay=" + pay +
                '}';
    }
}
