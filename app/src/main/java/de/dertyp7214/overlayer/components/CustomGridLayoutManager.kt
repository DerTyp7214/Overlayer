package de.dertyp7214.overlayer.components

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class CustomGridLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }
}