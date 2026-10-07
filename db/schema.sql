-- SQL Server schema for the Online Banking System (database JavaClass).
IF DB_ID('JavaClass') IS NULL CREATE DATABASE JavaClass;
GO
USE JavaClass;
GO
IF OBJECT_ID('Account') IS NULL
  CREATE TABLE Account (Username varchar(50) PRIMARY KEY, Password varchar(50), Name varchar(50));
IF OBJECT_ID('CheckingAccount') IS NULL
  CREATE TABLE CheckingAccount (CheckingAccountNumber varchar(50) PRIMARY KEY, CustomerName varchar(50),
    Balance float, CustomerID varchar(50));
IF OBJECT_ID('SavingAccount') IS NULL
  CREATE TABLE SavingAccount (SavingAccountNumber varchar(50) PRIMARY KEY, CustomerName varchar(50),
    Balance float, InterestRate float, CustomerID varchar(50));
IF OBJECT_ID('Transactions') IS NULL
  CREATE TABLE Transactions (TransactionNumber varchar(50) PRIMARY KEY, TransactionAmount float,
    TransactionType varchar(50), TransactionTime varchar(50), TransactionDate varchar(50),
    FromAccount varchar(50), ToAccount varchar(50), CustomerID varchar(50));
GO
