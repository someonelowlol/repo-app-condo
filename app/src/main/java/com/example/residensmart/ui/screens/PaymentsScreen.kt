package com.example.residensmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
fun PaymentsScreen(
    viewModel: CondoViewModel,
    modifier: Modifier = Modifier
) {
    val resident by viewModel.resident.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Pendientes, 1: Pagados
    var activePaymentForPay by remember { mutableStateOf<Payment?>(null) }
    var showReceiptForPayment by remember { mutableStateOf<Payment?>(null) }

    val pendingList = payments.filter { it.status == PaymentStatus.PENDING || it.status == PaymentStatus.UNDER_REVIEW }
    val paidList = payments.filter { it.status == PaymentStatus.PAID }

    val totalPending = pendingList.sumOf { it.amount }
    val totalPaid = paidList.sumOf { it.amount }

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

        // FINANCIAL SUMMARY CARDS
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gestión de Cuotas y Finanzas",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = EditorialText
                )
                Text(
                    text = "Revisa y abona las cuotas de mantenimiento y servicios extraordinarios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "TOTAL PENDIENTE", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$%,.2f".format(totalPending),
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalPending > 0) StatusUrgent else StatusPaid
                            )
                            Text(text = "${pendingList.size} conceptos", fontSize = 11.sp, color = EditorialMuted)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "TOTAL HISTÓRICO PAGADO", style = MaterialTheme.typography.labelSmall, color = EditorialMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$%,.2f".format(totalPaid),
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandTeal
                            )
                            Text(text = "${paidList.size} recibos liquidados", fontSize = 11.sp, color = EditorialMuted)
                        }
                    }
                }
            }
        }

        // TAB SWITCHER
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardSurface,
                contentColor = BrandBlue,
                divider = { HorizontalDivider(color = EditorialBorder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, EditorialBorder)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "PENDIENTES (${pendingList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_pending_payments")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "HISTORIAL PAGADO (${paidList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_paid_payments")
                )
            }
        }

        // PAYMENTS LIST
        val currentList = if (selectedTab == 0) pendingList else paidList

        if (currentList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "¡Felicidades! No tienes pagos pendientes." else "Aún no hay registros en tu historial.",
                        fontSize = 14.sp,
                        color = EditorialMuted
                    )
                }
            }
        } else {
            items(currentList) { payment ->
                PaymentRowItem(
                    payment = payment,
                    onPayClick = { activePaymentForPay = payment },
                    onViewReceipt = { showReceiptForPayment = payment },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    // MODAL: PAY NOW
    activePaymentForPay?.let { p ->
        PaymentCheckoutDialog(
            payment = p,
            onDismiss = { activePaymentForPay = null },
            onConfirmPay = { method, proof ->
                viewModel.payPayment(p.id, method, proof)
                activePaymentForPay = null
            }
        )
    }

    // MODAL: VIEW RECEIPT
    showReceiptForPayment?.let { p ->
        AlertDialog(
            onDismissRequest = { showReceiptForPayment = null },
            title = {
                Text(
                    text = "Recibo Oficial de Pago",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = p.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = p.description, fontSize = 12.sp, color = EditorialMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = EditorialBorder)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Monto Liquidado: $%,.2f MXN".format(p.amount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTeal)
                    Text(text = "Fecha de Liquidación: ${p.paidAt ?: "Registrado"}", fontSize = 12.sp, color = EditorialMuted)
                    Text(text = "Referencia: ${p.reference}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = BrandBlue)
                    Text(text = "Método: ${p.paymentMethod?.labelEs ?: "Transferencia"}", fontSize = 12.sp, color = EditorialMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReceiptForPayment = null },
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
}

@Composable
fun PaymentRowItem(
    payment: Payment,
    onPayClick: () -> Unit,
    onViewReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, EditorialBorder, RoundedCornerShape(2.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = payment.category.labelEs,
                    containerColor = EditorialHighlight,
                    contentColor = BrandBlue
                )
                StatusBadge(
                    text = payment.status.labelEs,
                    containerColor = when (payment.status) {
                        PaymentStatus.PENDING -> StatusUrgent.copy(alpha = 0.1f)
                        PaymentStatus.UNDER_REVIEW -> StatusUnderReview.copy(alpha = 0.1f)
                        PaymentStatus.PAID -> StatusPaid.copy(alpha = 0.1f)
                    },
                    contentColor = when (payment.status) {
                        PaymentStatus.PENDING -> StatusUrgent
                        PaymentStatus.UNDER_REVIEW -> StatusUnderReview
                        PaymentStatus.PAID -> StatusPaid
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = payment.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EditorialText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = payment.description,
                fontSize = 12.sp,
                color = EditorialMuted,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = EditorialBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$%,.2f MXN".format(payment.amount),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = EditorialText
                    )
                    Text(
                        text = "Vence: ${payment.dueDate}",
                        fontSize = 11.sp,
                        color = EditorialMuted
                    )
                }

                if (payment.status == PaymentStatus.PENDING) {
                    Button(
                        onClick = onPayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("pay_btn_${payment.id}")
                    ) {
                        Text("PAGAR AHORA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
                    }
                } else {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(2.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        modifier = Modifier.testTag("receipt_btn_${payment.id}")
                    ) {
                        Text("VER RECIBO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCheckoutDialog(
    payment: Payment,
    onDismiss: () -> Unit,
    onConfirmPay: (PaymentMethod, String?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }
    var proofAttached by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pagar Cuota de Condominio",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = payment.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Total a abonar: $%,.2f MXN".format(payment.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SELECCIONA EL MÉTODO DE PAGO",
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                PaymentMethod.values().forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = method.labelEs, fontSize = 12.sp, color = EditorialText)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedMethod == PaymentMethod.BANK_TRANSFER) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EditorialHighlight)
                            .border(1.dp, EditorialBorder)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "DATOS DE TRANSFERENCIA SPEI", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = BrandBlue)
                            Text(text = "Banco: BBVA México", fontSize = 11.sp, color = EditorialText)
                            Text(text = "CLABE: 0121 8001 5555 4443 21", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text(text = "Beneficiario: Condominio Feliz AC", fontSize = 11.sp, color = EditorialText)
                            Text(text = "Referencia: ${payment.reference}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = BrandTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { proofAttached = !proofAttached },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Icon(
                            imageVector = if (proofAttached) Icons.Default.CheckCircle else Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = if (proofAttached) StatusPaid else BrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (proofAttached) "Comprobante Adjuntado (recibo.pdf)" else "Simular Adjuntar Comprobante",
                            fontSize = 11.sp,
                            color = if (proofAttached) StatusPaid else BrandBlue
                        )
                    }
                } else if (selectedMethod == PaymentMethod.CREDIT_CARD) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EditorialHighlight)
                            .border(1.dp, EditorialBorder)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Pago seguro con pasarela integrada. Se procesará el cargo inmediato a tu tarjeta vinculada.",
                            fontSize = 11.sp,
                            color = EditorialMuted
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmPay(selectedMethod, if (proofAttached) "comprobante_${payment.id}.jpg" else null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("confirm_checkout_button")
            ) {
                Text("CONFIRMAR PAGO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialBg)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text("CANCELAR", fontSize = 11.sp, color = EditorialMuted)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(4.dp)
    )
}
