package com.xt.hibernate.bean;

import javax.persistence.Column;

/**
 * @author xt
 * @create 2019/4/3 17:02
 * @Desc
 */

public class Pay {

    private int monthPay;
    private int yearPay;
    private int vocationPay;
    private Worker worker;

    public int getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(int monthPay) {
        this.monthPay = monthPay;
    }

    public int getYearPay() {
        return yearPay;
    }

    public void setYearPay(int yearPay) {
        this.yearPay = yearPay;
    }

    public int getVocationPay() {
        return vocationPay;
    }

    public void setVocationPay(int vocationPay) {
        this.vocationPay = vocationPay;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Pay() {
    }

    @Override
    public String toString() {
        return "Pay{" +
                "monthPay=" + monthPay +
                ", yearPay=" + yearPay +
                ", vocationPay=" + vocationPay +
                '}';
    }

    public Pay(int monthPay, int yearPay, int vocationPay) {
        this.monthPay = monthPay;
        this.yearPay = yearPay;
        this.vocationPay = vocationPay;
    }
}
