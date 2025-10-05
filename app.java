package com.example.fabricrestapi;

public class Account {
    private String dealerID;
    private String msisdn;
    private String mpin;
    private double balance;
    private String status;
    private double transAmount;
    private String transType;
    private String remarks;

    // Getters and Setters
    public String getDealerID() { return dealerID; }
    public void setDealerID(String dealerID) { this.dealerID = dealerID; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public String getMpin() { return mpin; }
    public void setMpin(String mpin) { this.mpin = mpin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTransAmount() { return transAmount; }
    public void setTransAmount(double transAmount) { this.transAmount = transAmount; }
    public String getTransType() { return transType; }
    public void setTransType(String transType) { this.transType = transType; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
