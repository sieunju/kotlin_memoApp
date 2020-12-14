package com.hmju.memo.model.test

import com.google.gson.annotations.SerializedName

/**
 * Description :
 *
 * Created by juhongmin on 2020/12/06
 */
@SuppressWarnings("serial")
sealed class TestUiModel {

    data class TestHeaderModel(
        val headerContents: ArrayList<TestHeaderImg>
    ) : TestUiModel()

    data class A0001Model(
        @SerializedName("v_card_name")
        val cardNm: String,
        @SerializedName("bannerList")
        val dataList: ArrayList<TestNormalBannerImg>
    ) : TestUiModel()

    data class A0002Model(
        @SerializedName("v_title")
        val title: String,
        @SerializedName("v_content")
        val content: String,
        @SerializedName("bannerlist")
        val dataList: ArrayList<TestUrlBannerImg>
    ) : TestUiModel()

    data class A0002AndB0001Model(
        @SerializedName("v_title")
        val title: String,
        @SerializedName("v_content")
        val content : String,
        @SerializedName("events")
        val dataList: ArrayList<TestNormalEventImg>
    ) : TestUiModel()

    data class A0003AndB0001Model(
        @SerializedName("v_title")
        val title: String,
        @SerializedName("v_content")
        val content: String,
        @SerializedName("personalList")
        val dataList: ArrayList<TestNormalEventImg>
    ) : TestUiModel()

    data class A0004AndB0002Model(
        @SerializedName("v_title")
        val title: String,
        @SerializedName("v_content")
        val content: String,
        @SerializedName("v_image_path")
        val imgPath : String
    ) : TestUiModel()

    data class A0005AndB0003Model(
        @SerializedName("v_title")
        val title: String,
        @SerializedName("v_content")
        val content: String,
        @SerializedName("v_image_path")
        val imgPath: String
    ) : TestUiModel()

}