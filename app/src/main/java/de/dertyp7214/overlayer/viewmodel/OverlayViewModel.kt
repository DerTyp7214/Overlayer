package de.dertyp7214.overlayer.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.data.OverlayQueueItem

class OverlayViewModel : ViewModel() {

    private val overlays = MutableLiveData<Map<String, OverlayGroup>>()
    private val overlayQueue = MutableLiveData<HashMap<String, OverlayQueueItem>>()

    fun getOverlays(): Map<String, OverlayGroup> {
        return overlays.value ?: HashMap()
    }

    fun setOverlays(list: Map<String, OverlayGroup>) {
        overlays.value = HashMap(list)
    }

    fun observeOverlays(owner: LifecycleOwner, observer: Observer<Map<String, OverlayGroup>>) {
        overlays.observe(owner, observer)
    }

    fun getOverlayQueue(): HashMap<String, OverlayQueueItem> {
        return HashMap(overlayQueue.value ?: HashMap())
    }

    fun addOverlayToQueue(overlayQueueItem: OverlayQueueItem) {
        val queue = getOverlayQueue()
        if (!queue.containsKey(overlayQueueItem.packageName) || overlayQueueItem.refresh) queue[overlayQueueItem.packageName] =
            overlayQueueItem
        else queue.remove(overlayQueueItem.packageName)
        overlayQueue.value = queue
    }

    fun clearOverlays() {
        overlayQueue.value?.apply {
            this.iterator().forEach {
                it.value.clear()
            }
        }
        overlayQueue.value = HashMap()
    }

    fun observeOverlayQueue(
        owner: LifecycleOwner,
        observer: Observer<Map<String, OverlayQueueItem>>
    ) {
        overlayQueue.observe(owner, observer)
    }
}