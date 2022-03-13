
package com.mycompany.atmmanagementsys;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoanData {
    private final StringProperty loanID;
    private final StringProperty accountID;
    private final StringProperty loanReason;
    private final StringProperty loanStatus;
    private final StringProperty loanAmount;
//constructor
    public LoanData(String loanID, String accountID, String loanReason, String loanStatus, String loanAmount) {
        this.loanID = new SimpleStringProperty(loanID);
        this.accountID = new SimpleStringProperty(accountID);
        this.loanReason = new SimpleStringProperty(loanReason);
        this.loanStatus = new SimpleStringProperty(loanStatus);
        this.loanAmount = new SimpleStringProperty(loanAmount);
    }

//getter
    public String getLoanID() {
        return loanID.get();
    }

    public String getAccountNo() {
        return accountID.get();
    }

    public String getLoanReason() {
        return loanReason.get();
    }

    public String getLoanStatus() {
        return loanStatus.get();
    }

    public String getLoanAmount() {
        return loanAmount.get();
    }
}
