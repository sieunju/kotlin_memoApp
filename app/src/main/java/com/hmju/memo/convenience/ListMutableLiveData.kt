package com.hmju.memo.convenience

import androidx.lifecycle.MutableLiveData


/**
 * Description :
 *
 * Created by juhongmin on 2020/06/10
 */
class ListMutableLiveData<T : Any> : MutableLiveData<ArrayList<T>>(){
    init {
        // new ArrayList
        value = arrayListOf()
    }

    // NonNull 형
    override fun getValue() = super.getValue()!!

    fun add(item: T){
        value.add(item)
    }

    fun add(index: Int, item: T){
        value.add(index,item)
    }

    fun addAll(itemList: List<T>){
        value.addAll(itemList)
    }
}