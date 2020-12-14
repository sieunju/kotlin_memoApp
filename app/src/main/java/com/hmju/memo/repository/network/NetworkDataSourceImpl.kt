package com.hmju.memo.repository.network

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.bumptech.glide.load.resource.file.FileResource
import com.google.gson.JsonObject
import com.hmju.memo.convenience.io
import com.hmju.memo.convenience.netIo
import com.hmju.memo.convenience.nextCompute
import com.hmju.memo.define.TestCardType
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.test.TestHeaderImg
import com.hmju.memo.model.test.TestResponse
import com.hmju.memo.model.test.TestUiModel
import com.hmju.memo.repository.network.paging.MemoListDataSourceFactory
import com.hmju.memo.repository.network.paging.PagingModel
import com.hmju.memo.repository.preferences.AccountPref
import com.hmju.memo.utils.GsonProvider
import com.hmju.memo.utils.JLogger
import com.hmju.memo.utils.ResourceProvider
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

/**
 * Description : Network 통신후 데이터 처리를 위한 클래스,
 * 파라미터는 추후 추가될수도 있음.
 *
 * Created by hmju on 2020-09-16
 */
class NetworkDataSourceImpl(
    private val apiService: ApiService,
    private val actPref: AccountPref,
    private val provider: ResourceProvider,
    private val gson: GsonProvider
) : NetworkDataSource {

    override fun fetchMemoList(params: MemoListParam): PagingModel<MemoItem> {
        val factory = MemoListDataSourceFactory(
            apiService = apiService,
            actPref = actPref,
            memoParams = params
        )
        val pagedList = factory.toLiveData(
            Config(
                pageSize = 20,
                initialLoadSizeHint = 20,
                enablePlaceholders = true
            )
        )

        return PagingModel(
            pagedList = pagedList,
            networkState = Transformations.switchMap(factory.sourceLiveData) { it.networkState }
        )
    }

    override fun fetchMainTest(): Single<ArrayList<TestUiModel>> {
        return apiService
            .fetchMainTest()
            .io()
            .nextCompute()
            .flatMap { jsonObject ->
                JLogger.d("Json Parser Thread ${Thread.currentThread()}")
                val code = gson.getValue(jsonObject, "status", Boolean::class.java)
                val errMsg = gson.getValue(jsonObject, "errMsg", String::class.java)
                val dataList = arrayListOf<TestUiModel>()

                if (code == false) {
                    return@flatMap Single.create<ArrayList<TestUiModel>> {
                        it.onError(
                            Throwable(
                                errMsg
                            )
                        )
                    }
                }

                // Header Contents
                gson.getList(jsonObject, "headerContents", TestHeaderImg::class.java)
                    ?.also { list ->
                        dataList.add(TestUiModel.TestHeaderModel(headerContents = list))
                    }

                // Main Contents
                val jsonArray = jsonObject.getAsJsonArray("mainContents")

                for (i in 0 until jsonArray.size()) {
                    JLogger.d("TEST:: CNT ${i}")
                    val item = jsonArray[i]
                    val type : String? = gson.getValue(item,TestCardType.KEY_CARD,String::class.java)

                    when (type) {
                        TestCardType.A0001 -> {
                            gson.getObject(item,TestUiModel.A0001Model::class.java)?.let { dataList.add(it) }
                        }

                        TestCardType.A0002 -> {
                            val subType =
                                gson.getValue(item, TestCardType.KEY_SUB, String::class.java)
                            // Sub Type 분기 처리
                            if (subType == TestCardType.B0001) {
                                gson.getObject(item, TestUiModel.A0002AndB0001Model::class.java)
                                    ?.also { model ->
                                        dataList.add(model)
                                    }
                            } else {
                                gson.getObject(item, TestUiModel.A0002Model::class.java)
                                    ?.also { model ->
                                        dataList.add(model)
                                    }
                            }
                        }

                        TestCardType.A0003 -> {
                            val subType =
                                gson.getValue(item, TestCardType.KEY_SUB, String::class.java)

                            // Sub Type 분기 처리
                            if (subType == TestCardType.B0001) {
                                gson.getObject(item, TestUiModel.A0003AndB0001Model::class.java)
                                    ?.also { model ->
                                        dataList.add(model)
                                    }
                            }
                        }

                        TestCardType.A0004 -> {
                            val subType =
                                gson.getValue(item, TestCardType.KEY_SUB, String::class.java)

                            // Sub Type 분기 처리
                            if (subType == TestCardType.B0002) {
                                gson.getObject(item, TestUiModel.A0004AndB0002Model::class.java)
                                    ?.also { model ->
                                        dataList.add(model)
                                    }
                            }
                        }

                        TestCardType.A0005 -> {
                            val subType =
                                gson.getValue(item, TestCardType.KEY_SUB, String::class.java)

                            // Sub Type 분기 처리
                            if (subType == TestCardType.B0003) {
                                gson.getObject(item, TestUiModel.A0004AndB0002Model::class.java)
                                    ?.also { model ->
                                        dataList.add(model)
                                    }
                            }
                        }
                    }
                }

                return@flatMap Single.create<ArrayList<TestUiModel>> {
                    it.onSuccess(dataList)
                }
            }
    }
}