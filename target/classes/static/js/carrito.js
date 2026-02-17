// ===============================
// Carrito (cliente)
// ===============================

const API = {
  carrito: "/api/carrito",
  add: "/api/carrito/add",
  qty: "/api/carrito/qty",
  remove: (itemId) => `/api/carrito/item/${itemId}`,
  vaciar: "/api/carrito/vaciar",
  checkout: "/api/carrito/checkout",
};

// 🔑 token guardado por login.js
const getToken = () => localStorage.getItem("token");

// Elementos UI
const tbody = document.getElementById("carritoBody");
const totalEl = document.getElementById("totalGeneral");
const btnVaciar = document.getElementById("btnVaciar");
const btnCheckout = document.getElementById("btnCheckout");

// ===============================
// Init
// ===============================
document.addEventListener("DOMContentLoaded", init);

async function init() {
  btnVaciar?.addEventListener("click", onVaciar);
  btnCheckout?.addEventListener("click", onCheckout);

  const token = getToken();

  if (token) {
    // Fusiona carrito invitado al iniciar sesión
    await fusionarInvitadoAlBackend();

    // Carga desde backend (con fallback a local si viene vacío)
    const ok = await cargarDesdeApi();
    if (!ok) cargarDesdeLocal();
  } else {
    cargarDesdeLocal();
  }
}

// ===============================
// Render
// ===============================
function render(items) {
  tbody.innerHTML = "";
  let total = 0;

  if (!items || items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Tu carrito está vacío</td></tr>`;
    totalEl.textContent = "0.00";
    return;
  }

  items.forEach((it) => {
    // Normalización de campos (API vs localStorage)
    const id =
      it.itemId ??
      it.id ??
      it.productoId ??
      (typeof it.producto === "object" ? it.producto.id : undefined);

    const nombre =
        it.nombreProducto ??
        it.productoNombre ??
      it.nombre ??
      (typeof it.producto === "object" ? it.producto.nombre : "Producto");

    const precio = Number(
      it.precio ??
      it.precioUnitario ??
      it.precio_unit ??
      (typeof it.producto === "object" ? it.producto.precio : 0)
    );

    const cantidad = Number(it.cantidad ?? it.qty ?? 1);
    const sub = precio * cantidad;
    total += sub;

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${nombre}</td>
      <td class="text-end">${precio.toFixed(2)}</td>
      <td class="text-center">
        <input type="number" min="1" value="${cantidad}"
               class="form-control form-control-sm w-auto d-inline"
               data-id="${id}" />
      </td>
      <td class="text-end">${sub.toFixed(2)}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger" data-remove="${id}">
          Eliminar
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });

  totalEl.textContent = total.toFixed(2);

  // Handlers por fila
  tbody.querySelectorAll("input[type=number]").forEach((inp) => {
    inp.addEventListener("change", onChangeQty);
  });
  tbody.querySelectorAll("button[data-remove]").forEach((btn) => {
    btn.addEventListener("click", onRemove);
  });
}

// ===============================
// Backend
// ===============================
async function cargarDesdeApi() {
  const token = getToken();
  if (!token) return false;

  try {
    const res = await fetch(API.carrito, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("GET /api/carrito falló");

    const data = await res.json();
    const items = data.items || data.detalles || (Array.isArray(data) ? data : []);
    render(items);
    return items.length > 0;
  } catch (e) {
    console.error("cargarDesdeApi:", e);
    return false;
  }
}

async function onChangeQty(e) {
  const token = getToken();
  const id = Number(e.target.dataset.id);
  const cantidad = Math.max(1, Number(e.target.value || 1));

  if (token) {
    try {
      await fetch(API.qty, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ itemId: id, cantidad }),
      });
      await cargarDesdeApi();
    } catch (_) {}
  } else {
    const cart = JSON.parse(localStorage.getItem("carrito") || "[]");
    const it = cart.find((x) => Number(x.id) === id);
    if (it) it.cantidad = cantidad;
    localStorage.setItem("carrito", JSON.stringify(cart));
    cargarDesdeLocal();
  }
}

async function onRemove(e) {
  const token = getToken();
  const id = Number(e.currentTarget.dataset.remove);

  if (token) {
    try {
      await fetch(API.remove(id), {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      await cargarDesdeApi();
    } catch (_) {}
  } else {
    const cart = (JSON.parse(localStorage.getItem("carrito") || "[]") || []).filter(
      (x) => Number(x.id) !== id
    );
    localStorage.setItem("carrito", JSON.stringify(cart));
    cargarDesdeLocal();
  }
}

async function onVaciar() {
  const token = getToken();
  if (token) {
    try {
      await fetch(API.vaciar, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      await cargarDesdeApi();
    } catch (_) {}
  } else {
    localStorage.removeItem("carrito");
    cargarDesdeLocal();
  }
}

async function onCheckout() {
  const token = getToken();
  if (!token) {
    alert("Inicia sesión para completar tu compra.");
    window.location.href = "/login";
    return;
  }
  try {
    const res = await fetch(API.checkout, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Checkout falló");

    // Espera JSON { pedidoId: number }
    const data = await res.json();
    const pedidoId = data?.pedidoId || data?.id;

    if (pedidoId) {
      window.location.href = `/boleta/${pedidoId}`;
      return;
    }

    // Fallback: intenta leer Location
    const loc = res.headers.get("Location");
    if (loc) {
      const m = loc.match(/\/boleta\/(\d+)/);
      if (m) {
        window.location.href = `/boleta/${m[1]}`;
        return;
      }
    }

    await cargarDesdeApi();
    alert("Pago realizado.");
  } catch (e) {
    console.error("checkout:", e);
    alert("No se pudo completar el pago.");
  }
}

// ===============================
// LocalStorage (invitado)
// ===============================
function cargarDesdeLocal() {
  const cart = JSON.parse(localStorage.getItem("carrito") || "[]");
  const items = cart.map((c) => ({
    id: c.id,
    nombre: c.nombre,
    precio: Number(c.precio || 0),
    cantidad: Number(c.cantidad || 1),
  }));
  render(items);
}

/**
 * Fusiona los items del carrito invitado al carrito del usuario logueado.
 */
async function fusionarInvitadoAlBackend() {
  const token = getToken();
  if (!token) return;

  const invitado = JSON.parse(localStorage.getItem("carrito") || "[]");
  if (!Array.isArray(invitado) || invitado.length === 0) return;

  for (const it of invitado) {
    try {
      await fetch(API.add, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ productoId: it.id, cantidad: it.cantidad || 1 }),
      });
    } catch (e) {
      console.warn("No se pudo fusionar item:", e);
    }
  }
  localStorage.removeItem("carrito");
}
