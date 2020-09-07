package com.hmju.memo.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.RequestCode
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.memo.MemoDetailActivity

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

fun Activity.moveMemoDetail(
    rootView : View,
    memoData: MemoItem
) {
    // create the transition animation - the images in the layouts
    // of both activities are defined with android:transitionName="robot"
    val options = ActivityOptions.makeSceneTransitionAnimation(this,rootView,"rootView")
    val intent = Intent(this,MemoDetailActivity::class.java)
    val bundle = Bundle()
    bundle.putSerializable(ExtraCode.MEMO_DETAIL,memoData)
    intent.putExtras(bundle)
    startActivityForResult(intent,RequestCode.MEMO_DETAIL,options.toBundle())
}