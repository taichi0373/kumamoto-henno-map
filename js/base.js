// ウィンドウのリサイズイベントを監視
window.addEventListener('resize', function () {
    const formSelectElements = document.querySelectorAll('.form-select');
    if (window.innerWidth < 360) {
        formSelectElements.forEach(function (element) {
            element.classList.add('form-select-sm');
        });
    } else {
        formSelectElements.forEach(function (element) {
            element.classList.remove('form-select-sm');
        });
    }
});
window.dispatchEvent(new Event('resize'));

document.addEventListener('DOMContentLoaded', (event) => {
    event.preventDefault();
    var height = window.innerHeight;
    document.documentElement.style.setProperty('--vh', height / 100 + 'px');

    const currentUrl = window.location.pathname; // 現在のURLのパスを取得
    const links = document.querySelectorAll(".nav-link");
    links.forEach(link => {
        if (link.getAttribute("href") === currentUrl) {
            link.classList.add("active");
        }
    });


});
