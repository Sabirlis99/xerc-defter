package com.subhan.xercdefteri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subhan.xercdefteri.data.Category
import com.subhan.xercdefteri.data.Expense
import com.subhan.xercdefteri.ui.ExpenseViewModel
import com.subhan.xercdefteri.ui.components.DonutChart
import com.subhan.xercdefteri.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val monthNamesAz = listOf(
    "Yanvar", "Fevral", "Mart", "Aprel", "May", "İyun",
    "İyul", "Avqust", "Sentyabr", "Oktyabr", "Noyabr", "Dekabr"
)

private fun money(v: Double): String = String.format(Locale("az", "AZ"), "%,.2f", v)

@Composable
fun HomeScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.uiState.collectAsState()

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var formError by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Receipt, contentDescription = null, tint = Gold, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Xərc dəftəri", style = MaterialTheme.typography.headlineMedium, color = Ink)
        }
        Text(
            "Gündəlik xərclərini qeyd et, hara getdiyini gör",
            color = InkSoft,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 30.dp, bottom = 16.dp)
        )

        // Month nav + total card
        Column(
            Modifier
                .fillMaxWidth()
                .background(Card, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .border(1.dp, HairlineOnCard, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(20.dp, 18.dp, 20.dp, 14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.shiftMonth(-1) }) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Əvvəlki ay", tint = InkSoft)
                }
                Text(
                    "${monthNamesAz[state.viewMonth.monthValue - 1]} ${state.viewMonth.year}",
                    fontSize = 13.sp, fontWeight = FontWeight.Medium, color = InkSoft
                )
                IconButton(onClick = { viewModel.shiftMonth(1) }) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Növbəti ay", tint = InkSoft)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Cəmi xərc", fontSize = 12.sp, color = InkSoft)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    money(state.monthTotal),
                    fontFamily = MonoNumbers, fontSize = 34.sp, fontWeight = FontWeight.Medium, color = Ink
                )
                Spacer(Modifier.width(6.dp))
                Text("₼", fontSize = 18.sp, color = InkSoft)
            }
        }
        PerforatedEdge()

        // Breakdown
        if (state.breakdown.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Card)
                    .border(1.dp, HairlineOnCard)
                    .padding(20.dp, 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutChart(breakdown = state.breakdown)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.breakdown.take(4).forEach { ct ->
                        val pct = if (state.monthTotal > 0) (ct.total / state.monthTotal * 100).toInt() else 0
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(ct.category.color, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                ct.category.label, fontSize = 12.5.sp, color = InkSoft,
                                modifier = Modifier.weight(1f)
                            )
                            Text("$pct%", fontFamily = MonoNumbers, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = Ink)
                        }
                    }
                    if (state.breakdown.size > 4) {
                        Text("+${state.breakdown.size - 4} digər kateqoriya", fontSize = 11.5.sp, color = InkSoft)
                    }
                }
            }
        }

        // Add form
        Column(
            Modifier
                .fillMaxWidth()
                .background(Card)
                .border(1.dp, HairlineOnCard)
                .padding(20.dp, 20.dp, 20.dp, 8.dp)
        ) {
            Text(
                "YENİ XƏRC ƏLAVƏ ET", fontSize = 11.sp, fontWeight = FontWeight.Medium,
                color = InkSoft, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = amountText,
                    onValueChange = { amountText = it; formError = null },
                    placeholder = { Text("0.00", fontSize = 28.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = MonoNumbers, fontSize = 28.sp, fontWeight = FontWeight.Medium, color = Ink
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text("₼", fontSize = 20.sp, color = InkSoft)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.values().forEach { cat ->
                    val active = selectedCategory == cat
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(62.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) cat.color.copy(alpha = 0.10f) else Color.Transparent)
                            .border(
                                if (active) 1.5.dp else 1.dp,
                                if (active) cat.color else HairlineOnCard,
                                RoundedCornerShape(10.dp)
                            )
                            .clickableNoRipple { selectedCategory = cat }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(cat.icon, contentDescription = cat.label, tint = if (active) cat.color else InkSoft, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.height(5.dp))
                        Text(
                            cat.shortLabel, fontSize = 10.sp, color = if (active) Ink else InkSoft,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Qeyd (məsələn: market)", fontSize = 13.5.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            if (formError != null) {
                Text(formError!!, color = Rust, fontSize = 12.5.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = {
                    val value = amountText.replace(',', '.').toDoubleOrNull()
                    if (value == null || value <= 0.0) {
                        formError = "Məbləği düzgün daxil edin"
                        return@Button
                    }
                    viewModel.addExpense(value, selectedCategory, note, date)
                    amountText = ""
                    note = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Paper),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Text("Əlavə et")
            }
        }
        PerforatedEdge()

        Spacer(Modifier.height(4.dp))

        // Ledger list
        if (state.groupedByDate.isEmpty()) {
            Text(
                "Bu ay üçün hələ heç bir xərc qeyd olunmayıb.",
                color = InkSoft, fontSize = 13.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            state.groupedByDate.forEach { (localDate, items) ->
                val dayTotal = items.sumOf { it.amount }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 6.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${localDate.dayOfMonth} ${localDate.month.getDisplayName(TextStyle.FULL, Locale("az"))}",
                        fontSize = 11.5.sp, color = InkSoft
                    )
                    Text(money(dayTotal) + " ₼", fontFamily = MonoNumbers, fontSize = 11.5.sp, color = InkSoft)
                }
                items.forEach { expense ->
                    TransactionRow(expense = expense, onDelete = { viewModel.deleteExpense(expense) })
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(expense: Expense, onDelete: () -> Unit) {
    val cat = Category.fromId(expense.categoryId)
    Row(
        Modifier
            .fillMaxWidth()
            .background(Card, RoundedCornerShape(10.dp))
            .border(1.dp, HairlineOnCard, RoundedCornerShape(10.dp))
            .padding(12.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(32.dp)
                .background(cat.color.copy(alpha = 0.10f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(cat.label, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = Ink)
            if (expense.note.isNotBlank()) {
                Text(expense.note, fontSize = 12.sp, color = InkSoft, maxLines = 1)
            }
        }
        Text(
            money(expense.amount) + " ₼",
            fontFamily = MonoNumbers, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Ink
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Outlined.Delete, contentDescription = "Sil", tint = Rust, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun PerforatedEdge() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(16.dp)
            .background(Card),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(18) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(Paper, androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}
