package com.hmju.memo.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.define.Etc
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
    pair: Pair<View, MemoItem>
) {
    moveMemoDetail(Triple(pair.first, pair.second, -1))
}

/**
 * 메모 상세보기 페이지 진입
 */
fun Activity.moveMemoDetail(
    triple: Triple<View, MemoItem, Int>
) {
    val options =
        ActivityOptions.makeSceneTransitionAnimation(
            this,
            triple.first,
            BaseActivity.TRANSITIONNAME
        )
    val intent = Intent(this, MemoDetailActivity::class.java)
    val bundle = Bundle()
    bundle.putSerializable(ExtraCode.MEMO_DETAIL, triple.second)
    bundle.putInt(ExtraCode.MEMO_DETAIL_POS, triple.third)
    intent.putExtras(bundle)

    startActivityForResult(intent, RequestCode.MEMO_DETAIL, options.toBundle())
}

/**
 * 카메라 캡처 화면 진입
 */
fun Activity.moveCameraCapture(photoUriCallback: (Uri?) -> Unit) {
    // 권한 상태 체크.
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            val photoName =
                "Memo_${System.currentTimeMillis()}${Etc.IMG_FILE_EXTENSION}"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photoName)
                put(MediaStore.Images.Media.MIME_TYPE, Etc.IMG_MIME_TYPE_FILE_EXTENSION)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
            }

            val photoUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            photoUriCallback.invoke(photoUri)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, RequestCode.CAMERA_CAPTURE)
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top)
        }
    }
}