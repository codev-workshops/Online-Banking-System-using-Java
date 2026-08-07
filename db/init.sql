-- Schema for the Online Banking System.
-- Identifiers are unquoted so PostgreSQL folds them to lower case, which keeps
-- the application's mixed-case SQL literals working unchanged.

CREATE TABLE IF NOT EXISTS Account (
    Username VARCHAR(50) PRIMARY KEY,
    Password VARCHAR(50) NOT NULL,
    Name     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS CheckingAccount (
    CheckingAccountNumber VARCHAR(50) PRIMARY KEY,
    CustomerName          VARCHAR(50),
    Balance               REAL,
    CustomerID            VARCHAR(50) REFERENCES Account(Username)
);

CREATE TABLE IF NOT EXISTS SavingAccount (
    SavingAccountNumber VARCHAR(50) PRIMARY KEY,
    CustomerName        VARCHAR(50),
    Balance             REAL,
    InterestRate        REAL DEFAULT 0.01,
    CustomerID          VARCHAR(50) REFERENCES Account(Username)
);

CREATE TABLE IF NOT EXISTS Transactions (
    TransactionNumber VARCHAR(50) PRIMARY KEY,
    TransactionAmount REAL,
    TransactionType   VARCHAR(50),
    TransactionTime   VARCHAR(50),
    TransactionDate   VARCHAR(50),
    FromAccount       VARCHAR(50),
    ToAccount         VARCHAR(50),
    CustomerID        VARCHAR(50)
);

INSERT INTO Account (Username, Password, Name)
VALUES ('demo', 'demo', 'Demo Customer')
ON CONFLICT (Username) DO NOTHING;

INSERT INTO CheckingAccount (CheckingAccountNumber, CustomerName, Balance, CustomerID)
VALUES ('CHK1000000001', 'Demo Customer', 2500.00, 'demo')
ON CONFLICT (CheckingAccountNumber) DO NOTHING;

INSERT INTO SavingAccount (SavingAccountNumber, CustomerName, Balance, InterestRate, CustomerID)
VALUES ('SAV1000000001', 'Demo Customer', 10750.50, 0.02, 'demo')
ON CONFLICT (SavingAccountNumber) DO NOTHING;
