<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title></title>
</head>
<body>
<form novalidate="novalidate" action="#" id="login" method="POST" name="login">
    <input type="hidden" value="UTF-8" name="_charset_">
    <input type="hidden" value="User name and password do not match" name="errorMessage">
    <input type="hidden" value="/" name="resource">

    <p class="sign-in-title">Sign in</p>

    <label for="username"><span>User name</span></label>
    <input type="email" autocomplete="off" spellcheck="false" placeholder="User name" pattern=".*" autofocus="autofocus"
           name="j_username" id="username"><br>
    <label for="password"><span>Password</span></label>
    <input type="password" autocomplete="off" spellcheck="false" placeholder="Password" name="j_password" id="password"><br>

    <div class="alert error hidden" id="error">

    </div>
    <button class="primary" type="submit">Sign In</button>
</form>
</body>
</html>
