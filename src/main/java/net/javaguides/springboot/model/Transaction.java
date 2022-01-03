package net.javaguides.springboot.model;

import java.util.Date;


public class Transaction {

    String lastTransaction;
    Date date;
    String lastIP;

    public Transaction(String lastIP, String lastTransaction, Date date) {
        this.lastIP = lastIP;
        this.lastTransaction = lastTransaction;
        this.date = date;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(String lastTransaction) {
        this.lastTransaction = lastTransaction;
    }




}
