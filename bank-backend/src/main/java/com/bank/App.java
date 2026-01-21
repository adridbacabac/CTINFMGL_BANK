package com.bank;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import java.util.Scanner;


public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        while (true) {
            System.out.println("\n=== Bank System Menu ===");
            System.out.println("1. View Accounts");
            System.out.println("2. Add Account");
            System.out.println("3. Update Account Balance");
            System.out.println("4. Delete Account");
            System.out.println("5. Transfer Money");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    accountDAO.viewAccounts();
                    break;
                case 2:
                    System.out.print("Account ID: ");
                    String newId = scanner.nextLine();
                    System.out.print("Account Type: ");
                    String newType = scanner.nextLine();
                    System.out.print("Initial Balance: ");
                    int newBalance = Integer.parseInt(scanner.nextLine());
                    System.out.print("Status: ");
                    String newStatus = scanner.nextLine();
                    accountDAO.addAccount(newId, newType, newBalance, newStatus);
                    break;
                case 3:
                    System.out.print("Account ID to update: ");
                    String upId = scanner.nextLine();
                    System.out.print("New Balance: ");
                    int upBalance = Integer.parseInt(scanner.nextLine());
                    accountDAO.updateBalance(upId, upBalance);
                    break;
                case 4:
                    System.out.print("Account ID to delete: ");
                    String delId = scanner.nextLine();
                    accountDAO.deleteAccount(delId);
                    break;
                case 5:
                    System.out.print("From Account ID: ");
                    String fromAcc = scanner.nextLine();
                    System.out.print("To Account ID: ");
                    String toAcc = scanner.nextLine();
                    System.out.print("Amount to Transfer: ");
                    int amount = Integer.parseInt(scanner.nextLine());
                    System.out.print("Transaction ID: ");
                    String transId = scanner.nextLine();
                    transactionDAO.transferMoney(fromAcc, toAcc, amount, transId);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
