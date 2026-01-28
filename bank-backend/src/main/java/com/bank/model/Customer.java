package com.bank.model;

public class Customer {
    /** Database record ID */
    private int id;
    /** Customer identifier string */
    private String customerId;
    /** Customer's full name */
    private String name;
    /** Customer's login username */
    private String username;
    /** Customer's login PIN */
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
