package org.ozyegin.cs.entity;

import java.util.Date;


public class Transaction {
    int transactionId;
    String company;
    int productId;
    int amount;
    Date createdDate;

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

    public Transaction company(String company) {
        this.company = company;
        return this;
    }
    public Transaction amount(int amount) {
        this.amount = amount;
        return this;
    }
    public Transaction productId(int productId) {
        this.productId = productId;
        return this;
    }
    public Transaction createdDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    public Transaction transactionId(int transactionId) {
        this.transactionId = transactionId;
        return this;
    }

}
