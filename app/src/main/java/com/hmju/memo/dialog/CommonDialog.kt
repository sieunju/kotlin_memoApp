package com.hmju.memo.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.hmju.memo.R
import java.lang.Exception

/**
 * Common Dialog Class
 */
class CommonDialog(private val context: Context) {

    interface Listener {
        fun onClick(which: Int)
    }

    private val rootView: View by lazy {
        View.inflate(context, R.layout.dialog_common, null)
    }
    private var tvTitle: AppCompatTextView? = null
    private var tvContents: AppCompatTextView? = null
    private var isCancel: Boolean = false
    private var listener: Listener? = null

    private lateinit var dialog: Dialog

    companion object {
        val POSITIVE: Int = 1
        val NEGATIVE: Int = 2
    }


    fun setTitle(@StringRes id: Int): CommonDialog {
        return setTitle(context.getString(id))
    }

    fun setTitle(text: String): CommonDialog {
        if (tvTitle == null) {
            tvTitle = rootView.findViewById(R.id.tvTitle)
        }

        if (text.isNotEmpty()) {
            tvTitle!!.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                tvTitle!!.text = Html.fromHtml(text.replace("\n", "<br>"))
            } else {
                tvTitle!!.text =
                    Html.fromHtml(text.replace("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY)
            }
        }
        return this
    }

    fun setContents(@StringRes id: Int): CommonDialog {
        return setContents(context.getString(id))
    }

    fun setContents(text: String): CommonDialog {
        if (tvContents == null) {
            tvContents = rootView.findViewById(R.id.tvContents)
        }

        if (text.isNotEmpty()) {
            tvContents!!.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                tvContents!!.text = Html.fromHtml(text.replace("\n", "<br>"))
            } else {
                tvContents!!.text =
                    Html.fromHtml(text.replace("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY)
            }
        }
        return this
    }

    fun setNegativeButton(@StringRes id: Int): CommonDialog {
        return setNegativeButton(context.getString(id))
    }

    fun setNegativeButton(text: String): CommonDialog {
        val button = rootView.findViewById<AppCompatTextView>(R.id.tvNegative)
        button.text = text
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            dismiss()
            listener?.onClick(NEGATIVE)
        }
        return this
    }

    fun setPositiveButton(@StringRes id: Int): CommonDialog {
        return setPositiveButton(context.getString(id))
    }

    fun setPositiveButton(text: String): CommonDialog {
        val button = rootView.findViewById<AppCompatTextView>(R.id.tvPositive)
        button.text = text
        button.visibility = View.VISIBLE
        button.setOnClickListener {
            dismiss()
            listener?.onClick(POSITIVE)
        }
        return this
    }

    fun setCancelable(isCancel: Boolean): CommonDialog {
        this.isCancel = isCancel
        return this
    }

    fun setListener(listener: Listener): CommonDialog {
        this.listener = listener
        return this
    }

    fun show() {
        val builder = AlertDialog.Builder(context, R.style.CommonDialog).setView(rootView)
        builder.setCancelable(isCancel)
        dialog = builder.create()
        dialog.show()
    }

    private fun dismiss() {
        Handler().post(Runnable {
            try {
                dialog.dismiss()
            } catch (ex: Exception) {

            }
        })
    }

}