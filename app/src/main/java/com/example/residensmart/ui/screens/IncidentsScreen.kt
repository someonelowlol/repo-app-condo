package com.example.residensmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.residensmart.data.model.*
import com.example.residensmart.ui.components.CondoTopBar
import com.example.residensmart.ui.components.StatusBadge
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

@Composable
fun IncidentsScreen(
    viewModel: CondoViewModel,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val incidents by viewModel.incidents.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf<IncidentStatus?>(null) }
    var showCreateModal by remember { mutableStateOf(false) }
    var selectedIncidentForDetail by remember { mutableStateOf<Incident?>(null) }

    val filteredList = incidents.filter {
        selectedStatusFilter == null || it.status == selectedStatusFilter
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = BrandBlue,
                contentColor = EditorialBg,
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("report_incident_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REPORTAR FALLA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        },
        containerColor = EditorialBg,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                CondoTopBar(
                    resident = resident,
                    currentRole = currentRole,
                    onRoleToggle = { viewModel.setRole(it) }
                )
            }

            // HEADER
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tickets e Incidentes",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = EditorialText
                    )
                    Text(
                        text = "Reporta desperfectos y da seguimiento en tiempo real a las reparaciones técnicas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // STATUS FILTERS
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedStatusFilter == null,
                                onClick = { selectedStatusFilter = null },
                                label = { Text("TODOS (${incidents.size})", fontSize = 11.sp) },
                                shape = RoundedCornerShape(2.dp)
                            )
                        }
                        items(IncidentStatus.values()) { status ->
                            val count = incidents.count { it.status == status }
                            FilterChip(
                                selected = selectedStatusFilter == status,
                                onClick = { selectedStatusFilter = status },
                                label = { Text("${status.labelEs.uppercase()} ($count)", fontSize = 11.sp) },
                                shape = RoundedCornerShape(2.dp)
                            )
                        }
                    }
                }
            }

            // LIST OF TICKETS
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron tickets con el filtro seleccionado.",
                            fontSize = 14.sp,
                            color = EditorialMuted
                        )
                    }
                }
            } else {
                items(filteredList) { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = { selectedIncidentForDetail = incident },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // MODAL: REPORT NEW INCIDENT
    if (showCreateModal) {
        CreateIncidentDialog(
            onDismiss = { showCreateModal = false },
            onConfirm = { title, cat, priority, loc, desc ->
                viewModel.reportIncident(title, cat, priority, loc, desc, null)
                showCreateModal = false
            }
        )
    }

    // MODAL: INCIDENT DETAIL & CHAT
    selectedIncidentForDetail?.let { inc ->
        // Retrieve fresh state from ViewModel
        val currentIncident = incidents.find { it.id == inc.id } ?: inc
        IncidentDetailDialog(
            incident = currentIncident,
            resident = resident,
            isAdmin = currentRole == UserRole.ADMIN,
            onDismiss = { selectedIncidentForDetail = null },
            onAddComment = { content ->
                val author = if (currentRole == UserRole.ADMIN) "Administrador" else resident.name
                val roleStr = if (currentRole == UserRole.ADMIN) "admin" else "resident"
                viewModel.addIncidentComment(inc.id, content, author, roleStr)
            }
        )
    }
}

@Composable
fun IncidentCard(
    incident: Incident,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(
                        text = incident.category.labelEs,
                        containerColor = EditorialHighlight,
                        contentColor = BrandBlue
                    )
                    val priorityColor = when (incident.priority) {
                        IncidentPriority.HIGH -> StatusUrgent
                        IncidentPriority.MEDIUM -> StatusPending
                        IncidentPriority.LOW -> BrandTeal
                    }
                    StatusBadge(
                        text = "PRIORIDAD ${incident.priority.labelEs}",
                        containerColor = priorityColor.copy(alpha = 0.1f),
                        contentColor = priorityColor
                    )
                }

                StatusBadge(
                    text = incident.status.labelEs,
                    containerColor = when (incident.status) {
                        IncidentStatus.REPORTED -> StatusPending.copy(alpha = 0.12f)
                        IncidentStatus.ASSIGNED -> StatusUnderReview.copy(alpha = 0.12f)
                        IncidentStatus.IN_PROGRESS -> BrandBlue.copy(alpha = 0.12f)
                        IncidentStatus.RESOLVED -> StatusPaid.copy(alpha = 0.12f)
                    },
                    contentColor = when (incident.status) {
                        IncidentStatus.REPORTED -> StatusPending
                        IncidentStatus.ASSIGNED -> StatusUnderReview
                        IncidentStatus.IN_PROGRESS -> BrandBlue
                        IncidentStatus.RESOLVED -> StatusPaid
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = incident.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EditorialText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = incident.description,
                fontSize = 12.sp,
                color = EditorialMuted,
                maxLines = 2,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ubicación: ${incident.location}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandBlue
            )

            if (incident.technicianName != null) {
                Text(
                    text = "Asignado a: ${incident.technicianName}",
                    fontSize = 11.sp,
                    color = BrandTeal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = EditorialBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reportado: ${incident.createdAt}",
                    fontSize = 11.sp,
                    color = EditorialMuted
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = BrandBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${incident.comments.size} mensajes",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )
                }
            }
        }
    }
}

@Composable
fun CreateIncidentDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: IncidentCategory, priority: IncidentPriority, location: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(IncidentCategory.PLUMBING) }
    var selectedPriority by remember { mutableStateOf(IncidentPriority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reportar Nuevo Incidente",
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
                    label = { Text("Título de la falla (ej. Fuga en lavabo)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_input_title")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación exacta (ej. Baño principal Apto 402)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_input_location")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "CATEGORÍA", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(IncidentCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.labelEs, fontSize = 10.sp) },
                            shape = RoundedCornerShape(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "URGENCIA / PRIORIDAD", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IncidentPriority.values().forEach { prio ->
                        FilterChip(
                            selected = selectedPriority == prio,
                            onClick = { selectedPriority = prio },
                            label = { Text(prio.labelEs, fontSize = 10.sp) },
                            shape = RoundedCornerShape(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción detallada del problema") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("incident_input_description")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onConfirm(title, selectedCategory, selectedPriority, location.ifBlank { "Unidad Privativa" }, description)
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("submit_incident_button")
            ) {
                Text("REGISTRAR REPORTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
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

@Composable
fun IncidentDetailDialog(
    incident: Incident,
    resident: Resident,
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = incident.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = BrandBlue
                )
                Text(
                    text = "Estado: ${incident.status.labelEs} • ${incident.location}",
                    fontSize = 12.sp,
                    color = EditorialMuted
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                Text(
                    text = incident.description,
                    fontSize = 13.sp,
                    color = EditorialText,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = EditorialBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SEGUIMIENTO Y COMENTARIOS (${incident.comments.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(incident.comments) { c ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (c.authorRole == "admin") BrandBlue.copy(alpha = 0.08f) else if (c.authorRole == "technician") BrandTeal.copy(alpha = 0.08f) else EditorialHighlight)
                                .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = c.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (c.authorRole == "admin") BrandBlue else if (c.authorRole == "technician") BrandTeal else EditorialText
                                    )
                                    Text(text = c.createdAt.takeLast(5), fontSize = 10.sp, color = EditorialMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = c.content, fontSize = 12.sp, color = EditorialText)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Escribe un mensaje...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("incident_comment_input")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText)
                                newCommentText = ""
                            }
                        },
                        enabled = newCommentText.isNotBlank(),
                        modifier = Modifier.testTag("incident_send_comment_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = BrandBlue)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text("Cerrar", color = EditorialBg)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(4.dp)
    )
}
