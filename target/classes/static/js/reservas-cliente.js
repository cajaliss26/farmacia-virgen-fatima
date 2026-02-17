// ==============================
// Config
// ==============================
const API_SUCURSALES = "/api/sucursales";
const API_RESERVAS = "/api/reservas";

// 🔑 lo que guarda login.js: accessToken, id, nombre, apellido
const accessToken = localStorage.getItem("token");
const userId = localStorage.getItem("id");          // opcional para mostrar
const nombre = localStorage.getItem("nombre") || "";
const apellido = localStorage.getItem("apellido") || "";

const authHeaders = () => ({
  "Content-Type": "application/json",
  "Authorization": `Bearer ${accessToken}`
});

// ==============================
// Carga inicial
// ==============================
document.addEventListener("DOMContentLoaded", () => {
  // Debe estar logueado
  if (!accessToken) {
    alert("⚠️ Debes iniciar sesión para acceder a esta página.");
    window.location.href = "/login";
    return;
  }

  const lblCliente = document.getElementById("clienteNombre");
  if (lblCliente) lblCliente.textContent = `${nombre} ${apellido}`.trim();

  cargarSucursales();

  const form = document.getElementById("formReserva");
  //const inputPersonas = document.getElementById("numPersonas");
  //const inputPersonas = document.getElementById("fechaReserva")
  //inputPersonas?.addEventListener("input");
  form?.addEventListener("submit", registrarReserva);
});

// ==============================
// Sucursales
// ==============================
function cargarSucursales() {
  const select = document.getElementById("sucursalSelect");
  if (!select) return;

  fetch(API_SUCURSALES, { headers: authHeaders() })
      .then(res => res.ok ? res.json() : Promise.reject(res))
      .then(data => {
        select.innerHTML = `<option value="">Seleccione una sucursal</option>`;
        data.forEach(s => {
          select.innerHTML += `<option value="${s.id}">${s.nombre} - ${s.distrito ?? ""}</option>`;
        });
      })
      .catch(() => {
        select.innerHTML = `<option value="">Error al cargar sucursales</option>`;
      });
}

// ==============================
// Total (solo UI, el backend recalcula)
// ==============================
//function calcularTotal() {
//  const personas = parseInt(document.getElementById("numPersonas").value) || 0;
//  const total = personas * 4; // S/ 4 por persona (informativo)
//  document.getElementById("total").value = total.toFixed(2);
//}

// ==============================
// Registrar reserva
// ==============================
function registrarReserva(e) {
  e.preventDefault();

  const sucursalId = document.getElementById("sucursalSelect").value;
  //const numeroPersonas = document.getElementById("numPersonas").value;
  const fechaReserva = document.getElementById("fechaReserva").value; // yyyy-MM-dd
  const horaInicio = document.getElementById("horaReserva").value;    // HH:mm (inicio)
  const horaFin = document.getElementById("horaFin")?.value;          // HH:mm (fin)

  if (!sucursalId  || !fechaReserva || !horaInicio || !horaFin) {
    mostrarAlerta("Por favor complete todos los campos.", "danger");
    return;
  }

  // Validar que la hora de fin sea posterior a la hora de inicio
  if (horaFin <= horaInicio) {
    mostrarAlerta("La hora de fin debe ser mayor que la hora de inicio.", "warning");
    return;
  }

  // ⚠️ Enviar SOLO lo que el backend espera (ReservaRequest):
  // { sucursalId, fechaReserva, horaInicio, horaFin, numeroPersonas }
  fetch(API_RESERVAS, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({
      sucursalId: parseInt(sucursalId),
      fechaReserva,              // LocalDate (ISO)
      horaInicio,                // LocalTime (HH:mm)
      horaFin                  // LocalTime (HH:mm)
      //numeroPersonas: parseInt(numeroPersonas)
    })
  })
      .then(res => res.ok ? res.json() : res.text().then(t => Promise.reject(t)))
      .then(() => {
        mostrarAlerta("✅ Reserva registrada exitosamente.", "success");
        document.getElementById("formReserva").reset();
        //document.getElementById("total").value = "";
      })
      .catch(err => {
        console.error("Error reserva:", err);
        mostrarAlerta("❌ Error al registrar la reserva. Intenta de nuevo.", "danger");
      });
}

// ==============================
// Alertas
// ==============================
function mostrarAlerta(mensaje, tipo) {
  const alertContainer = document.getElementById("alertContainer");
  if (!alertContainer) return;
  alertContainer.innerHTML = `
    <div class="alert alert-${tipo} text-center fw-semibold fade show" role="alert">
      ${mensaje}
    </div>`;
  setTimeout(() => (alertContainer.innerHTML = ""), 3000);
}
