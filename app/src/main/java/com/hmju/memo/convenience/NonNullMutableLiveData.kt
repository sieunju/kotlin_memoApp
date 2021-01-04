package com.hmju.memo.convenience

import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(defValue: T) : MutableLiveData<T>() {
    init {
        value = defValue
    }

    override fun getValue() = super.getValue()!!
}