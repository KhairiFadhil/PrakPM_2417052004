package model

import androidx.annotation.DrawableRes

data class ServiceItem(
    val label: String,
    @DrawableRes val iconRes: Int,
)
