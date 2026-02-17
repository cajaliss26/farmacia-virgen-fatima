document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");

    if (!loginForm) return;

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!email || !password) {
            alert("Por favor, completa todos los campos.");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/public/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                throw new Error("Credenciales inválidas. Intenta nuevamente.");
            }

            const data = await response.json();
            console.log("✅ Login exitoso:", data);

            // Guardar datos en localStorage
            //localStorage.setItem("token", data.accessToken);
            //localStorage.removeItem("token"); junto al debajo se pusieron después
            //localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("token", data.accessToken);
            console.log("✅ accessToken guardado:", localStorage.getItem("accessToken"));
            localStorage.setItem("nombre", data.nombre);
            localStorage.setItem("apellido", data.apellido);
            localStorage.setItem("rol", data.rol);


            // ⚙️ Guardar el ID del usuario (nuevo)
            if (data.id) {
                localStorage.setItem("userId", data.id);
            } else if (data.usuario && data.usuario.id) {
                localStorage.setItem("userId", data.usuario.id);
            }

            // Mostrar bienvenida
            alert(`Bienvenido ${data.nombre} ${data.apellido}`);

            // Redirección según rol
            if (data.rol === "ADMIN") {
                window.location.href = "/dashboard-admin";
            } else if (data.rol === "CLIENTE") {
                window.location.href = "/dashboard-cliente/productos";
            }

        } catch (error) {
            console.error("❌ Error en login:", error);
            alert(error.message || "Error de conexión con el servidor.");
        }
    });
});
