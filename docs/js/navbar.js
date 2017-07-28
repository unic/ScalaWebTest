(function ($, app) {

    /**
     * TODO documentation
     */
    app.DocContentView = function (selector) {

        // Private variables and functions
        var $docContentView = $(selector);

        var h2Elements = $docContentView.find('h2');
        var highlightingTriggeringTitles = $docContentView.find('h2, h3');

        function getH2Elements() {
            return h2Elements;
        }

        function getHighlightingTriggeringTitles() {
            return highlightingTriggeringTitles;
        }

        var smoothScroll = $(" ul, li, a").click(function () {
            $('html, body').animate({
                scrollTop: $($.attr(this, 'href')).offset().top
            }, 1000);
            return false;
        });


        function init() {

        }

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

        var headingToNavigationEntryMap = $sidebar.find("header a, li a").toArray().reduce(function (map, element) {
            var href = $(element).attr("href");
            map[href] = $(element).parent();
            return map;
        }, {});

        var foldingTriggeringTitles = docContentView.getH2Elements();
        var highlightingTriggeringTitles = docContentView.getHighlightingTriggeringTitles();

        var currentlyHightlightedNavEntry = headingToNavigationEntry(highlightingTriggeringTitles[0].id);
        var currentlyUnfoldedNavEntry = headingToTopLevelNavigationEntry(foldingTriggeringTitles[0].id);

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

        function highlight(navElement) {
            navElement.addClass('highlight');
        }

        function unhighlight(navElement) {
            navElement.removeClass('highlight');
        }

        function findNavTopLevel(navElement) {
            if (!navElement || navElement.hasClass("sidebarHeadings")) {
                return navElement;
            } else {
                return navElement.parents(".sidebarHeadings");
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

        function headingToNavigationEntry(id) {
            return headingToNavigationEntryMap["#" + id];
        }

        function headingToTopLevelNavigationEntry(id) {
            var headingToTopLevelNavigationEntryMap =
                $sidebar.find("li a").toArray().reduce(function (map, element) {
                    var href = $(element).attr("href");
                    map[href] = $(element).parent().siblings("header");
                    return map;
                }, new $.extend({}, headingToNavigationEntryMap));

            return headingToTopLevelNavigationEntryMap["#" + id];
        }


        function adjustPosition() {
            //make navigation stick to the top
            if ($(window).scrollTop() > 350) {
                $sidebar.addClass('sidebar-fixed');
            }
            else if ($(window).scrollTop() < 345) {
                $sidebar.removeClass('sidebar-fixed');
                gttBtn.removeClass('gttBtn');
                $sidebar.addClass('pre-sidebar');
            }
        }

        function sidebarScroll() {
            var topMostViewable;
            var bottomMostAboveView;

            adjustPosition();


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
            var navEntryToHighlight = headingToNavigationEntry(headingToHighlight.id);

            if (navEntryToHighlight && !currentlyHightlightedNavEntry.is(navEntryToHighlight)) {
                var navEntryToUnfold = headingToTopLevelNavigationEntry(headingToHighlight.id);

                //(un)fold navigation
                if (navEntryToUnfold && !currentlyUnfoldedNavEntry.is(navEntryToUnfold)) {
                    fold(currentlyUnfoldedNavEntry);
                    unfold(navEntryToUnfold);
                    currentlyUnfoldedNavEntry = navEntryToUnfold;
                }

                highlight(navEntryToHighlight);
                unhighlight(currentlyHightlightedNavEntry);

                currentlyHightlightedNavEntry = navEntryToHighlight;
            }
        }



        var gttBtn = $("#gttBtn");

        function init() {
            adjustPosition();
            skel.onStateChange(
                function(){
                    if (skel.isActive("mobile")) {
                        activateMobileView();
                    } else {
                        activateDesktopView();
                    }
                });
        }

        function activateDesktopView() {
            childLiElements.hide();

            highlight(currentlyHightlightedNavEntry);
            unfold(currentlyUnfoldedNavEntry);
            $(window).on('scroll', sidebarScroll);
        }

        function activateMobileView() {
            unhighlight(currentlyHightlightedNavEntry);

            $('.sidebarHeadings').find('li').show();
            $(window).off('scroll', sidebarScroll);
        }

        // Public API
        return {
            init: init
        };

    };


})(jQuery, window.ScalaWebTest || (window.ScalaWebTest = {}));