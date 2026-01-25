package com.bank.util;

import com.bank.model.Customer;
import com.bank.model.Account;

public class Session {
    public static Customer currentCustomer;
    public static Account currentAccount;

    public static void clear() {
        currentCustomer = null;
        currentAccount = null;
    }
}
