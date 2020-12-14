package com.hmju.memo.model.test

import com.google.gson.annotations.SerializedName


open class TestNormalEventImg(
    @SerializedName("v_event_reg_dt")
    val regDtm : String,
    @SerializedName("v_event_en_dt")
    val endDtm : String,
    @SerializedName("v_event_img_path")
    val imgPath : String,
    @SerializedName("v_event_nm")
    val title: String
) : TestBaseImgModel()

open class TestUrlBannerImg(
    @SerializedName("v_img_url")
    val imgUrl: String,
    @SerializedName("v_url")
    val linkUrl: String
) : TestBaseImgModel()

open class TestNormalBannerImg(
    @SerializedName("v_image_path")
    val imgPath: String
) : TestBaseImgModel()

open class TestHeaderImg(
    @SerializedName("v_image_path")
    val imgPath: String,
    @SerializedName("v_url")
    val linkUrl: String,
    @SerializedName("v_code")
    val code: String,
    @SerializedName("v_title")
    val title: String
) : TestBaseImgModel()

open class TestBaseImgModel {
    @SerializedName("manage_no")
    var manageNo: Int = -1
}