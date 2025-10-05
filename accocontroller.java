package com.example.fabricrestapi;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private Gateway gateway;

    @Autowired
    private FabricGatewayConfig config;

    private Contract getContract() {
        Network network = gateway.getNetwork(config.getChannelName());
        return network.getContract(config.getChaincodeName());
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.submitTransaction("CreateAccount",
                account.getDealerID(), account.getMsisdn(), account.getMpin(),
                String.valueOf(account.getBalance()), account.getStatus(),
                String.valueOf(account.getTransAmount()), account.getTransType(), account.getRemarks());
        return ResponseEntity.ok(new String(result, StandardCharsets.UTF_8));
    }

    @GetMapping("/{msisdn}")
    public ResponseEntity<String> readAccount(@PathVariable String msisdn) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("ReadAccount", msisdn);
        return ResponseEntity.ok(new String(result, StandardCharsets.UTF_8));
    }

    @PutMapping("/{msisdn}")
    public ResponseEntity<String> updateAccount(@PathVariable String msisdn, @RequestBody Account account) throws Exception {
        Contract contract = getContract();
        contract.submitTransaction("UpdateAccount", msisdn,
                account.getDealerID(), account.getMpin(),
                String.valueOf(account.getBalance()), account.getStatus(),
                String.valueOf(account.getTransAmount()), account.getTransType(), account.getRemarks());
        return ResponseEntity.ok("Account updated");
    }

    @GetMapping
    public ResponseEntity<String> getAllAccounts() throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("GetAllAccounts");
        return ResponseEntity.ok(new String(result, StandardCharsets.UTF_8));
    }

    @GetMapping("/history/{msisdn}")
    public ResponseEntity<String> getAccountHistory(@PathVariable String msisdn) throws Exception {
        Contract contract = getContract();
        byte[] result = contract.evaluateTransaction("GetAccountHistory", msisdn);
        return ResponseEntity.ok(new String(result, StandardCharsets.UTF_8));
    }
}
