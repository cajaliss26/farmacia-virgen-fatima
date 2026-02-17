
document.addEventListener("DOMContentLoaded", () => {
   // const navLinks = document.getElementById("nav-links");

    const navLinks = document.getElementById("nav-links");
    if (!navLinks) return; // 👈 si no existe, salimos sin romper

    const token = localStorage.getItem("accessToken") || localStorage.getItem("token");
    //const token = localStorage.getItem("token");
    const nombre = localStorage.getItem("nombre");
    const apellido = localStorage.getItem("apellido");
    const rol = localStorage.getItem("rol");

    navLinks.innerHTML = "";

    // Si no hay token o ha expirado
    if (!token || isTokenExpirado(token)) {
        navLinks.innerHTML = `
            <li class="nav-item"><a class="nav-link text-light" href="/">Inicio</a></li>
            <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/productos">Nuestros productos</a></li>
            <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/reservas">Reservar atención</a></li>
            <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/carrito">Carrito</a></li>
            <li class="nav-item ms-2"><a href="/login" class="btn btn-outline-light btn-sm">Iniciar sesión</a></li>
            <li class="nav-item ms-2"><a href="/register" class="btn btn-danger btn-sm">Registrarse</a></li>
        `;
        localStorage.clear();
        return;
    }

    // Si está logeado, muestra su nombre completo
    navLinks.innerHTML = `
        <li class="nav-item"><a class="nav-link text-light" href="/">Inicio</a></li>
        <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/productos">Nuestros productos</a></li>
        <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/reservas">Reservar atención</a></li>
        <li class="nav-item"><a class="nav-link text-light" href="/dashboard-cliente/carrito">Carrito</a></li>
        <li class="nav-item dropdown ms-3">
            <a class="nav-link dropdown-toggle text-light d-flex align-items-center" href="#" id="userMenu" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-person-circle me-2 fs-5"></i> ${nombre} ${apellido}
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">Mi perfil</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#" id="logoutBtn">Cerrar sesión</a></li>
            </ul>
        </li>
    `;

    document.getElementById("logoutBtn").addEventListener("click", (e) => {
        e.preventDefault();
        localStorage.clear();
        alert("Sesión cerrada correctamente.");
        window.location.href = "/login";
    });
});

// Verifica si el token JWT está expirado
function isTokenExpirado(token) {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return Date.now() >= payload.exp * 1000;
    } catch (e) {
        console.warn("⚠️ Token inválido o vacío.");
        return true;
    }
}





