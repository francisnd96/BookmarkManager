<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<html>
<head><title>Error</title></head>
<body>
<style>
form {
 width: 500px;
 overflow:hidden;}
label {
 clear: both;
 float: left;
 width: 40%;}
input {
 float: left;
 width: 55%;}
</style>
<h1>Error</h1>
<%
String errorMsg="Access denied";
String errorid=request.getParameter("errorid");
System.out.print(errorid);
if(errorid!=null){
	if(errorid.equals("1.jsp")){
		errorMsg+= " - Folder already exists in database.";
	}else if(errorid.equals("2.jsp")){
		errorMsg+=" - Parent Path does not exist";
	}else if(errorid.equals("3.jsp")){
		errorMsg+=" - This folder is locked, items cannot be added";
	}
}else{
	 errorMsg+=" - An unexpected error has occurred.";
}
%>
<div>
	<label for="link"><%=errorMsg%></label>
</div>
<br /><br /><br />
<p><a href="/view/listAll">Return to Tree</a></p>
</body></html>



