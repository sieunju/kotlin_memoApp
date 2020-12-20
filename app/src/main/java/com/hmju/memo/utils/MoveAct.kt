package com.hmju.memo.utils

import android.annotation.SuppressLint
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
import android.view.View
import androidx.core.content.FileProvider
import com.hmju.memo.R
import com.hmju.memo.base.BaseActivity
import com.hmju.memo.define.Etc
import com.hmju.memo.define.ExtraCode
import com.hmju.memo.define.RequestCode
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.ui.memo.MainActivity
import com.hmju.memo.ui.memo.MemoDetailActivity
import org.koin.android.ext.android.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

inline fun <reified T : Activity> Activity.startActBundle(data: Bundle.() -> Unit) {
    val intent = Intent(this,T::class.java)
    val bundle = Bundle()
    bundle.data()
    intent.putExtras(bundle)
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

fun Activity.moveMain() {
    startAct<MainActivity>{
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
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
 * 카메라 캡처 페이지 이동 함수.
 *
 * @param uri 카메라 캡처후 해당 파일 정보 세팅할 위치값.
 */
fun Activity.moveCamera(uri: Uri?) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        resolveActivity(packageManager)?.let {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(this, RequestCode.CAMERA_CAPTURE)
        }
    }
}

//@SuppressLint("SimpleDateFormat")
//fun Activity.moveCamera(callBack: (String) -> Unit) {
//    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent->
//        intent.resolveActivity(packageManager)?.also {
//            try {
//                // Create TempFile
//                val tempFile = File.createTempFile(
//                    "temp_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}",
//                    ".jpg",
//                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//                ).apply {
//                    // Save a File
//                    JLogger.d("File Path $absolutePath")
//                    callBack.invoke(absolutePath)
//                }
//                tempFile.also {
//                    val photoUri: Uri = FileProvider.getUriForFile(
//                        this,
//                        packageName,
//                        it
//                    )
//                    JLogger.d("Photo Uri $photoUri")
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
//                    startActivityForResult(intent,RequestCode.CAMERA_CAPTURE)
//                }
//            } catch (ex :Exception) {
//                JLogger.d("Create File Exception ${ex.message}")
//            }
//        }
//
//    }
//}

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
                put(MediaStore.Images.Media.TITLE, "내용!?")
                put(MediaStore.Images.Media.DISPLAY_NAME, photoName)
                put(MediaStore.Images.Media.MIME_TYPE, Etc.IMG_MIME_TYPE_FILE_EXTENSION)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
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