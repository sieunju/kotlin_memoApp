package com.hmju.memo.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hmju.memo.R

/**
 * Description : 공통 버튼
 *
 * Created by hmju on 2020-06-16
 */
class ConfirmDialog : MaterialAlertDialogBuilder {

    // Button 타입
    enum class Type {
        ONE, TWO
    }

    constructor(
        ctx: Context,
        @StringRes msgId: Int
    ) : this(
        ctx = ctx,
        msg = ctx.getString(msgId),
        type = Type.ONE
    )

    constructor(ctx: Context, msg: String) :
            this(
                ctx = ctx,
                msg = msg,
                type = Type.ONE
            )


    constructor(ctx: Context, msg: String, leftText: String, rightText: String) :
            this(
                ctx = ctx,
                msg = msg,
                type = Type.TWO,
                leftText = leftText,
                rightText = rightText
            )

    constructor(
        ctx: Context,
        msg: String,
        type: Type,
        leftText: String? = ctx.getString(R.string.str_cancel),
        rightText: String? = ctx.getString(R.string.str_confirm),
        listener: DialogInterface.OnClickListener? = null
    ) : super(ctx) {
        setMessage(msg)
        if (type == Type.TWO) {
            setNegativeButton(leftText, listener)
        }
        setPositiveButton(rightText, listener)
        show()
    }
}