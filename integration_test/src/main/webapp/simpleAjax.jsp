<%@ page contentType="text/html;charset=UTF-8"%>
<html>

<head>
    <title>Ajax Test</title>
    <script type="text/javascript"
            src="/jquery_2.1.3.min.js"></script>

    <script type="text/javascript" language="javascript">
        $(document).ready(function () {
            //load textSnippet content with a delay of 0.5 seconds
            setTimeout(function() {
                $('#container').load('/textSnippet.txt');
            }, 500);
        });
    </script>
</head>

<body>


<div id="container">
    static text
</div>


</body>

</html>
