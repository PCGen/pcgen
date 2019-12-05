/*
 * navtree.js
 * Converts an unordered list to an outline menu
 *
 * Based on:
 * Stuart Langridge's AQTree <http://www.kryogenix.org/code/browser/aqlists/>,
 * Aaron's labels.js <http://youngpup.net/demos/labels/>, and
 * Dave Lindquist's menuDropDown.js <http://www.gazingus.org/dhtml/?id=109)>
 */

addEvent(window, "load", makeTrees);

var currentMenus = [];

function hideChildren(nd, toplevel) {
    // iterate nd's children, and hide all <ul>s we find
    if (nd.childNodes && nd.childNodes.length > 0) {
        for (var ndi = 0; ndi < nd.childNodes.length; ndi++) {
            hideChildren(nd.childNodes[ndi], toplevel);
        }
    }
    if (nd.nodeName.toLowerCase() == "ul" && nd != toplevel) {
        nd.style.display = "none";
    }
}

function makeTrees() {
    // we don't actually need createElement,
    // but we do need good DOM support
    if (!document.createElement) {
        return;
    }

    uls = document.getElementsByTagName("ul");
    for (uli = 0; uli < uls.length; uli++) {
        ul = uls[uli];
        if (ul.nodeName.toLowerCase() == "ul" && ul.className.toLowerCase() == "navtree") {
            processULEL(ul);
        }
    }
}

function processULEL(ul) {
    if (!ul.childNodes || ul.childNodes.length == 0) {
        return;
    }

    // iterate <li>s
    for (var itemi = 0; itemi < ul.childNodes.length; itemi++) {
        var item = ul.childNodes[itemi];
        if (item.nodeName.toLowerCase() == "li" && item.className.toLowerCase() != "gap") {
            // iterate this <li>
            var a;
            var subul;
            subul = "";
            for (var sitemi = 0; sitemi < item.childNodes.length; sitemi++) {
                var sitem = item.childNodes[sitemi];
                switch (sitem.nodeName.toLowerCase()) {
                    case "a":
                        a = sitem;
                        break;
                    case "ul":
                        subul = sitem;
                        processULEL(subul);
                        break;
                }
            }
            if (subul) {
                associateEL(a, subul);
            } else {
                a.parentNode.style.listStyleImage = "url(./navtree/bullet.png)";
            }
        }
    }
}

function associateEL(a, ul) {
    a.onclick = function () {
        var display = ul.style.display;
        this.parentNode.style.listStyleImage = (display == "block") ? "url(./navtree/plus.png)" : "url(./navtree/minus.png)";
        ul.style.display = (display == "block") ? "none" : "block";
        return true;
    }
    a.onmouseover = function () {
        var display = ul.style.display;
        window.status = (display == "block") ? "Collapse" : "Expand";
        return true;
    }
    a.onmouseout = function () {
        window.status = "";
        return true;
    }
}

/* adds an eventListener for browsers which support it
 * written by Scott Andrew
 */
function addEvent(obj, evType, fn) {
    if (obj.addEventListener) {
        obj.addEventListener(evType, fn, true);
        return true;
    } else if (obj.attachEvent) {
        var r = obj.attachEvent("on" + evType, fn);
        return r;
    } else {
        return false;
    }
}
