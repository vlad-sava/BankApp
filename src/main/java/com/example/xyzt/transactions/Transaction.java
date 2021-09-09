package com.example.xyzt.transactions;

import java.time.LocalDateTime;

public class Transaction {

    private Integer targetAccount;
    private Integer targetCustomer;
    private Integer sendingAccount;
    private Integer sendingCustomer;
    private Float amount;
    private Integer transactionId;
    private LocalDateTime date;

//    private static Integer transactionIdCounter = 0;

    public Transaction(Integer targetAccount, Integer sendingAccount, Float amount, Integer targetCustomer, Integer sendingCustomer, Integer transationId) {
        this.targetAccount = targetAccount;
        this.sendingAccount = sendingAccount;
        this.amount = amount;
        this.targetCustomer = targetCustomer;
        this.sendingCustomer = sendingCustomer;
        this.transactionId = transationId;

//        this.transactionId = ++transactionIdCounter;
        this.date = LocalDateTime.now();
    }

    public void setTargetCustomer(Integer targetCustomer) {
        this.targetCustomer = targetCustomer;
    }

    public void setSendingCustomer(Integer sendingCustomer) {
        this.sendingCustomer = sendingCustomer;
    }

    public void setTargetAccount(Integer targetAccount) {
        this.targetAccount = targetAccount;
    }

    public void setSendingAccount(Integer sendingAccount) {
        this.sendingAccount = sendingAccount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

//    public static void setTransactionIdCounter(Integer transactionIdCounter) {
//        Transaction.transactionIdCounter = transactionIdCounter;
//    }

    public Integer getTargetAccount() {
        return targetAccount;
    }

    public Integer getSendingAccount() {
        return sendingAccount;
    }

    public Float getAmount() {
        return amount;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Integer getTargetCustomer() {
        return targetCustomer;
    }

    public Integer getSendingCustomer() {
        return sendingCustomer;
    }

//    public static Integer getTransactionIdCounter() {
//        return transactionIdCounter;
//    }

    @Override
    public String toString() {
        return "Transaction{" +
                "targetAccount=" + targetAccount +
                ", targetCustomer=" + targetCustomer +
                ", sendingAccount=" + sendingAccount +
                ", sendingCustomer=" + sendingCustomer +
                ", amount=" + amount +
                ", transactionId=" + transactionId +
                ", date=" + date +
                '}';
    }
}
