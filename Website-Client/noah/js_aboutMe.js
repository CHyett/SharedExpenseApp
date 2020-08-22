//sticky navbar functionality
document.addEventListener('DOMContentLoaded', function () {
    // When the user scrolls the page, execute fn
    window.onscroll = function () { fn() }

    // Get the navbar
    const navbar = document.getElementById("navbar")


    // Get the offset position of the navbar
    const sticky = navbar.offsetTop

    // Add the sticky class to the navbar when you reach its scroll position. Remove "sticky" when you leave the scroll position
    function fn() {
        if (window.pageYOffset >= sticky) {
            navbar.classList.add("sticky")
        } else {
            navbar.classList.remove("sticky")
        }
    }
})


//Animations on scroll functionality
document.addEventListener('DOMContentLoaded', function() {
    const scroll = window.requestAnimationFrame || function (callback) { window.setTimeout(callback, (1000 / 60)) }
    const onScrollElements = document.querySelectorAll(".photograph-background.show-on-scroll")
    function loop() {
        onScrollElements.forEach(element => {
            if (isElementInViewport(element))
                element.classList.add("is-visible")
            else
                element.classList.remove("is-visible")
        })
        scroll(loop)
    }
    loop()
    //Helper function to check if an element is in the viewport
    function isElementInViewport(el) {
        // jQuery check
        if (typeof jQuery === "function" && el instanceof jQuery) {
            el = el[0]
        }
        var rect = el.getBoundingClientRect()
        return (
            (rect.top <= 0
                && rect.bottom >= 0)
            ||
            (rect.bottom >= (window.innerHeight || document.documentElement.clientHeight) &&
                rect.top <= (window.innerHeight || document.documentElement.clientHeight))
            ||
            (rect.top >= 0 &&
                rect.bottom <= (window.innerHeight || document.documentElement.clientHeight))
        )
    }
})


//Active navbar functionality
//Get all anchors who have a parent with id 'wrapping'
let navLinks = document.querySelectorAll(".wrapping > a")
//console.log(navLinks)
let mainSections = document.querySelectorAll("main section")

/*document.addEventListener("scroll", function() {
    console.log(window.scrollY)
})*/

function myAlert() {
    alert("This part isn't implemented you dumb muddapucka!!!!!!!")
}