package com.subhan.xercdefteri.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val id: String,
    val label: String,
    val shortLabel: String,
    val icon: ImageVector,
    val color: Color
) {
    FOOD("food", "Ərzaq və qida", "Ərzaq", Icons.Outlined.Restaurant, Color(0xFFB8933E)),
    TRANSPORT("transport", "Nəqliyyat", "Nəqliyyat", Icons.Outlined.DirectionsCar, Color(0xFF5C7A5C)),
    HOUSING("housing", "Kirayə / Mənzil", "Kirayə", Icons.Outlined.Home, Color(0xFF7A4B3A)),
    UTILITIES("utilities", "Kommunal", "Kommunal", Icons.Outlined.Bolt, Color(0xFFB5652C)),
    HEALTH("health", "Sağlamlıq", "Sağlamlıq", Icons.Outlined.MonitorHeart, Color(0xFFA83B32)),
    ENTERTAINMENT("entertainment", "Əyləncə", "Əyləncə", Icons.Outlined.Movie, Color(0xFF6B5B95)),
    SHOPPING("shopping", "Geyim / Alış-veriş", "Alış-veriş", Icons.Outlined.ShoppingBag, Color(0xFF3F7A8C)),
    EDUCATION("education", "Təhsil", "Təhsil", Icons.Outlined.MenuBook, Color(0xFF4A6B4A)),
    COMMUNICATION("communication", "Rabitə / İnternet", "Rabitə", Icons.Outlined.Phone, Color(0xFF8B7355)),
    OTHER("other", "Digər", "Digər", Icons.Outlined.MoreHoriz, Color(0xFF8B8578));

    companion object {
        fun fromId(id: String): Category = values().firstOrNull { it.id == id } ?: OTHER
    }
}
