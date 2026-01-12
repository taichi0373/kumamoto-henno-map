document.addEventListener('DOMContentLoaded', (event) => {
    event.preventDefault();
    const btn_back = document.getElementById("btn-back");
    btn_back.addEventListener("click", function () {
        window.location.href = "index.php";
    });
});