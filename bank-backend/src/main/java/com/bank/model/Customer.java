package com.bank.model;

public class Customer {
    private int id;
    private String customerId;
    private String name;
    private String username;
    private String pin;

    public Customer(int id, String customerId, String name, String username, String pin) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.username = username;
        this.pin = pin;
    }

    public int getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPin() { return pin; }
}
