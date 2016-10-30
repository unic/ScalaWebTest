<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
</head>
<body>
<div>
    <p class="fitting-element-order">
        text-before-link
        <a href="#">link</a>
        text-after-link
    </p>

    <p class="not-fitting-element-order">
        text-before-link
        <a href="#">link</a>
    </p>

    <p class="not-fitting-element-order">
        <a href="#">link</a>
        text-after-link
    </p>

    <p class="not-fitting-element-order missing-elements">
        text
    </p>

    <p class="not-fitting-element-order missing-elements">
        <a href="#">link</a>
    </p>
</div>
</body>
</html>
