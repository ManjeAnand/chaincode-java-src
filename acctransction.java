package org.hyperledger.fabric.samples.account;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "account",
        info = @Info(
                title = "Account transfer",
                description = "The sample account transfer",
                version = "1.0.0"))
@Default
public final class AccountTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AccountErrors {
        ACCOUNT_NOT_FOUND,
        ACCOUNT_ALREADY_EXISTS
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateAccount(ctx, "D001", "1234567890", "1234", 1000.0, "Active", 0.0, "", "Initial deposit");
        CreateAccount(ctx, "D002", "0987654321", "5678", 500.0, "Active", 0.0, "", "Initial setup");
    }

    @Transaction()
    public Account CreateAccount(final Context ctx, final String dealerID, final String msisdn, final String mpin,
                                 final double balance, final String status, final double transAmount,
                                 final String transType, final String remarks) {
        ChaincodeStub stub = ctx.getStub();

        String accountJSON = stub.getStringState(msisdn);
        if (!accountJSON.isEmpty()) {
            String errorMessage = String.format("Account %s already exists", msisdn);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountErrors.ACCOUNT_ALREADY_EXISTS.toString());
        }

        Account account = new Account(dealerID, msisdn, mpin, balance, status, transAmount, transType, remarks);
        accountJSON = genson.serialize(account);
        stub.putStringState(msisdn, accountJSON);

        return account;
    }

    @Transaction()
    public Account ReadAccount(final Context ctx, final String msisdn) {
        ChaincodeStub stub = ctx.getStub();
        String accountJSON = stub.getStringState(msisdn);

        if (accountJSON == null || accountJSON.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", msisdn);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountErrors.ACCOUNT_NOT_FOUND.toString());
        }

        return genson.deserialize(accountJSON, Account.class);
    }

    @Transaction()
    public void UpdateAccount(final Context ctx, final String msisdn, final String dealerID, final String mpin,
                              final double balance, final String status, final double transAmount,
                              final String transType, final String remarks) {
        ChaincodeStub stub = ctx.getStub();

        String accountJSON = stub.getStringState(msisdn);

        if (accountJSON == null || accountJSON.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", msisdn);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AccountErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account account = new Account(dealerID, msisdn, mpin, balance, status, transAmount, transType, remarks);

        accountJSON = genson.serialize(account);
        stub.putStringState(msisdn, accountJSON);
    }

    @Transaction()
    public boolean AccountExists(final Context ctx, final String msisdn) {
        ChaincodeStub stub = ctx.getStub();
        String accountJSON = stub.getStringState(msisdn);

        return (accountJSON != null && !accountJSON.isEmpty());
    }

    @Transaction()
    public String GetAllAccounts(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        QueryResultsIterator<KeyModification> iterator = stub.getHistoryForKey("");
        List<Account> accounts = new ArrayList<Account>();

        for (KeyModification result: iterator) {
            Account acct = genson.deserialize(result.getStringValue(), Account.class);
            accounts.add(acct);
        }

        return genson.serialize(accounts);
    }

    @Transaction()
    public String GetAccountHistory(final Context ctx, final String msisdn) {
        ChaincodeStub stub = ctx.getStub();

        QueryResultsIterator<KeyModification> iterator = stub.getHistoryForKey(msisdn);
        List<String> history = new ArrayList<String>();

        for (KeyModification mod: iterator) {
            String txId = mod.getTxId();
            String value = mod.getStringValue();
            String timestamp = mod.getTimestamp().toString();
            boolean isDelete = mod.isDelete();
            history.add("TxID: " + txId + ", Timestamp: " + timestamp + ", Value: " + value + ", IsDelete: " + isDelete);
        }

        return genson.serialize(history);
    }
}
