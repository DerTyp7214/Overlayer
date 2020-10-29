package de.dertyp7214.overlayer.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import de.dertyp7214.overlayer.data.OverlayGroup

class OverlayViewModel : ViewModel() {

    private val overlays = MutableLiveData<Map<String, OverlayGroup>>()

    fun getOverlays(): Map<String, OverlayGroup> {
        return overlays.value ?: HashMap()
    }

    fun setOverlays(list: Map<String, OverlayGroup>) {
        overlays.value = HashMap(list)
    }

    fun observeOverlays(owner: LifecycleOwner, observer: Observer<Map<String, OverlayGroup>>) {
        overlays.observe(owner, observer)
    }
}