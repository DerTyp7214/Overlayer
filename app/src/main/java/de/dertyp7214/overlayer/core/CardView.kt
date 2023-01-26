package de.dertyp7214.overlayer.core

import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun MaterialCardView.corners(
    topLeftRadius: Float = 5.dp(this.context),
    topRightRadius: Float = 5.dp(this.context),
    bottomRightRadius: Float = 5.dp(this.context),
    bottomLeftRadius: Float = 5.dp(this.context),
) {
    val shapePathModel = ShapeAppearanceModel().toBuilder()
        .setTopLeftCorner(CornerFamily.ROUNDED, topLeftRadius)
        .setTopRightCorner(CornerFamily.ROUNDED, topRightRadius)
        .setBottomRightCorner(CornerFamily.ROUNDED, bottomRightRadius)
        .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeftRadius)

    val background = MaterialShapeDrawable(shapePathModel.build())
    background.fillColor = this.cardBackgroundColor
    background.elevation = this.cardElevation

    this.background = background
    invalidate()
}