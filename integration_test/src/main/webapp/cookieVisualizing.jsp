<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>This page lists all available cookies</title>
</head>
<body>

<h1>This page lists all available cookies</h1>
<ul id="cookies"></ul>
<script>
    function listCookies() {
        var theCookies = document.cookie.split(';');
        var aString = '';
        for (var i = 1; i <= theCookies.length; i++) {
            aString += "<li>" + i + ' ' + theCookies[i - 1] + "</li>";
        }
        return aString;
    }
    document.getElementById("cookies").innerHTML = listCookies();
</script>
</body>
</html>
