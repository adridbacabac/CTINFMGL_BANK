package com.bank.util;

import com.bank.model.Customer;
import com.bank.model.Account;

public class Session {
    /** The currently logged-in customer, null if not authenticated */
    public static Customer currentCustomer;
    /** The currently selected account for the logged-in customer */
    public static Account currentAccount;

    public static void clear() {
        currentCustomer = null;
        currentAccount = null;
    }
}
