package com.mycompany.atmmanagementsys;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionData {
    private final StringProperty transID;
    private final StringProperty transType;
    private final StringProperty transAmount;

    //constructor
    public TransactionData(String transID, String transType, String transAmount) {
        this.transID = new SimpleStringProperty(transID);
        this.transType = new SimpleStringProperty(transType);
        this.transAmount = new SimpleStringProperty(transAmount);
    }

    //getter functions
    public String getTransID() {
        return transID.get();
    }

    public String getTransType() {
        return transType.get();
    }

    public String getTransAmount() {
        return transAmount.get();
    }
}
