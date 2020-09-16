package com.hmju.memo.convenience

import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(defaultValue: T) : MutableLiveData<T>() {
    init {
        value = defaultValue
    }

    override fun getValue() = super.getValue()!!
}