package org.ozyegin.cs.entity;

import java.util.List;

public class Email {
    private int emailId;
    private String email;
    private String companyName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }
    public Email emailId(int emailId) {
        this.emailId = emailId;
        return this;
    }
    public Email email(String email) {
        this.email = email;
        return this;
    }
    public Email companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

}
