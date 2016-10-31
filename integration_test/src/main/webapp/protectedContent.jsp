<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mock of a protected content page</title>
</head>
<body>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    if (!"admin".equals(username) || !"secret".equals(password)) {
%>
    <form name="login_form" action="protectedContent.jsp" method="get">
        <label for="username">username</label>
        <input type="text" name="username" id="username"/>
        <label for="password">password</label>
        <input type="password" name="password" id="password"/>
        <button type="submit">login</button>
    </form>
<%
    } else {
%>
    <p>sensitive information</p>
<%
    }
%>
</body>
</html>
