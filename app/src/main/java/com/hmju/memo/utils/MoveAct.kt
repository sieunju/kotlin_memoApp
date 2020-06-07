package com.hmju.memo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Description: Activity 이동 처리 클래
 *
 * Created by juhongmin on 2020/06/07
 */


inline fun <reified T: Activity> Context.startAct(){
    val intent = Intent(this,T::class.java)
    startActivity(intent)
}