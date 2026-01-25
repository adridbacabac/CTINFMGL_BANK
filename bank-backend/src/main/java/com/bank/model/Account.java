package com.bank.model;

public class Account {
    private int id;
    private String accountId;
    private String customerId;
    private String accountType;
    private double balance;
    private String status;

    public Account(int id, String accountId, String customerId,
                   String accountType, double balance, String status) {
        this.id = id;
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public String getAccountId() { return accountId; }
    public double getBalance() { return balance; }
}
