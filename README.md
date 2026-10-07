# Online-Banking-System-using-Java
This was a project in my Java class where I had to develop an online banking system with the use cases provided by the professor.

Based on the use case, I had to do the following so that a customer can open a checking account and/or a savings account.
1.	Create tables CheckingAccount(CheckingAccountNumber, CustomerName, Balance, CustomerID), SavingsAccount(SavingsAccountNumber, CustomerName, Balance, InterestRate, CustomerID), Transactions(TransactionNumber, TransactionAmount, TransactionType, TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID). Use varchar(50) for any non-numerical value including AccountNumber and TransactionNumber, and float for any numerical value. CustomerID here means Username.
2.	Study entity class CheckingAccount.java, and then develop your entity classes SavingsAccount.java and Transaction.java that has a method for recording a transaction.
3.	Add necessary statements to OpenBankAccountControl.java to open both Checking and Savings accounts, and record the transactions of opening deposits.

 In continuation to the above, had to do then do the following:
 
Develop use cases of Account Overview, Inquire Transactions, Deposit, and Withdraw by using the services from your bank as examples. And develop your Java programs to fully implement the functionalities with access to database. Assume that a customer has two accounts, Savings Account and Checking Account. Account Overview shows the current balances of customers’ Savings Account and Checking Account. Inquire Transactions allow customers to search specific transactions by entering a starting date and ending date.
1.	Develop methods deposit(), withdraw() in your classes CheckingAccount and SavingsAccount, getBalance() and calculateInterests() in class SavingsAccount,  searchTransaction() in class Transactions. Then implement Transfer, Deposit, and Withdraw functionalities. It means that your program shows Transfer, Deposit, or Withdraw after successful login. Your current program is to show Open Bank Account window after successful login. Modifications are necessary. 
2.	Organize these functionalities as tabs in one window. The default tab is Account Overview. Please combine Open Account into this application.  Show the window after successful login. These tabs must be in your window, Account Overview, Open Account, Deposit, Withdraw, Transfer and Inquire Transactions subtabs.
 

## Build, test and run

Requirements: JDK 17+ (tested on 21), Maven 3.6+, Docker.

```bash
mvn package                      # compile, run unit tests (in-memory H2, no DB needed), build target/online-banking.war

docker compose up -d             # SQL Server 2022 on localhost:1433 (SA password: $MSSQL_SA_PASSWORD, default BankDev_Passw0rd!)
docker compose exec mssql /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P 'BankDev_Passw0rd!' -i /db/schema.sql

DB_URL='jdbc:sqlserver://localhost:1433;databaseName=JavaClass;user=sa;password=BankDev_Passw0rd!;encrypt=false' \
  mvn -DskipTests jetty:run-war  # http://localhost:8080/ (pages are served under /CSCI6810/)
```

`DBConnection` reads the JDBC URL from the `db.url` system property or the `DB_URL` environment variable and falls back
to the original Windows integrated-security URL.
