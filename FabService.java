package com.example.fabricrestapi;

import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.net.ssl.SslContext;

@Configuration
public class FabricGatewayConfig {

    private static final String MSP_ID = "Org1MSP";
    private static final String CHANNEL_NAME = "mychannel";
    private static final String CHAINCODE_NAME = "account";
    private static final String PEER_ENDPOINT = "localhost:7051";
    private static final String OVERRIDE_AUTH = "peer0.org1.example.com";

    // Adjust these paths to your crypto materials
    private static final String CERT_PATH = "path/to/cert.pem";
    private static final String KEY_PATH = "path/to/private-key.pem";
    private static final String TLS_CERT_PATH = "path/to/tls-ca-cert.pem";

    @Bean
    public Gateway gateway() throws Exception {
        ManagedChannel channel = newChannel();
        Gateway.Builder builder = Gateway.newInstance()
                .identity(newIdentity())
                .signer(newSigner())
                .connection(channel)
                .evaluateOptions(options -> options.withDeadlineAfter(5, java.util.concurrent.TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, java.util.concurrent.TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, java.util.concurrent.TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, java.util.concurrent.TimeUnit.MINUTES));
        return builder.connect();
    }

    private ManagedChannel newChannel() throws IOException {
        SslContext sslContext = GrpcSslContexts.forClient()
                .trustManager(Paths.get(TLS_CERT_PATH).toFile())
                .build();

        return NettyChannelBuilder.forTarget(PEER_ENDPOINT)
                .overrideAuthority(OVERRIDE_AUTH)
                .sslContext(sslContext)
                .build();
    }

    private Identity newIdentity() throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(Paths.get(CERT_PATH));
        X509Certificate certificate = Identities.readX509Certificate(certReader);
        return new X509Identity(MSP_ID, certificate);
    }

    private Signer newSigner() throws IOException, InvalidKeyException {
        var keyReader = Files.newBufferedReader(Paths.get(KEY_PATH));
        PrivateKey privateKey = Identities.readPrivateKey(keyReader);
        return Signers.newPrivateKeySigner(privateKey);
    }

    public String getChannelName() {
        return CHANNEL_NAME;
    }

    public String getChaincodeName() {
        return CHAINCODE_NAME;
    }
}
