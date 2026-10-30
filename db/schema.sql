-- SQL Server schema for the JavaClass database (see README for column rules).
IF DB_ID('JavaClass') IS NULL CREATE DATABASE JavaClass;
GO
USE JavaClass;
GO
IF OBJECT_ID('Account') IS NULL
  CREATE TABLE Account (
    Username varchar(50) NOT NULL PRIMARY KEY,
    Password varchar(255) NOT NULL,
    Name varchar(50)
  );
-- Password holds a PasswordUtil PBKDF2 hash (~90 chars); widen databases created with varchar(50).
-- Rows that still hold plaintext passwords will not verify: reset those passwords (or hash them
-- with a one-off migration) before those customers can log in again.
IF COL_LENGTH('Account', 'Password') < 255
  ALTER TABLE Account ALTER COLUMN Password varchar(255) NOT NULL;
IF OBJECT_ID('CheckingAccount') IS NULL
  CREATE TABLE CheckingAccount (
    CheckingAccountNumber varchar(50) NOT NULL PRIMARY KEY,
    CustomerName varchar(50),
    Balance float,
    CustomerID varchar(50)
  );
IF OBJECT_ID('SavingAccount') IS NULL
  CREATE TABLE SavingAccount (
    SavingAccountNumber varchar(50) NOT NULL PRIMARY KEY,
    CustomerName varchar(50),
    Balance float,
    InterestRate float,
    CustomerID varchar(50)
  );
IF OBJECT_ID('Transactions') IS NULL
  CREATE TABLE Transactions (
    TransactionNumber varchar(50) NOT NULL PRIMARY KEY,
    TransactionAmount float,
    TransactionType varchar(50),
    TransactionTime varchar(50),
    TransactionDate varchar(50),
    FromAccount varchar(50),
    ToAccount varchar(50),
    CustomerID varchar(50)
  );
GO
