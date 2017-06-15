$(document).ready(function () {
    var sidebar = $("#sidebar");

    var foldingTriggeringTitles = $("h2");
    var highlightingTriggeringTitles = $("h2, h3");

    //user nav
    var child = $('.child');
    var parent = $('.parent');

    child.hide();
    parent.click(function () {
        foldToggle(this);
    });

    var navigationEntries = sidebar.find("header a, li a").toArray().reduce(function (map, element) {
        var href = $(element).attr("href");
        map[href] = $(element).parent();
        return map;
    }, {});

    var currentlyHighlightedHeading = highlightingTriggeringTitles[0];
    navigationEntries["#" + currentlyHighlightedHeading.id].addClass('highlight');

    var currentlyUnfoldedHeading = foldingTriggeringTitles[0];
    foldToggle(navigationEntries["#" + currentlyUnfoldedHeading.id]);


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

        var topMostViewable;
        var bottomMostAboveView;
        //(un)fold navigation
        foldingTriggeringTitles.each(function (index, heading) {
            if (isAboveView(heading)) {
                bottomMostAboveView = heading;
            }
            if (isScrolledIntoView(heading) && !topMostViewable) {
                topMostViewable = heading;
            }
        });


        var headingToUnfold = topMostViewable ? topMostViewable : bottomMostAboveView;
        if (headingToUnfold && currentlyUnfoldedHeading !== headingToUnfold) {
            fold(navigationEntries["#" + currentlyUnfoldedHeading.id]);
            unfold(navigationEntries["#" + headingToUnfold.id]);
            currentlyUnfoldedHeading = headingToUnfold;
        }

        //highlight navigation
        highlightingTriggeringTitles.each(function (index, heading) {
            if (isAboveView(heading)) {
                bottomMostAboveView = heading;
            }
            if (isScrolledIntoView(heading) && !topMostViewable) {
                topMostViewable = heading;
            }
        });


        var headingToHighlight = topMostViewable ? topMostViewable : bottomMostAboveView;
        if (headingToHighlight && currentlyHighlightedHeading !== headingToHighlight) {
            navigationEntries["#" + currentlyHighlightedHeading.id].removeClass('highlight');
            navigationEntries["#" + headingToHighlight.id].addClass('highlight');
            currentlyHighlightedHeading = headingToHighlight;
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

    function foldToggle(navElement) {
        var navTopLevelElement = findNavTopLevel(navElement);
        if (navTopLevelElement) {
            navTopLevelElement.find('li').slideToggle();
        }
    }

    function fold(navElement) {
        var navTopLevelElement = findNavTopLevel(navElement);
        if (navTopLevelElement) {
            navTopLevelElement.find('li').slideUp();
        }
    }

    function unfold(navElement) {
        var navTopLevelElement = findNavTopLevel(navElement);
        if (navTopLevelElement) {
            navTopLevelElement.find('li').slideDown();
        }
    }

    function findNavTopLevel(navElement) {
        if (!navElement || navElement.hasClass("parent")) {
            return navElement;
        } else {
            return navElement.parents(".parent");
        }
    }

    //smooth scroll
    $(" ul, li, a").click(function () {
        $('html, body').animate({
            scrollTop: $($.attr(this, 'href')).offset().top
        }, 1000);
        return false;
    });

});