package com.hmju.memo.utils

import android.util.Log
import com.hmju.memo.BuildConfig

/**
 * Description: Logger Utils Class.
 *
 * Created by juhongmin on 2020/05/30
 */
class JLogger {
    companion object {
        val TAG = "JLogger"

        @JvmStatic
        @JvmName("d")
        fun d(msg: String) {
            if (BuildConfig.DEBUG) {
                val ste = Thread.currentThread().stackTrace[4]
                val sb = StringBuilder()
                sb.append("[")
                sb.append(ste.methodName)
                sb.append("]")
                Log.d("$TAG:$sb", msg)
            }
        }

        fun d(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, msg)
            }
        }

        @JvmStatic
        @JvmName("e")
        fun e(msg: String) {
            if (BuildConfig.DEBUG) {
                val ste = Thread.currentThread().stackTrace[4]
                val sb = StringBuilder()
                sb.append("[")
                sb.append(ste.methodName)
                sb.append("]")
                Log.e("$TAG:$sb", msg)
            }
        }
    }
}