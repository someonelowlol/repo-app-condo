# ResidentSmart - Android Application (Kotlin & Jetpack Compose)

Aplicación móvil integral para administración de condominios residenciales, desarrollada en **Kotlin** con **Jetpack Compose** y **Material Design 3**.

---

## 🏢 Características Principales

1. **Panel Principal (Home):**
   - Resumen del estado de cuenta financiero (saldo pendiente vs. al corriente).
   - Acciones rápidas (Pagos, Reservar, Incidentes, Directorio).
   - Tablón de circulares y comunicados oficiales con estados de lectura y badges de categoría (*Mantenimiento, Urgente, Evento, Informativo*).
   - Vista previa de próximas reservas con generación de pases digitales QR.
   - Seguimiento a tickets de soporte técnico activos.

2. **Gestión de Cuotas y Pagos:**
   - Desglose de cuotas pendientes e historial de pagos liquidados.
   - Información bancaria oficial para transferencias SPEI (CLABE, Banco, Beneficiario, Referencia única).
   - Soporte para adjuntar comprobantes de pago y aprobación administrativa.
   - Generación de recibos digitales de pago.

3. **Reserva de Áreas Comunes (Amenidades):**
   - Catálogo de espacios: Alberca de Terraza, Sala de Juntas/Coworking, Salón de Eventos Sociales, Asadores de Jardín, Cancha de Pádel.
   - Información detallada: capacidad máxima, tarifa por hora, fotos y reglamento.
   - Selección de fecha, franjas horarias y cálculo de costos en tiempo real.
   - Pases de acceso con código QR simulado para control en caseta o torniquete.

4. **Tickets e Incidentes:**
   - Reporte de averías con categoría (*Plomería, Electricidad, Elevadores, Seguridad, Área Común, Otros*), nivel de prioridad (*Baja, Media, Alta*) y ubicación exacta.
   - Canal de comentarios y chat bidireccional entre el residente, la administración y los técnicos asignados.
   - Filtrado por estado (*Reportado, Asignado, En Progreso, Resuelto*).

5. **Directorio y Asistencia de Emergencia:**
   - Contactos clave: Administrador General, Caseta de Vigilancia 24/7, Recepción y Paquetería.
   - Botón directo para emergencias médicas y bomberos (911).
   - Consulta rápida de normas esenciales del condominio (silencio, mascotas, mudanzas).

6. **Consola de Administración:**
   - Conmutador de rol directo (*Residente / Administrador*).
   - Aprobación y rechazo de reservaciones con depósito en garantía.
   - Triaje y asignación de técnicos a reportes de mantenimiento.
   - Emisión y difusión de circulares al muro del condominio.

---

## 🛠️ Arquitectura y Tecnologías

- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Patrón:** MVVM (Model-View-ViewModel) con `StateFlow`
- **Carga de Imágenes:** Coil Compose
- **Iconografía:** Material Icons Extended
- **Paleta Editorial:**
  - Azul Corporativo: `#0D305F`
  - Acento Verde Azulado: `#0D9488`
  - Fondo Editorial Cálido: `#FDFCFB`
  - Bordes y Líneas: `#E5E1DA`
  - Tipografía Serif para títulos y Sans para legibilidad de datos.
