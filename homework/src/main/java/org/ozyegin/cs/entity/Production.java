package org.ozyegin.cs.entity;

import com.google.common.base.Objects;

public class Production {
    private int productionId;
    private int productId;
    private String company;
    private int capacity;

    public Production productionId(int productionId) {
        this.productionId = productionId;
        return this;
    }

    public Production productId(int productId) {
        this.productId = productId;
        return this;
    }
    public Production company(String company) {
        this.company = company;
        return this;
    }
    public Production capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }
    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int hashCode() {
        return Objects.hashCode(getProductionId(), getProductId(), getCompany(),getCapacity());
    }
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Production production = (Production) o;
        return getProductionId() == production.getProductionId() &&
                getProductId() == production.getProductId() &&
                getCapacity() == production.getCapacity() &&
                Objects.equal(getCompany(), production.getCompany()) ;
    }
}
