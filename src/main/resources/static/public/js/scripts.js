function resizeBannerIframe() {
    const iframe = document.getElementById('banner-frame');
    if (iframe && iframe.contentWindow.document) {
        const newHeight = iframe.contentWindow.document.documentElement.scrollHeight;
        iframe.style.height = newHeight + 'px';
    }
}
document.getElementById('banner-frame').onload = resizeBannerIframe;
window.addEventListener('resize', resizeBannerIframe);

function resizeNavbarIframe() {
    const iframe = document.getElementById('navbar-frame');
    if (iframe && iframe.contentWindow.document) {
        const newHeight = iframe.contentWindow.document.documentElement.scrollHeight;
        iframe.style.height = newHeight + 'px';
    }
}
document.getElementById('navbar-frame').onload = resizeNavbarIframe;
window.addEventListener('resize', resizeNavbarIframe);

function resizeFooterIframe() {
    const iframe = document.getElementById('footer-frame');
    if (iframe && iframe.contentWindow.document) {
        const newHeight = iframe.contentWindow.document.documentElement.scrollHeight;
        iframe.style.height = newHeight + 'px';
    }
}
document.getElementById('footer-frame').onload = resizeFooterIframe;
window.addEventListener('resize', resizeFooterIframe);


 //Принудительное обновление iframe с помощью JavaScript
 //Добавьте этот код, чтобы обновлять iframe при загрузке страницы: 
 
document.addEventListener("DOMContentLoaded", function () {
    var iframe = document.getElementById("banner-frame");
    iframe.src = iframe.src; // Перезагружает iframe
});

document.addEventListener("DOMContentLoaded", function () {
    var iframe = document.getElementById("navbar-frame");
    iframe.src = iframe.src; // Перезагружает iframe
});