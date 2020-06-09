package com.hmju.memo.utils

import android.util.Log

/**
 * Description: Logger Utils Class.
 *
 * Created by juhongmin on 2020/05/30
 */
class JLogger {
    companion object {
        val TAG = "JLogger"

        fun d(msg: String) {
            val ste = Thread.currentThread().stackTrace[4]
            val sb = StringBuilder()
            sb.append("[")
//            sb.append(ste.fileName.replace(".kt", ""))
//            sb.append("::")
            sb.append(ste.methodName)
            sb.append("]")
            Log.d("$TAG:$sb", "Thread\t${Thread.currentThread().name}\tMsg\t$msg")
        }

        fun e(msg: String) {
            val ste = Thread.currentThread().stackTrace[4]
            val sb = StringBuilder()
            sb.append("[")
            sb.append(ste.fileName.replace(".kt", ""))
            sb.append("::")
            sb.append(ste.methodName)
            sb.append("]")
            Log.e("$TAG:$sb", msg)
        }

        @JvmStatic
        fun D(msg: String) {
            val ste = Thread.currentThread().stackTrace[4]
            val sb = StringBuilder()
            sb.append("[")
//            sb.append(ste.fileName.replace(".kt", ""))
//            sb.append("::")
            sb.append(ste.methodName)
            sb.append("]")
            Log.d("$TAG:$sb", msg)
        }
    }
}