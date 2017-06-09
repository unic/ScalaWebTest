$(document).ready(function () {
    var sidebar = $("#sidebar");
    var navigationEntries = sidebar.find("header a, li a").toArray().reduce(function (map, element) {
        var href = $(element).attr("href");
        map[href] = $(element).parent();
        return map;
    }, {});

    var currentlyHighlightedHeading = $("h2, h3, h4")[0];
    $(navigationEntries["#" + currentlyHighlightedHeading.id]).addClass('highlight');


    $(window).scroll(function () {

        var gttBtn = $("#gttBtn");

        //make navigation stick to the top
        if ($(window).scrollTop() > 350) {
            sidebar.addClass('sidebar-fixed');
        }
        else if ($(window).scrollTop() < 345) {
            sidebar.removeClass('sidebar-fixed');
            gttBtn.removeClass('gttBtn');
            sidebar.addClass('pre-sidebar');
        }

        //highlight navigation
        var topMostViewable;
        var bottomMostAboveView;

        $("h2, h3, h4").each(function (index, heading) {
            if (isAboveView(heading)) {
                bottomMostAboveView = heading;
            }
            if (isScrolledIntoView(heading) && !topMostViewable) {
                topMostViewable = heading;
            }
        });

        var headingToHighlight = topMostViewable ? topMostViewable : bottomMostAboveView;
        if (headingToHighlight && currentlyHighlightedHeading !== headingToHighlight) {
            if (navigationEntries["#" + currentlyHighlightedHeading.id]) {
                removeClass('highlight');
            }
            else if (navigationEntries["#" + headingToHighlight.id]) {
                addClass('highlight');

            }

        }

        function isScrolledIntoView(elem) {
            var docViewTop = $(window).scrollTop();
            var docViewBottom = docViewTop + $(window).height();

            var elemTop = $(elem).offset().top;
            var elemBottom = elemTop + $(elem).height();

            return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
        }

        function isAboveView(elem) {
            var docViewTop = $(window).scrollTop();
            var elemTop = $(elem).offset().top;

            return ((elemTop <= docViewTop));
        }
    });

    //user nav
    $('.child').hide();
    $('.parent').click(function () {
        $(this).siblings('.parent').find('ul').slideUp();
        $(this).find('ul').slideToggle();
    });
});