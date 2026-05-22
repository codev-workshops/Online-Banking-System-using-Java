package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Vector;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest extends TestBase {

    @BeforeEach
    void createTestData() throws Exception {
        Account account = new Account("transcust", "pass", "pass", "Trans Customer");
        account.signUp();

        CheckingAccount ca = new CheckingAccount("TCHK001", "Trans Customer", "transcust", "5000.0");
        ca.openAcct();

        SavingAccount sa = new SavingAccount("TSAV001", "Trans Customer", "transcust", "3000.0");
        sa.openAcct();
    }

    @Test
    void testRecordTransaction() {
        Transaction tx = new Transaction("TCHK001", "TSAV001", "transcust", "100.0", "Transfer");
        String txNumber = tx.recordTransaction();
        assertNotNull(txNumber, "Transaction number should not be null");
        assertFalse(txNumber.isEmpty(), "Transaction number should not be empty");
    }

    @Test
    void testRecordDepositTransaction() {
        Transaction tx = new Transaction("Checking", "transcust", "500.0");
        String txNumber = tx.recordTransaction();
        assertNotNull(txNumber, "Transaction number should not be null for deposit");
    }
}
