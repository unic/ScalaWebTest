(function() {
    // Global for JSONP tweet callback
    window.loadTweets = window.loadTweets || [];

    var PATTERN_TWITTER_TIMESTAMP = /([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]+)\+[0-9]+/;

    function parseTime(pattern, value) {
        pattern.exec(value);
        // Date(year, month [, day [, hours[, minutes[, seconds[, ms]]]]])
        return new Date(RegExp.$1, RegExp.$2 - 1, RegExp.$3, RegExp.$4, RegExp.$5, RegExp.$6).getTime();
    }

    // Provider for activities in the stream
    var Provider = function (config) {
        this.config = config;
        this.handle = config.handle;
    };

    // Tweets. Since there is no authentication-free tweet search API and we've got
    // no server-side component, we'll use a twitter widget as the data source.
    // this means we have to parse the widget HTML to retrieve data.
    var TweetsForWidget = function (widgetId) {
        var result = [];
        var knownTweets = [];

        loadTweets.push(function (data) {
            $(data.body).find("li.timeline-TweetList-tweet").each(function (_, elem) {
                var tweet = {
                    type: "tweet"
                };
                var $elem = $(elem);
                var tweetPermalink;
                $elem.find("a.timeline-Tweet-timestamp").each(function (_, elem) {
                    tweetPermalink = elem.href;
                });

                if (knownTweets[tweetPermalink]) {
                    return;
                }

                knownTweets[tweetPermalink] = true;
                tweet.url = tweetPermalink;

                $elem.find("time.dt-updated").each(function (_, elem) {
                    tweet.time = parseTime(PATTERN_TWITTER_TIMESTAMP, elem.getAttribute("datetime"));
                });
                $elem.find(".timeline-Tweet-text").each(function (_, elem) {
                    tweet.message = elem.innerHTML;
                });

                result.push(tweet);
            });

            return result;
        });

        var provider = new Provider({
            //old style calls
            //source: "https://cdn.syndication.twimg.com/widgets/timelines/" + widgetId + "?lang=en&t=1560138&callback=loadTweets[" + (loadTweets.length - 1) + "]&suppress_response_codes=true"
            //source: "https://cdn.syndication.twimg.com/timeline/profile?callback=__twttr.callbacks.tl_i0_profile_scalawebtest_old&dnt=false&domain=localhost%3A63342&lang=en&screen_name=scalawebtest&suppress_response_codes=true&t=1641760&with_replies=false"
            //stripped parameters
            //&domain=localhost%3A63342
           //&t=1641760
            source: "https://cdn.syndication.twimg.com/timeline/profile?callback=__twttr.callbacks.tl_i0_profile_scalawebtest_old&dnt=false&lang=en&screen_name=scalawebtest&suppress_response_codes=true&with_replies=false"
        });

        provider.load = function (callback) {
            $.ajax({
                url: provider.config.source,
                dataType: "script",
                success: function () {
                    callback(result);
                },
                error: function (script) {
                    throw new Error("Could not load script " + script);
                }
            });
        };
        return  provider;
    };

    TweetsForWidget("f8bf188a26c0fb191f8cdd1eb88ad3c7").load(function(tweets) {
        if (!tweets.length) return;
        var tweet = tweets[0];
        $("#tweet").find("blockquote").html('<a href="' + tweet.url + '"><img src="/images/tweet.png" />' + new Date(tweet.time).toDateString() + '</a>: &ldquo;' + tweet.message + '&rdquo;')
    });
})();