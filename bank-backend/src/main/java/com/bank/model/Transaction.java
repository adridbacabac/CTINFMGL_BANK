package com.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private LocalDateTime date;

    public Transaction(String transactionId, String fromAccount,
                       String toAccount, double amount, LocalDateTime date) {
        this.transactionId = transactionId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
    }
}
