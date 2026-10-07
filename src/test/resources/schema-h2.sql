CREATE TABLE IF NOT EXISTS Account (Username varchar(50) PRIMARY KEY, Password varchar(50), Name varchar(50));
CREATE TABLE IF NOT EXISTS CheckingAccount (CheckingAccountNumber varchar(50) PRIMARY KEY,
  CustomerName varchar(50), Balance float, CustomerID varchar(50));
CREATE TABLE IF NOT EXISTS SavingAccount (SavingAccountNumber varchar(50) PRIMARY KEY,
  CustomerName varchar(50), Balance float, InterestRate float, CustomerID varchar(50));
CREATE TABLE IF NOT EXISTS Transactions (TransactionNumber varchar(50) PRIMARY KEY, TransactionAmount float,
  TransactionType varchar(50), TransactionTime varchar(50), TransactionDate varchar(50),
  FromAccount varchar(50), ToAccount varchar(50), CustomerID varchar(50));
