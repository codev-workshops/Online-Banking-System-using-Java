# Online-Banking-System-using-Java

A 2012-era JSP/Servlet online banking application, replatformed to run as a
container against PostgreSQL.

## Run it

```bash
docker compose up --build
open http://localhost:8080/CSCI6810/login.html   # demo / demo
```

On Kubernetes (local `kind` cluster):

```bash
kind create cluster --name banking --config k8s/kind-cluster.yaml
docker build -t online-banking:local .
kind load docker-image online-banking:local --name banking
kubectl apply -f k8s/
kubectl rollout status deploy/banking-app
open http://localhost:30080/CSCI6810/login.html
```

Database configuration is read from `DB_URL`, `DB_USER` and `DB_PASSWORD`.
The schema and demo data live in `db/init.sql` (mirrored into a ConfigMap in
`k8s/postgres.yaml`).

The app is served at the `/CSCI6810` context path because that path is
hard-coded throughout the original JSPs.

## Known gaps

Sign-up, login and Account Overview work end to end. The remaining screens
(Deposit, Withdraw, Transfer, Open Account, Inquire Transactions) post to
servlets and JSPs that were never committed to this repository; they are
untouched by the replatforming work.

## Original project description

This was a project in my Java class where I had to develop an online banking system with the use cases provided by the professor.

Based on the use case, I had to do the following so that a customer can open a checking account and/or a savings account.
1.	Create tables CheckingAccount(CheckingAccountNumber, CustomerName, Balance, CustomerID), SavingsAccount(SavingsAccountNumber, CustomerName, Balance, InterestRate, CustomerID), Transactions(TransactionNumber, TransactionAmount, TransactionType, TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID). Use varchar(50) for any non-numerical value including AccountNumber and TransactionNumber, and float for any numerical value. CustomerID here means Username.
2.	Study entity class CheckingAccount.java, and then develop your entity classes SavingsAccount.java and Transaction.java that has a method for recording a transaction.
3.	Add necessary statements to OpenBankAccountControl.java to open both Checking and Savings accounts, and record the transactions of opening deposits.

 In continuation to the above, had to do then do the following:
 
Develop use cases of Account Overview, Inquire Transactions, Deposit, and Withdraw by using the services from your bank as examples. And develop your Java programs to fully implement the functionalities with access to database. Assume that a customer has two accounts, Savings Account and Checking Account. Account Overview shows the current balances of customers’ Savings Account and Checking Account. Inquire Transactions allow customers to search specific transactions by entering a starting date and ending date.
1.	Develop methods deposit(), withdraw() in your classes CheckingAccount and SavingsAccount, getBalance() and calculateInterests() in class SavingsAccount,  searchTransaction() in class Transactions. Then implement Transfer, Deposit, and Withdraw functionalities. It means that your program shows Transfer, Deposit, or Withdraw after successful login. Your current program is to show Open Bank Account window after successful login. Modifications are necessary. 
2.	Organize these functionalities as tabs in one window. The default tab is Account Overview. Please combine Open Account into this application.  Show the window after successful login. These tabs must be in your window, Account Overview, Open Account, Deposit, Withdraw, Transfer and Inquire Transactions subtabs.
