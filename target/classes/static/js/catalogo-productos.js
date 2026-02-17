// ====================================
// 🔹 CONFIG
// ====================================
const API_CATALOGO = "/api/catalogo/productos";
const API_CARRITO_ADD = "/api/carrito/add";

// token JWT guardado por tu login.js
//const getToken = () => localStorage.getItem("accessToken");
//const isLogged = () => !!getToken();
// ===============================
// 🔐 Helpers de autenticación
// ===============================
function getToken() {
  return localStorage.getItem("accessToken") || localStorage.getItem("token");
}

// REFORZANDO IS LOGGED
function isTokenExpirado(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return Date.now() >= payload.exp * 1000;
  } catch (e) {
    return true;
  }
}

function isLogged() {
  const t = getToken();
  if (!t) return false;

  if (isTokenExpirado(t)) {
    localStorage.removeItem("token");
    localStorage.removeItem("accessToken");
    return false;
  }
  return true;
}
/*
function isLogged() {

  return !!getToken();
}
*/

// Wrapper para fetch con auth (cuando hay token)
const apiFetch = (url, opts = {}) => {
  const token = getToken();
  const headers = { "Content-Type": "application/json", ...(opts.headers || {}) };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return fetch(url, { ...opts, headers });
};

document.addEventListener("DOMContentLoaded", () => {
  listarProductos();
  actualizarBadgeCarrito();
});

// ====================================
// 🔹 LISTAR PRODUCTOS
// ====================================
function listarProductos() {
  const contenedor = document.getElementById("productosContainer");
  contenedor.innerHTML = `<p class="text-center text-muted mt-4">Cargando productos...</p>`;

  fetch(API_CATALOGO)
    .then((res) => {
      if (!res.ok) throw new Error("Error al obtener productos");
      return res.json();
    })
    .then((productos) => {
      contenedor.innerHTML = "";

      if (!productos || productos.length === 0) {
        contenedor.innerHTML = `<p class="text-center text-muted">No hay productos disponibles.</p>`;
        return;
      }
      const logged = isLogged();
      productos.forEach((p) => {
        const col = document.createElement("div");
        col.className = "col-md-4 col-lg-3";

        const precio = Number(p.precio || 0).toFixed(2);
        const distrito = p.distrito ? ` (${p.distrito})` : "";

        col.innerHTML = `
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column">
              <h5 class="card-title fw-bold text-primary">${p.nombre}</h5>
              <p class="card-text mb-1"><strong>Categoría:</strong> ${p.categoria ?? ""}</p>
              <p class="card-text mb-1"><strong>Sucursal:</strong> ${p.sucursal ?? ""}${distrito}</p>

              <div class="mt-auto">
                <p class="card-text text-success fw-bold mb-2">S/ ${precio}</p>
                <div class="input-group mb-2">
                  <span class="input-group-text">Cant.</span>
                  <input type="number" min="1" value="1" class="form-control" id="qty-${p.id}">
                </div>
                <button class="btn btn-outline-primary w-100" data-id="${p.id}">
                  <i class="bi bi-cart-plus"></i> Agregar al carrito
                </button>
                ${!logged ? `<small class="text-muted d-block mt-1">Inicia sesión para guardar tu compra en la cuenta</small>` : ""}
              </div>
            </div>
          </div>`;

        contenedor.appendChild(col);
      });

      // listeners "Agregar"
      /*contenedor.querySelectorAll("button[data-id]").forEach((btn) => {
        btn.addEventListener("click", async (e) => {
          const id = Number(e.currentTarget.dataset.id);
          const qty = Math.max(1, Number(document.getElementById(`qty-${id}`)?.value || 1));

          // a) logueado → API backend
          /* if (isLogged()) {
            try {
              const r = await apiFetch(API_CARRITO_ADD, {
                method: "POST",
                body: JSON.stringify({ productoId: id, cantidad: qty }),
              });
              if (!r.ok) throw new Error(await r.text());
              mostrarAlerta("🧺 Producto agregado al carrito ✅", "success");
            } catch (err) {
              console.error("❌ Add carrito error:", err);
              mostrarAlerta(`No se pudo agregar al carrito: ${String(err).slice(0,120)}`, "danger");
            } finally {
              actualizarBadgeCarrito(true);
            }
          } else {
            // b) invitado → localStorage
            agregarLocalStorage(id, qty, productos.find((x) => x.id === id));
            mostrarAlerta("🧺 Producto agregado al carrito (invitado) ✅", "success");
            actualizarBadgeCarrito();
          }*/

      /*
              if (isLogged()) {
                try {
                  const userId = Number(localStorage.getItem("userId"));

                  const r = await apiFetch(API_CARRITO_ADD, {
                    method: "POST",
                    body: JSON.stringify({ usuarioId: userId, productoId: id, cantidad: qty }),
                  });

                  if (!r.ok) throw new Error(await r.text());
                  mostrarAlerta("🧺 Producto agregado al carrito ✅", "success");
                } catch (err) {
                  console.error("❌ Add carrito error:", err);
                  mostrarAlerta(`No se pudo agregar al carrito: ${String(err).slice(0,120)}`, "danger");
                } finally {
                  actualizarBadgeCarrito(true);
                }
              }

        }
        );
      });
    }); */
contenedor.querySelectorAll("button[data-id]").forEach((btn) => {
  btn.addEventListener("click", async (e) => {
    const id = Number(e.currentTarget.dataset.id);
    const qty = Math.max(1, Number(document.getElementById(`qty-${id}`)?.value || 1));

    // ✅ Si NO está logueada → localStorage
    if (!isLogged()) {
      agregarLocalStorage(id, qty, productos.find((x) => x.id === id));
      mostrarAlerta("🧺 Producto agregado al carrito (invitado) ✅", "success");
      actualizarBadgeCarrito();
      return;
    }

    // ✅ Si está logueada → backend
    try {
      const userId = Number(localStorage.getItem("userId"));

      const r = await apiFetch(API_CARRITO_ADD, {
        method: "POST",
        body: JSON.stringify({ productoId: id, cantidad: qty })
        //body: JSON.stringify({ usuarioId: userId, productoId: id, cantidad: qty }),
      });

      if (!r.ok) throw new Error(await r.text());
      mostrarAlerta("🧺 Producto agregado al carrito ✅", "success");
    } catch (err) {
      console.error("❌ Add carrito error:", err);
      mostrarAlerta(`No se pudo agregar al carrito: ${String(err).slice(0,120)}`, "danger");
    } finally {
      actualizarBadgeCarrito(true);
    }
  });
});
    })
    .catch((err) => {
      console.error("❌ Error al cargar productos:", err);
      contenedor.innerHTML = `<p class="text-danger text-center mt-4">Error al cargar los productos. Intenta más tarde.</p>`;
    });
}

// ====================================
// 🔹 Carrito invitado (localStorage)
// ====================================
function agregarLocalStorage(id, cantidad, prod) {
  const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
  const idx = carrito.findIndex((it) => it.id === id);

  if (idx >= 0) {
    carrito[idx].cantidad += cantidad;
  } else {
    carrito.push({
      id,
      nombre: prod?.nombre ?? "",
      precio: Number(prod?.precio ?? 0),
      cantidad,
    });
  }
  localStorage.setItem("carrito", JSON.stringify(carrito));
}

// 👉 Úsalo tras un login exitoso (p. ej., en login.js) para enviar el carrito invitado al backend
// mergea todos los items del localStorage al carrito real del usuario
async function fusionarCarritoInvitadoALogueado() {
  if (!isLogged()) return;
  const invitado = JSON.parse(localStorage.getItem("carrito")) || [];
  if (invitado.length === 0) return;

  for (const it of invitado) {
    try {

      const userId = Number(localStorage.getItem("userId"));
      const r = await apiFetch(API_CARRITO_ADD, {
        method: "POST",

        body: JSON.stringify({ usuarioId: userId, productoId: it.id, cantidad: it.cantidad }),
       // body: JSON.stringify({ productoId: it.id, cantidad: it.cantidad }),
      });
      if (!r.ok) console.warn("No se pudo fusionar item:", await r.text());
    } catch (e) {
      console.warn("Error fusionando item:", e);
    }
  }
  localStorage.removeItem("carrito");
  actualizarBadgeCarrito(true);
}

// ====================================
// 🔹 UI: alertas y badge carrito
// ====================================
function mostrarAlerta(mensaje, tipo) {
  const cont = document.getElementById("alertContainer");
  cont.innerHTML = `
    <div class="alert alert-${tipo} alert-dismissible fade show text-center" role="alert">
      ${mensaje}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>`;
  setTimeout(() => {
    const alerta = document.querySelector(".alert");
    if (!alerta) return;
    const inst = bootstrap.Alert.getOrCreateInstance(alerta);
    inst.close();
  }, 2500);
}

/**
 * Si tienes un badge en el navbar (p.ej. junto a “Ver Carrito”), ponle id="cartBadge"
 * - Invitado: cuenta items de localStorage
 * - Logueado: si tienes un endpoint rápido que traiga conteo, puedes consultarlo; si no, deja el conteo “desconocido” o muestra un punto.
 */
async function actualizarBadgeCarrito(forzarBackend = false) {
  const badge = document.getElementById("cartBadge");
  if (!badge) return;

  if (!isLogged()) {
    const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    const total = carrito.reduce((acc, it) => acc + (it.cantidad || 0), 0);
    badge.textContent = total > 99 ? "99+" : String(total);
    badge.classList.toggle("d-none", total === 0);
    return;
  }

  // Logueado:
  // Opción A: si agregas un endpoint /api/carrito que devuelve el carrito, úsalo y cuenta items:
  if (forzarBackend) {
    try {
      const r = await apiFetch("/api/carrito");
      if (r.ok) {
        const data = await r.json();
        const total = (data.items || []).reduce((acc, it) => acc + (it.cantidad || 0), 0);
        badge.textContent = total > 99 ? "99+" : String(total);
        badge.classList.toggle("d-none", total === 0);
        return;
      }
    } catch (_) {}
  }

  // Opción B (simple): mostrar un punto indicador
  badge.textContent = "•";
  badge.classList.remove("d-none");
}

// Exporta la función de fusión por si quieres llamarla desde login.js
window.fusionarCarritoInvitadoALogueado = fusionarCarritoInvitadoALogueado;
