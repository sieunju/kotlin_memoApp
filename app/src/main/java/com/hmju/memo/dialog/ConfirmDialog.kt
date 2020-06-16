package com.hmju.memo.dialog

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Description :
 *
 * Created by hmju on 2020-06-16
 */
class ConfirmDialog : MaterialAlertDialogBuilder {

    constructor(
        ctx: Context,
        msg: Int
    ) : this(
        ctx = ctx,
        msgId = msg,
        leftText = null,
        rightText = null,
        listener = null
    )

    constructor(ctx: Context, msg: String) :
            this(
                ctx = ctx,
                msg = msg,
                leftText = null,
                rightText = null,
                listener = null
            )


    constructor(ctx: Context, msg: String, leftText: String, rightText: String) :
            this(
                ctx = ctx,
                msg = msg,
                leftText = leftText,
                rightText = rightText,
                listener = null
            )

    constructor(
        ctx: Context,
        msg: String? = "확인했습니다.",
        leftText: String? = null,
        rightText: String? = null,
        listener: DialogInterface.OnClickListener?
    ) : super(ctx) {
        val right = rightText ?: "확인"
        setMessage(msg)
        setNegativeButton(leftText, listener)
        setPositiveButton(right, listener)
        show()
    }

    constructor(
        ctx: Context,
        msgId: Int,
        leftText: String? = "취소",
        rightText: String? = "확인",
        listener: DialogInterface.OnClickListener? = null
    ) : super(ctx) {
        setMessage(msgId)
        setNegativeButton(leftText, listener)
        setPositiveButton(rightText, listener)
        show()
    }
}