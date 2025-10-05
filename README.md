📘 Hyperledger Fabric Assignment
This repository contains the internship assignment implementation using Hyperledger Fabric:

Level-1 → Setup and run the Fabric test network
Level-2 → Develop and deploy a Java chaincode (AccountContract)
Level-3 → Build a Spring Boot REST API that invokes the chaincode and containerize it with Docker
🚀 Prerequisites
Ensure you have the following installed:

Docker & Docker Compose
Git
Java 17+
Maven 3.8+
Hyperledger Fabric Samples & Binaries
Clone Fabric samples:

git clone https://github.com/hyperledger/fabric-samples.git
cd fabric-samples
# Optional: checkout stable branch
# git checkout release-2.5
Download Fabric binaries & images:

curl -sSL https://bit.ly/2ysbOFE | bash -s -- 2.5.0 1.5.0
🧩 Level-1: Start Test Network
From inside fabric-samples/test-network:

# Start Fabric test network with 2 orgs, orderer, CA, CouchDB
./network.sh up createChannel -c mychannel -s couchdb

# To stop and clean
./network.sh down
This creates mychannel where we will deploy the Java chaincode.

⚙️ Level-2: Java Chaincode
📂 Code location: chaincode-java/

The chaincode (AccountContract) manages account assets with fields:

DEALERID, MSISDN, MPIN, BALANCE, STATUS, TRANSAMOUNT, TRANSTYPE, REMARKS
Key Functions
CreateAccount(id, dealerId, msisdn, mpin, balance, status)
ReadAccount(id)
UpdateAccount(id, mpin, status, remarks)
ApplyTransaction(id, amount, trType, remarks)
(trType = credit | debit)
GetAccountHistory(id)
GetAllAccounts()
Build Chaincode
cd chaincode-java
mvn clean package
Deploy Chaincode
From fabric-samples/test-network:

./network.sh deployCC -ccn accountcc -ccp ../chaincode-java -ccl java
Quick Test (CLI)
docker exec -it cli bash

# Create account
peer chaincode invoke -C mychannel -n accountcc \
  -c '{"function":"CreateAccount","Args":["acct1","dealer123","+919900112233","1234","1000.0","ACTIVE"]}'

# Query account
peer chaincode query -C mychannel -n accountcc \
  -c '{"Args":["ReadAccount","acct1"]}'
🌐 Level-3: REST API (Spring Boot)
📂 Code location: rest-api-java/

The REST API uses Fabric Gateway Java SDK to connect to Fabric and invoke the chaincode.

Endpoints
Method	Endpoint	Description
POST	/accounts	Create new account
GET	/accounts/{id}	Read account
PUT	/accounts/{id}	Update account fields
POST	/accounts/{id}/transaction	Apply credit/debit
GET	/accounts/{id}/history	Get account history
Configuration
Update rest-api-java/src/main/resources/application.properties:

server.port=8080
fabric.connection.profile=connection.json
fabric.wallet.path=wallet
fabric.user=appUser
fabric.channel=mychannel
fabric.chaincode=accountcc
⚠️ Copy connection.json from fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/
⚠️ Copy user credentials into rest-api-java/wallet/ (e.g. appUser created by test-network).
Do not commit private keys to GitHub.

Run Locally
cd rest-api-java
mvn spring-boot:run
Build JAR
mvn clean package
java -jar target/fabric-rest-api-java-1.0.0.jar
Run with Docker
cd rest-api-java
docker build -t fabric-rest-api-java:1.0 .

docker run -d --name fabric-rest-api -p 8080:8080 \
  -v $(pwd)/connection.json:/app/connection.json \
  -v $(pwd)/wallet:/app/wallet \
  --network test-network_default \
  fabric-rest-api-java:1.0
🧪 Example API Usage
Create Account
curl -X POST http://localhost:8080/accounts \
 -H "Content-Type: application/json" \
 -d '{"id":"acct1","dealerID":"dealer123","msisdn":"+919900112233","mpin":"1234","balance":1000,"status":"ACTIVE"}'
Read Account
curl http://localhost:8080/accounts/acct1
Debit Transaction
curl -X POST http://localhost:8080/accounts/acct1/transaction \
 -H "Content-Type: application/json" \
 -d '{"amount":100,"trType":"debit","remarks":"purchase"}'
Account History
curl http://localhost:8080/accounts/acct1/history
📂 Repository Layout
hyperledger-fabric-assignment/
├── README.md
├── chaincode-java/       # Java chaincode (AccountContract)
├── rest-api-java/        # Spring Boot REST API + Dockerfile
├── docs/                 # Supporting docs (assignment.pdf, screenshots)
└── instructions/         # Helper scripts and guides
⚠️ Notes & Best Practices
Use Java 17+ for both chaincode and REST API.
Use Fabric v2.4+ with test-network.
Never commit wallet credentials or private keys to public repos.
For money, prefer integer minor units (e.g., cents) instead of floating-point.
REST API connects with Fabric Gateway and needs proper network config.
✅ Next Steps
Clone this repo
Start Fabric test-network (Level-1)
Build & deploy chaincode (Level-2)
Run REST API locally or in Docker (Level-3)
Test endpoints with curl/Postman
🔗 This completes the Hyperledger Fab
