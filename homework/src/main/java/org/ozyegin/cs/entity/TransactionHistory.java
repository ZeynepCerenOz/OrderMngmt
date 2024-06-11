package org.ozyegin.cs.entity;

import java.util.Date;


public class TransactionHistory {
    int transactionHistoryId;
    int transactionId;
    String company;
    int productId;
    int amount;
    Date createdDate;

    public int getTransactionHistoryId() {
        return transactionHistoryId;
    }

    public void setTransactionHistoryId(int transactionHistoryId) {
        this.transactionHistoryId = transactionHistoryId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public TransactionHistory company(String company) {
        this.company = company;
        return this;
    }
    public TransactionHistory transactionHistoryID(int transactionHistoryId) {
        this.transactionHistoryId = transactionHistoryId;
        return this;
    }
    public TransactionHistory amount(int amount) {
        this.amount = amount;
        return this;
    }
    public TransactionHistory productId(int productId) {
        this.productId = productId;
        return this;
    }
    public TransactionHistory createdDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    public TransactionHistory transactionId(int transactionId) {
        this.transactionId = transactionId;
        return this;
    }

}
