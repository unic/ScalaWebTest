<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>A HTML page with white spaces in element attributes</title>
</head>
<body>
    <div class="">
        <div class="first"></div>
    </div>

    <div class="         precedingSpace"></div>

    <div class="<%= '\t' %>precedingTab"></div>


    <div class=" surroundingSpace "></div>
    <div class="<%= '\t' %>surroundingTab<%= '\t' %>"></div>

    <div class="   space<%= '\t' %>separated<%= '\t' %><%= '\t' %>          "></div>

    <div class="line<%="\n\r\n"%>breaks"><div id="lineBreakChild"></div></div>
</body>
</html>
