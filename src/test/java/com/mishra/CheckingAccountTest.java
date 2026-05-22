package com.mishra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest extends TestBase {

    @BeforeEach
    void createTestUser() throws Exception {
        Account account = new Account("testcust", "pass", "pass", "Test Customer");
        account.signUp();
    }

    @Test
    void testOpenCheckingAccount() {
        CheckingAccount ca = new CheckingAccount("CHK001", "Test Customer", "testcust", "1000.0");
        assertTrue(ca.openAcct(), "Should open checking account successfully");
    }

    @Test
    void testOpenDuplicateAccount() {
        CheckingAccount ca1 = new CheckingAccount("CHK002", "Test Customer", "testcust", "500.0");
        assertTrue(ca1.openAcct());

        CheckingAccount ca2 = new CheckingAccount("CHK002", "Test Customer", "testcust", "600.0");
        assertFalse(ca2.openAcct(), "Should not open duplicate account");
    }

    @Test
    void testGetBalance() {
        CheckingAccount ca = new CheckingAccount("CHK003", "Test Customer", "testcust", "750.0");
        ca.openAcct();

        CheckingAccount query = new CheckingAccount("CHK003");
        float balance = query.getBalance();
        assertEquals(750.0f, balance, 0.01f, "Balance should be 750.0");
    }

    @Test
    void testDeposit() {
        CheckingAccount ca = new CheckingAccount("CHK004", "Test Customer", "testcust", "1000.0");
        ca.openAcct();

        CheckingAccount deposit = new CheckingAccount("CHK004", "Test Customer", "testcust", "250.0");
        assertTrue(deposit.deposit("testcust"), "Deposit should succeed");

        CheckingAccount query = new CheckingAccount("CHK004");
        assertEquals(1250.0f, query.getBalance(), 0.01f);
    }

    @Test
    void testWithdraw() {
        CheckingAccount ca = new CheckingAccount("CHK005", "Test Customer", "testcust", "1000.0");
        ca.openAcct();

        CheckingAccount withdraw = new CheckingAccount("CHK005", "Test Customer", "testcust", "300.0");
        assertTrue(withdraw.Withdraw("testcust"), "Withdraw should succeed");

        CheckingAccount query = new CheckingAccount("CHK005");
        assertEquals(700.0f, query.getBalance(), 0.01f);
    }

    @Test
    void testGetAccno() {
        CheckingAccount ca = new CheckingAccount("CHK006", "Test Customer", "testcust", "500.0");
        ca.openAcct();

        CheckingAccount query = new CheckingAccount();
        assertEquals("CHK006", query.getAccno("testcust"));
    }
}
