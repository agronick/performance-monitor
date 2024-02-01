package com.aatorque.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class AwareObserver<T>: Observer<T?> {
    var attachedTo: MutableLiveData<T?>? = null
    fun bind(viewLifecycleOwner: LifecycleOwner, binder: MutableLiveData<T?>) {
        unbind()
        binder.observe(viewLifecycleOwner, this)
        binder.postValue(binder.value)
        attachedTo = binder
    }

    fun unbind() {
        attachedTo?.removeObserver(this)
    }
}