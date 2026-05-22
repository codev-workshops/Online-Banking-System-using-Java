CREATE TABLE IF NOT EXISTS Account (
    Username VARCHAR(50) PRIMARY KEY,
    Password VARCHAR(50) NOT NULL,
    Name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS CheckingAccount (
    CheckingAccountNumber VARCHAR(50) PRIMARY KEY,
    CustomerName VARCHAR(50),
    Balance FLOAT,
    CustomerID VARCHAR(50),
    FOREIGN KEY (CustomerID) REFERENCES Account(Username)
);

CREATE TABLE IF NOT EXISTS SavingAccount (
    SavingAccountNumber VARCHAR(50) PRIMARY KEY,
    CustomerName VARCHAR(50),
    Balance FLOAT,
    InterestRate FLOAT DEFAULT 0.01,
    CustomerID VARCHAR(50),
    FOREIGN KEY (CustomerID) REFERENCES Account(Username)
);

CREATE TABLE IF NOT EXISTS Transactions (
    TransactionNumber VARCHAR(50) PRIMARY KEY,
    TransactionAmount FLOAT,
    TransactionType VARCHAR(50),
    TransactionTime VARCHAR(50),
    TransactionDate VARCHAR(50),
    FromAccount VARCHAR(50),
    ToAccount VARCHAR(50),
    CustomerID VARCHAR(50)
);
