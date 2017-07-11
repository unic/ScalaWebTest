(function ($, app) {

    /**
     * TODO documentation
     */
    app.DocContentView = function (selector) {

        // Private variables and functions
        var $docContentView = $(selector);

        var h2Elements = $docContentView.find('h2');
        var highlightingTriggeringTitles = $docContentView.find('h2, h3');

        var getH2Elements = function () {
            return h2Elements;
        };

        var getHighlightingTriggeringTitles = function () {
            return highlightingTriggeringTitles;
        };

        var smoothScroll = $(" ul, li, a").click(function () {
                $('html, body').animate({
                    scrollTop: $($.attr(this, 'href')).offset().top
                }, 1000);
                return false;
        });

        var init = function () {
            // TODO
        };

        // Public API
        return {
            init: init,
            getH2Elements: getH2Elements,
            getHighlightingTriggeringTitles: getHighlightingTriggeringTitles
        };

    };

    /**
     * TODO documentation
     */
    app.Sidebar = function (selector, docContentView) {

        // Private variables and functions
        var $sidebar = $(selector);

        var sidebarHeadings = $sidebar.find('ul.sidebarHeadings');
        var childLiElements = $sidebar.find('li.child');

        var hideAllLiChildElements = function () {
            childLiElements.hide();
        };

        var foldToggle = function (navElement) {
            var navTopLevelElement = findNavTopLevel(navElement);
            if (navTopLevelElement) {
                navTopLevelElement.find('li').slideToggle();
            }
        };

        var fold = function (navElement) {
            var navTopLevelElement = findNavTopLevel(navElement);
            if (navTopLevelElement) {
                navTopLevelElement.find('li').slideUp();
            }
        };

        var unfold = function (navElement) {
            var navTopLevelElement = findNavTopLevel(navElement);
            if (navTopLevelElement) {
                navTopLevelElement.find('li').slideDown();
            }
        };

        var highlight = function (navElement) {
            navElement.addClass('highlight');
        };

        var unhighlight = function (navElement) {
            navElement.removeClass('highlight');
        };

        var findNavTopLevel = function (navElement) {
            if (!navElement || navElement.hasClass("sidebarHeadings")) {
                return navElement;
            } else {
                return navElement.parents(".sidebarHeadings");
            }
        };

        var isScrolledIntoView = function (elem) {
            var docViewTop = $(window).scrollTop();
            var docViewBottom = docViewTop + $(window).height();

            var elemTop = $(elem).offset().top;
            var elemBottom = elemTop + $(elem).height();

            return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
        };

        var headingToNavigationEntryMap = $sidebar.find("header a, li a").toArray().reduce(function (map, element) {
            var href = $(element).attr("href");
            map[href] = $(element).parent();
            return map;
        }, {});

        var isAboveView = function isAboveView(elem) {
            var docViewTop = $(window).scrollTop();
            var elemTop = $(elem).offset().top;

            return ((elemTop <= docViewTop));
        };

        var headingToNavigationEntry = function (id) {
            return headingToNavigationEntryMap["#" + id];
        };

        var headingToTopLevelNavigationEntryMap =
            $sidebar.find("li a").toArray().reduce(function (map, element) {
                var href = $(element).attr("href");
                map[href] = $(element).parent().siblings("header");
                return map;
            }, new $.extend({}, headingToNavigationEntryMap));

        var headingToTopLevelNavigationEntry = function (id) {
            return headingToTopLevelNavigationEntryMap["#" + id];
        };


        var adjustPosition = function () {
            //make navigation stick to the top
            if ($(window).scrollTop() > 350) {
                $sidebar.addClass('sidebar-fixed');
            }
            else if ($(window).scrollTop() < 345) {
                $sidebar.removeClass('sidebar-fixed');
                gttBtn.removeClass('gttBtn');
                $sidebar.addClass('pre-sidebar');
            }
        };

        var gttBtn = $("#gttBtn");

        var init = function () {
            hideAllLiChildElements();

            sidebarHeadings.click(function () {
                foldToggle(this);
            });
        };

        // Public API
        return {
            init: init,
            foldToggle: foldToggle,
            fold: fold,
            unfold: unfold,
            findNavTopLevel: findNavTopLevel,
            headingToNavigationEntry: headingToNavigationEntry,
            headingToTopLevelNavigationEntry: headingToTopLevelNavigationEntry,
            adjustPosition: adjustPosition,
            highlight: highlight,
            unhighlight: unhighlight,
            isScrolledIntoView: isScrolledIntoView,
            isAboveView: isAboveView
        };

    };


})(jQuery, window.ScalaWebTest || (window.ScalaWebTest = {}));


// Startup application
$(document).ready(function () {

    var docContentView = new ScalaWebTest.DocContentView("#content");

    var sidebar = new ScalaWebTest.Sidebar("#sidebar", docContentView);
    sidebar.init();

    var foldingTriggeringTitles = docContentView.getH2Elements();
    var highlightingTriggeringTitles = docContentView.getHighlightingTriggeringTitles();

    var currentlyHightlightedNavEntry = sidebar.headingToNavigationEntry(highlightingTriggeringTitles[0].id);
    sidebar.highlight(currentlyHightlightedNavEntry);

    var currentlyUnfoldedNavEntry = sidebar.headingToTopLevelNavigationEntry(foldingTriggeringTitles[0].id);
    sidebar.foldToggle(currentlyUnfoldedNavEntry);

    $(window).scroll(function () {


        sidebar.adjustPosition();

        var topMostViewable;
        var bottomMostAboveView;

        //highlight navigation
        highlightingTriggeringTitles.each(function (index, heading) {
            if (sidebar.isAboveView(heading)) {
                bottomMostAboveView = heading;
            }
            if (sidebar.isScrolledIntoView(heading) && !topMostViewable) {
                topMostViewable = heading;
            }
        });

        var headingToHighlight = topMostViewable ? topMostViewable : bottomMostAboveView;
        var navEntryToHighlight = sidebar.headingToNavigationEntry(headingToHighlight.id);

        if (navEntryToHighlight && !currentlyHightlightedNavEntry.is(navEntryToHighlight)) {
            var navEntryToUnfold = sidebar.headingToTopLevelNavigationEntry(headingToHighlight.id);
            //(un)fold navigation
            if (navEntryToUnfold && !currentlyUnfoldedNavEntry.is(navEntryToUnfold)) {
                sidebar.fold(currentlyUnfoldedNavEntry);
                sidebar.unfold(navEntryToUnfold);
                currentlyUnfoldedNavEntry = navEntryToUnfold;
            }

            sidebar.highlight(navEntryToHighlight);
            sidebar.unhighlight(currentlyHightlightedNavEntry);

            currentlyHightlightedNavEntry = navEntryToHighlight;
        }

    });
})
;