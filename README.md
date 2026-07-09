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

## Project layout

This project is a standard Maven web application:

```
src/main/java      Java sources (entity classes in package com.mishra, servlets in the default package)
src/main/webapp    JSP / HTML views
src/test/java      JUnit + Mockito unit tests
```

## Build & test

Requires JDK 8+ and Maven 3.6+.

```bash
mvn clean test          # compile and run the unit tests
mvn clean test jacoco:report   # tests + HTML/XML coverage report (target/site/jacoco/)
mvn clean verify        # tests + enforce the 80% line-coverage gate (jacoco:check)
mvn clean package       # build the deployable WAR (target/online-banking-system.war)
```

### Test coverage

Unit tests are written with JUnit 4 and Mockito. Database access is unit-tested
without a live SQL Server: `DBConnection` accepts an injected `java.sql.Connection`,
and each entity class (`Account`, `CheckingAccount`, `SavingAccount`, `Transaction`,
`EnquireTransaction`) accepts an injected `DBConnection` via a package-private setter,
so JDBC (`Connection` / `Statement` / `ResultSet`) is fully mocked. `mockito-inline`
is used to mock `Account` construction inside the servlet tests.

Coverage is enforced by the `jacoco-maven-plugin`: `mvn verify` fails the build if
bundle line coverage drops below 80%.
 
