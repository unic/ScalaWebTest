<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<!--
	Linear by TEMPLATED
    templated.co @templatedco
    Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<head>
    <title>ScalaWebTest - integration testing made easy</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="initial-scale=1, maximum-scale=1" />
    <meta name="viewport" content="width=device-width" />
    <meta name="description" content="ScalaWebTest is a library for writing ScalaTest/Selenium based integration tests for websites. It helps you with your basic setup and provides a new and very efficient approach to testing." />

    <meta name="keywords" content="Scala,ScalaTest,Selenium,integration testing,web development,web testing,e2e testing,end-to-end testing,browser testing,ui testing" />

    <!-- Twitter Card data -->
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:title" content="Integration testing made easy" />
    <meta name="twitter:description" content="ScalaWebTest is a library for writing ScalaTest/Selenium based integration tests for websites. It helps you with your basic setup and provides a new and very efficient approach to testing." />

    <meta name="twitter:creator" content="@scalawebtest" />


    <!-- Iconography -->
    <link rel="shortcut icon" href="/images/favicons/favicon.ico">
    <link rel="apple-touch-icon" sizes="180x180" href="/images/favicons/apple-touch-icon.png">
    <link rel="icon" type="image/png" href="/images/favicons/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="/images/favicons/favicon-16x16.png" sizes="16x16">
    <link rel="manifest" href="/images/favicons/manifest.json">
    <link rel="mask-icon" href="/images/favicons/safari-pinned-tab.svg" color="#c22d40">
    <meta name="msapplication-config" content="/images/favicons/browserconfig.xml">
    <meta name="theme-color" content="#c22d40">

    <!-- Twitter Summary card images must be at least 200x200px -->
    <meta name="twitter:image" content="/images/summarycard.png" />

    <!-- Open Graph data -->
    <meta property="og:title" content="Integration testing made easy" />
    <meta property="og:type" content="article" />
    <meta property="og:url" content="/" />
    <meta property="og:image" content="/images/summarycard.png" />
    <meta property="og:description" content="ScalaWebTest is a library for writing ScalaTest/Selenium based integration tests for websites. It helps you with your basic setup and provides a new and very efficient approach to testing." />
    <meta property="og:site_name" content="scalawebtest.org" />

    <link href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,700,500,900' rel='stylesheet' type='text/css'>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="/js/gttBtn.js"></script>
    <script src="/js/navbar.js"></script>
    <script src="/js/skel.min.js"></script>
    <script src="/js/skel-panels.min.js"></script>
    <script src="/js/init.js"></script>
    <script src="/js/twitter.js" async></script>
    <link rel="stylesheet" href="/css/highlight.css" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <noscript>
        <link rel="stylesheet" href="/css/skel-noscript.css" />
        <link rel="stylesheet" href="/css/style.css" />
        <link rel="stylesheet" href="/css/style-desktop.css" />
    </noscript>
</head>

<body class="homepage">

<!-- Header -->
<div id="header">
    <div id="nav-wrapper">
        <!-- Nav -->
        <nav id="nav">
            <ul>
                <li class="home"><a href="/index.html">ScalaWebTest </a></li>

                <li><a href="/documentation.html" title="Documentation">Documentation</a></li>

                <li><a href="/download.html" title="Download">Download</a></li>

                <li><a href="/community.html" title="Community">Community</a></li>

            </ul>
        </nav>
    </div>
    <div class="container">

        <!-- Logo -->
        <div id="logo">
            <h1><a>Integration testing made easy</a></h1>
            <span class="tag">write <strong>Selenium</strong> and <strong>ScalaTest</strong> based integration tests in minutes</span>
        </div>
    </div>
</div>
<!-- Header -->


<!-- Featured -->
<div id="featured">
    <div class="container">

        <header>
            <h2>Reduce the effort needed to write integration tests</h2>
        </header>
        <p>
            ScalaWebTests helps you to set up integration tests in no time and provides useful functions in order to
            reduce the effort needed to write integration tests (end-to-end tests, browser tests or UI tests, we don't care how you call them) for web applications.
        </p>
        <hr/>
        <div class="row">
            <section class="4u">
                <span class="pennant"><span class="fa fa-fast-forward"></span></span>
                <h3>Ready in minutes</h3>
                <p>Add one dependency to your project and start writing your integration tests. We manage the transitive
                    dependencies for you and provide the functions every web project needs for testing</p>
                <a href="/documentation.html#gettingStarted" class="button button-style1">Get started</a>
            </section>
            <section class="4u">
                <span class="pennant"><span class="fa fa-heart-o"></span></span>
                <h3>Simple Gauges</h3>
                <p>Gauges or templates are used in manufacturing to quickly verify a workpiece. If it fits the gauge, it
                    fulfills the requirements.
                    An essential part of this concept is, to only verify what's needed and with the needed
                    precision.</p>
                <a href="/documentation.html#writingGauges" class="button button-style1">Read More</a>
            </section>
            <section class="4u">
                <span class="pennant"><span class="fa fa-copy"></span></span>
                <h3>Great examples</h3>
                <p>Imitation is a key process when learning new concepts. ScalaWebTest's own integration tests are there
                    to provide a great example on how to structure and build your integration tests.</p>
                <a href="https://github.com/unic/ScalaWebTest/tree/master/scalawebtest-integration/src/it/scala/org/scalawebtest/integration"
                   class="button button-style1">Learn More</a>
            </section>
        </div>
    </div>
</div>

<div id="fromtheblog">
    <div class="container">
        <header>
            <h2>Release announcements</h2>
        </header>
        <p>
            <a href="/2019/06/14/scalawebtest-release-3.0.0.html" class="button">Latest version: ScalaWebTest
                3.0.0</a>
        </p>
        <div class="row">

            <section class="4u">
                <h3>ScalaWebTest 3.0.0</h3>
                <p>is compatible with a Selenium WebDrivers. It can be configured with environment variables, which makes it Docker friendly.</p>
                <a href="/2019/06/14/scalawebtest-release-3.0.0.html" class="button">Read more</a>
            </section>

            <section class="4u">
                <h3>ScalaWebTest 2.0.1</h3>
                <p>fixes a bug in the JsonGauge</p>
                <a href="/2017/12/01/scalawebtest-release-2.0.1.html" class="button">Read more</a>
            </section>

            <section class="4u">
                <h3>ScalaWebTest 2.0.0</h3>
                <p>introduces semantic versioning</p>
                <a href="/2017/10/31/scalawebtest-release-2.0.0.html" class="button">Read more</a>
            </section>

        </div>
    </div>
</div>

<!-- Tweet -->
<div id="tweet">
    <div class="container">
        <section>
            <blockquote></blockquote>
        </section>
    </div>
</div>

<!-- Footer -->
<div id="footer">
    <div class="container">
        <section>
            <header>
                <h2>Get in touch</h2>
            </header>
            <p>
                E-Mail: <script>document.write('<a href="mailto:scalawebtest' + '@unic.com">scalawebtest' + "@unic.com</a>");</script>
            </p>
            <p>
                Unic AG<br>
                Belpstrasse 48, 3007 Bern, Switzerland<br>
                Telephone: +41 31 560 12 12<br>
                Fax: +41 31 560 12 13
            </p>

            <ul class="contact">
                <li><a href="https://twitter.com/scalawebtest" class="fa fa-twitter"><span>Twitter</span></a></li>
                <li><a href="https://github.com/unic/ScalaWebTest" class="fa fa-github"><span>Github</span></a></li>
            </ul>
        </section>
    </div>
</div>

<!-- Copyright -->
<div id="copyright">
    <div class="container">
        Design: <a href="http://templated.co">TEMPLATED</a>
    </div>
</div>
</body>
</html>
