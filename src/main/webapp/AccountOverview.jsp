<!--
/******************************************************************************
*	Program Author: Kavita for CSCI 6810 Java and the Internet	  *
*	Date: November, 2018													  *
*******************************************************************************/
-->
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="com.mishra.*" %>

<%
String UName = new String("");
	UName = request.getParameter( "UserID" );
	//out.println("Your username is " + UName);
			
	String Name = new String("");
	Name = request.getParameter("CustomerName");
	//out.println("Your customername is " + Name);
	
	
	CheckingAccount ca = new CheckingAccount();
	SavingAccount sa = new SavingAccount();
	String checkingNumber = ca.getAccno(UName);
	String savingNumber = sa.getAccno(UName);
%>

<HTML><HEAD></HEAD>
<BODY>
    <FORM NAME="AccountOverviewForm" ACTION="" METHOD ="POST">
        <INPUT TYPE='hidden' NAME='UserID' VALUE='<%=request.getParameter("UserID")%>'>

        <TABLE cellPadding=3 ALIGN='center'>

            <TR bgcolor='#F1F1FD'>
                <td> Account Type</td>
		<td> Account Number</td>
          	<td> Balance</td>
            </TR>
            <TR bgcolor='#F1F1FD'>
	        <td><%= "Checking Account" %></td> 
		<td><%= checkingNumber == null ? "none" : checkingNumber %></td>
		<td><%= checkingNumber == null ? "-" : String.valueOf(ca.getBalance(checkingNumber)) %></td>
            </TR>
            <TR bgcolor='#F1F1FD'>
	    	<td><%= "Saving Account" %></td> 
		<td><%= savingNumber == null ? "none" : savingNumber %></td>
		<td><%= savingNumber == null ? "-" : String.valueOf(sa.getBalance(savingNumber)) %></td>
            </TR>
            </TABLE><BR>
		<CENTER><INPUT TYPE="SUBMIT" NAME='submitBNTN' VALUE='Refresh'></CENTER><BR>
		
	</FORM>

	</BODY>
	</HTML>
<SCRIPT LANGUAGE='JavaScript'>
</SCRIPT>