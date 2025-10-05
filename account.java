# chaincode-java-srcpackage org.hyperledger.fabric.samples.account;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public final class Account {

    @Property()
    private final String dealerID;

    @Property()
    private final String msisdn;

    @Property()
    private final String mpin;

    @Property()
    private final double balance;

    @Property()
    private final String status;

    @Property()
    private final double transAmount;

    @Property()
    private final String transType;

    @Property()
    private final String remarks;

    public String getDealerID() {
        return dealerID;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMpin() {
        return mpin;
    }

    public double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public double getTransAmount() {
        return transAmount;
    }

    public String getTransType() {
        return transType;
    }

    public String getRemarks() {
        return remarks;
    }

    public Account(@JsonProperty("dealerID") final String dealerID,
                   @JsonProperty("msisdn") final String msisdn,
                   @JsonProperty("mpin") final String mpin,
                   @JsonProperty("balance") final double balance,
                   @JsonProperty("status") final String status,
                   @JsonProperty("transAmount") final double transAmount,
                   @JsonProperty("transType") final String transType,
                   @JsonProperty("remarks") final String remarks) {
        this.dealerID = dealerID;
        this.msisdn = msisdn;
        this.mpin = mpin;
        this.balance = balance;
        this.status = status;
        this.transAmount = transAmount;
        this.transType = transType;
        this.remarks = remarks;
    }
}
