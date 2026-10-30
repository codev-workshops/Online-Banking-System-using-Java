<!--
/******************************************************************************
*	Program Author: Kavita for CSCI 6810 Java and the Internet	  *
*	Date: November, 2018													  *
*******************************************************************************/
-->
<%@ page import="com.mishra.AuthFilter" %>
<%@ page import="com.mishra.CheckingAccount" %>
<%@ page import="com.mishra.SavingAccount" %>
<%!
	private static String lastFour(String accountNumber) {
		if (accountNumber == null) return "-";
		return accountNumber.length() <= 4 ? accountNumber : accountNumber.substring(accountNumber.length() - 4);
	}
%>
<%
	// The customer comes from the login session only; request parameters are never trusted.
	String UName = (String) session.getAttribute(AuthFilter.USER_ID);

	CheckingAccount ca = new CheckingAccount();
	String checkingNumber = ca.getAccno(UName);
	String checkingBalance = checkingNumber == null ? "-" : String.valueOf(ca.getBalance(checkingNumber));
	SavingAccount sa = new SavingAccount();
	String savingNumber = sa.getAccno(UName);
	String savingBalance = savingNumber == null ? "-" : String.valueOf(sa.getBalance(savingNumber));
%>

<HTML><HEAD></HEAD>
<BODY>
    <FORM NAME="AccountOverviewForm" ACTION="" METHOD ="POST">
        <INPUT TYPE='hidden' NAME='csrfToken' VALUE='<%= AuthFilter.csrfToken(session) %>'>

        <TABLE cellPadding=3 ALIGN='center'>

            <TR bgcolor='#F1F1FD'>
                <td> Account Type</td>
		<td> Account Number</td>
          	<td> Balance</td>
            </TR>
            <TR bgcolor='#F1F1FD'>
	        <td><%= "Checking Account" %></td> 
		<td><%= lastFour(checkingNumber) %></td>
		<td><%= checkingBalance %></td>
            </TR>
            <TR bgcolor='#F1F1FD'>
	    	<td><%= "Saving Account" %></td> 
		<td><%= lastFour(savingNumber) %></td>
		<td><%= savingBalance %></td>
            </TR>
            </TABLE><BR>
		<CENTER><INPUT TYPE="SUBMIT" NAME='submitBNTN' VALUE='Refresh'></CENTER><BR>
		
	</FORM>

	</BODY>
	</HTML>
<SCRIPT LANGUAGE='JavaScript'>
</SCRIPT>