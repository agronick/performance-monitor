package com.aatorque.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aatorque.datastore.Coloring

abstract class AwareObserver<T>: Observer<T?> {
    var attachedTo: MutableLiveData<T?>? = null
    fun bind(viewLifecycleOwner: LifecycleOwner, binder: MutableLiveData<T?>) {
        unbind()
        binder.observe(viewLifecycleOwner, this)
        attachedTo = binder
    }

    fun unbind() {
        attachedTo?.removeObserver(this)
    }
}