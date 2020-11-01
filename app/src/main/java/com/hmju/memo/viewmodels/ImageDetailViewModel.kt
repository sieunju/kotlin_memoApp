package com.hmju.memo.viewmodels

import com.hmju.memo.base.BaseViewModel
import com.hmju.memo.model.memo.FileItem

/**
 * Description : 이미지 자세히 보기 페이지
 *
 * Created by juhongmin on 2020/11/01
 */
class ImageDetailViewModel(
    private val pos : Int,
    private val pathList: ArrayList<FileItem>
) : BaseViewModel(){

}