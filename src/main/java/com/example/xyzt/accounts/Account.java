package com.example.xyzt.accounts;

import com.example.xyzt.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private Integer accountId;
    private Float balance;
    private String currency;
//    private Integer roleStatus;
    private Integer partnerId;  //account Holder

    private List<Transaction> transactions = new ArrayList<>();


    //______________________________________________________________________________
    public Account(Integer accountId, Float balance, String currency, Integer partnerId) {
        this.accountId = accountId;
        this.balance = balance;
        this.currency = currency;
        this.partnerId = partnerId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Float getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", partnerId=" + partnerId +
                '}';
    }
}
