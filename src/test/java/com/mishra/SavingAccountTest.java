package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingAccountTest extends TestBase {

    @BeforeEach
    void createTestUser() throws Exception {
        Account account = new Account("savcust", "pass", "pass", "Saving Customer");
        account.signUp();
    }

    @Test
    void testOpenSavingAccount() {
        SavingAccount sa = new SavingAccount("SAV001", "Saving Customer", "savcust", "2000.0");
        assertTrue(sa.openAcct(), "Should open saving account successfully");
    }

    @Test
    void testOpenDuplicateAccount() {
        SavingAccount sa1 = new SavingAccount("SAV002", "Saving Customer", "savcust", "1000.0");
        assertTrue(sa1.openAcct());

        SavingAccount sa2 = new SavingAccount("SAV002", "Saving Customer", "savcust", "500.0");
        assertFalse(sa2.openAcct(), "Should not open duplicate account");
    }

    @Test
    void testGetBalance() {
        SavingAccount sa = new SavingAccount("SAV003", "Saving Customer", "savcust", "3000.0");
        sa.openAcct();

        SavingAccount query = new SavingAccount("SAV003");
        float balance = query.getBalance();
        assertEquals(3000.0f, balance, 0.01f, "Balance should be 3000.0");
    }

    @Test
    void testDeposit() {
        SavingAccount sa = new SavingAccount("SAV004", "Saving Customer", "savcust", "1000.0");
        sa.openAcct();

        SavingAccount deposit = new SavingAccount("SAV004", "Saving Customer", "savcust", "500.0");
        assertTrue(deposit.deposit("savcust"), "Deposit should succeed");

        SavingAccount query = new SavingAccount("SAV004");
        assertEquals(1500.0f, query.getBalance(), 0.01f);
    }

    @Test
    void testWithdraw() {
        SavingAccount sa = new SavingAccount("SAV005", "Saving Customer", "savcust", "2000.0");
        sa.openAcct();

        SavingAccount withdraw = new SavingAccount("SAV005", "Saving Customer", "savcust", "800.0");
        assertTrue(withdraw.Withdraw("savcust"), "Withdraw should succeed");

        SavingAccount query = new SavingAccount("SAV005");
        assertEquals(1200.0f, query.getBalance(), 0.01f);
    }

    @Test
    void testGetAccno() {
        SavingAccount sa = new SavingAccount("SAV006", "Saving Customer", "savcust", "500.0");
        sa.openAcct();

        SavingAccount query = new SavingAccount();
        assertEquals("SAV006", query.getAccno("savcust"));
    }
}
