<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="com.mishra.*" %>

<HTML>
<HEAD>
<STYLE> 
	ul{list-style-type: none; padding: 2%;} 
	li{display: inline; padding: 2%;}
</STYLE>
</HEAD>
<BODY>
<h4 ALIGN='center'>Welcome to your link page</h4>
<FORM NAME="LogoutForm" ACTION="/LogoutServlet" METHOD="POST" TARGET="_top">
<INPUT TYPE='hidden' NAME='csrfToken' VALUE='<%= AuthFilter.csrfToken(session) %>'>
<TR bgcolor='#ECFAEB'>
<div id='navigation'>
<ul>
<li><A HREF='/CSCI6810/AccountOverview.jsp' TARGET='display'>Account Overview</A></li>
<li><A HREF='/CSCI6810/PreOpenBankAccount.jsp' TARGET='display'>Open Bank Account</A></li>
<li><A HREF='/CSCI6810/PreWithdraw.jsp' TARGET='display'>Withdraw</A></li>
<li><A HREF='/CSCI6810/PreDeposit.jsp' TARGET='display'>Deposit</A></li>
<li><A HREF='/CSCI6810/PreTransfer.jsp' TARGET='display'>Transfer</A></li>
<li><A HREF='/CSCI6810/InquireTransactions.jsp' TARGET='display'>Inquire Transaction</A></li>
<li><INPUT TYPE='submit' VALUE='Logout'></li>
</ul>
</div>
</TR>
</FORM>
</BODY>
<SCRIPT LANGUAGE=\"JavaScript\">
function checkInputs()
{
	var Prompts = \"\";
	Username = window.document.LoginPage.UserName.value;
	Password = window.document.LoginPage.PassWord.value;
	if (Username == \"\" || Password == \"\") 
	{
		if (Username == "")
		Prompts +=\"Please enter your username!\n";
		if (Password == "")
		Prompts +=\"Please enter your password!\n";
		if (Prompts != "")
		window.alert(Prompts);
	}
	else 
	{	
		document.LoginPage.submit();
	}
}
</SCRIPT>
</HTML>