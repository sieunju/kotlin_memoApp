package com.hmju.memo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * @param 기본 Activity 이동 처리 함수.
 */
inline fun <reified T : Activity> Activity.startAct() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * @param data : 고차 함수를 이용하여 세팅.
 */
inline fun <reified T : Activity> Activity.startAct(data: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.data()
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActResult(
    requestCode: Int,
    data: Intent.() -> Unit
) {
    val intent = Intent(this, T::class.java)
    intent.data()
    startActivityForResult(intent, requestCode)
}