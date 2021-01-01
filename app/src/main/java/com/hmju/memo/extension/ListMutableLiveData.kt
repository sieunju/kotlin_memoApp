package com.hmju.memo.extension

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
        value.add(item)
    }

    fun add(pos: Int, item: T) {
        value.add(pos, item)
    }

    fun addAll(itemList: List<T>) {
        value.addAll(itemList)
    }

    fun remove(item: T) {
        value.remove(item)
    }

    fun remove(pos: Int) {
        value.removeAt(pos)
    }

    fun postAdd(item: T) {
        val items = value
        items.add(item)
        postValue(items)
    }

    fun postAdd(pos: Int, item: T) {
        val items = value
        items.add(pos,item)
        postValue(items)
    }

    fun postAddAll(itemList: List<T>) {
        val items = value
        items.addAll(itemList)
        value = items
    }

    fun postRemove(item: T) {
        val items = value
        items.remove(item)
        value = items
    }

    fun postRemove(pos: Int) {
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

    fun contains(item: T): Boolean {
        return value.contains(item)
    }

    fun clear() {
        value.clear()
    }

    fun postClear(){
        val items = value
        items.clear()
        value = items
    }
}