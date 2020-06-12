package com.hmju.memo.convenience

import androidx.lifecycle.MutableLiveData


/**
 * Description : List 형식의 LiveData.
 *
 * Created by juhongmin on 2020/06/10
 */
class ListMutableLiveData<T> : MutableLiveData<ArrayList<T>>() {
    init {
        // new ArrayList
        value = arrayListOf()
    }

    // NonNull 형
    override fun getValue() = super.getValue()!!

    fun add(item: T) {
        val items = value
        items.add(item)
        value = items
    }

    fun add(pos: Int, item: T) {
        val items = value
        items.add(pos,item)
        value = items
    }

    fun addAll(itemList: List<T>) {
        val items = value
        items.addAll(itemList)
        value = items
    }

    fun remove(item: T) {
        val items = value
        items.remove(item)
        value = items
    }

    fun remove(pos: Int) {
        val items = value
        items.removeAt(pos)
        value = items
    }

    fun size(): Int {
        return value.size
    }

    fun get(pos: Int): T {
        return value[pos]
    }

    fun clear() {
        val items = value
        items.clear()
        value = items
    }
}