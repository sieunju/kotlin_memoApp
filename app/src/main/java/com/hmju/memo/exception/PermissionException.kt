package com.hmju.memo.exception

/**
 * Description : 권한 관련 Exception
 *
 * Created by hmju on 2020-06-17
 */
class PermissionException(
    private val permissionCd: Int,
    private val msg: String) : Exception() {
    companion object {
        const val CODE_LOCATION = 100
        const val CODE_CAMERA = 101
    }

    override fun toString(): String {
        return "PermissionException Code $permissionCd\nMsg $msg"
    }
}