package com.example.residensmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.residensmart.data.model.*
import com.example.residensmart.ui.components.CondoTopBar
import com.example.residensmart.ui.components.SectionHeader
import com.example.residensmart.ui.components.StatusBadge
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

@Composable
fun AdminScreen(
    viewModel: CondoViewModel,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val incidents by viewModel.incidents.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showBroadcastDialog by remember { mutableStateOf(false) }

    val pendingBookings = bookings.filter { it.status == BookingStatus.PENDING }
    val activeIncidents = incidents.filter { it.status != IncidentStatus.RESOLVED }

    val totalRecaudado = payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
    val totalPorCobrar = payments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EditorialBg),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            CondoTopBar(
                resident = resident,
                currentRole = currentRole,
                onRoleToggle = { viewModel.setRole(it) }
            )
        }

        // ADMIN OVERVIEW BANNER
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Panel de Administración",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = BrandBlue
                        )
                        Text(
                            text = "Gestión central de condominio, finanzas, amenidades y personal técnico.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EditorialMuted
                        )
                    }

                    Button(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("admin_new_announcement_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EMITIR AVISO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // STAT METRICS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatBox(
                        title = "TOTAL RECAUDADO",
                        value = "$%,.0f".format(totalRecaudado),
                        subtitle = "Cuotas cobradas",
                        color = StatusPaid,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatBox(
                        title = "POR COBRAR",
                        value = "$%,.0f".format(totalPorCobrar),
                        subtitle = "Cartera vencida/pendiente",
                        color = StatusUrgent,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatBox(
                        title = "INCIDENTES",
                        value = "${activeIncidents.size}",
                        subtitle = "Tickets activos",
                        color = BrandBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // SECTION: BOOKINGS AWAITING APPROVAL
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                SectionHeader(
                    title = "Aprobación de Reservaciones (${pendingBookings.size})",
                    subtitle = "Solicitudes que requieren validación de depósito y aforo"
                )

                if (pendingBookings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSurface)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay solicitudes de reserva pendientes por autorizar.",
                            fontSize = 13.sp,
                            color = EditorialMuted
                        )
                    }
                }
            }
        }

        items(pendingBookings) { booking ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = booking.amenityName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EditorialText
                        )
                        StatusBadge(
                            text = "POR AUTORIZAR",
                            containerColor = StatusPending.copy(alpha = 0.12f),
                            contentColor = StatusPending
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fecha: ${booking.date} • ${booking.timeSlot} • ${booking.guestCount} invitados",
                        fontSize = 12.sp,
                        color = EditorialMuted
                    )
                    Text(
                        text = "Arancel: $%,.2f MXN • Solicitante: Residente ${booking.residentId}".format(booking.totalCost),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandTeal
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.updateBookingStatus(booking.id, BookingStatus.CANCELLED) },
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("admin_reject_booking_${booking.id}")
                        ) {
                            Text("RECHAZAR", fontSize = 11.sp, color = StatusUrgent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.updateBookingStatus(booking.id, BookingStatus.CONFIRMED) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("admin_approve_booking_${booking.id}")
                        ) {
                            Text("APROBAR Y GENERAR QR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                        }
                    }
                }
            }
        }

        // SECTION: MAINTENANCE TICKETS ASSIGNMENT
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(
                    title = "Triaje de Mantenimiento e Incidentes",
                    subtitle = "Asigna técnicos certificados y actualiza el estado operativo"
                )
            }
        }

        items(activeIncidents) { incident ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = incident.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EditorialText
                        )
                        StatusBadge(
                            text = incident.status.labelEs,
                            containerColor = EditorialHighlight,
                            contentColor = BrandBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ubicación: ${incident.location} • Prioridad: ${incident.priority.labelEs}",
                        fontSize = 12.sp,
                        color = EditorialMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "ACCIONES DE GESTIÓN:", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateIncidentStatus(incident.id, IncidentStatus.ASSIGNED, "Ing. Carlos Gutiérrez")
                            },
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ASIGNAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.updateIncidentStatus(incident.id, IncidentStatus.IN_PROGRESS, null)
                            },
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("EN PROGRESO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusPending)
                        }
                        Button(
                            onClick = {
                                viewModel.updateIncidentStatus(incident.id, IncidentStatus.RESOLVED, null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusPaid),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("RESOLVER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                        }
                    }
                }
            }
        }
    }

    // MODAL: BROADCAST CIRCULAR
    if (showBroadcastDialog) {
        CreateBroadcastDialog(
            onDismiss = { showBroadcastDialog = false },
            onPublish = { title, content, cat ->
                viewModel.publishAnnouncement(title, content, cat, "Administrador General")
                showBroadcastDialog = false
            }
        )
    }
}

@Composable
fun AdminStatBox(
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = EditorialMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontFamily = FontFamily.Serif, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = subtitle, fontSize = 9.sp, color = EditorialMuted)
        }
    }
}

@Composable
fun CreateBroadcastDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, content: String, category: AnnouncementCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AnnouncementCategory.INFO) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Publicar Aviso Oficial a Residentes",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la circular") },
                    modifier = Modifier.fillMaxWidth().testTag("admin_announcement_title")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "TIPO DE COMUNICADO", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AnnouncementCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.labelEs, fontSize = 10.sp) },
                            shape = RoundedCornerShape(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido del comunicado") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("admin_announcement_content")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPublish(title, content, selectedCategory)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("admin_submit_announcement_btn")
            ) {
                Text("PUBLICAR EN EL MURO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(2.dp)) {
                Text("CANCELAR", fontSize = 11.sp, color = EditorialMuted)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(4.dp)
    )
}
