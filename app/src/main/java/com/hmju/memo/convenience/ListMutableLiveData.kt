package com.hmju.memo.convenience

import androidx.lifecycle.MutableLiveData


/**
 * Description : List 형식의 LiveData.
 *
 * Created by juhongmin on 2020/06/10
 */
class ListMutableLiveData<T> : MutableLiveData<ArrayList<T>>(){
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

    fun remove(item: T){
        value.remove(item)
    }

    fun remove(pos: Int){
        value.removeAt(pos)
    }

    fun clear(){
        value.clear()
    }
}