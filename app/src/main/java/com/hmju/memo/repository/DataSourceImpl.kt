package com.hmju.memo.repository

import com.hmju.memo.base.BaseResponse
import com.hmju.memo.convenience.io
import com.hmju.memo.convenience.ui
import com.hmju.memo.convenience.withIo
import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.form.MemoListParam
import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoFileResponse
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.model.memo.MemoResponse
import com.hmju.memo.repository.db.AppDataBase
import com.hmju.memo.repository.db.dto.Memo
import com.hmju.memo.repository.db.dto.MemoImage
import com.hmju.memo.repository.network.ApiService
import com.hmju.memo.repository.network.login.LoginManager
import com.hmju.memo.repository.network.paging.PagingModel
import com.hmju.memo.utils.ImageFileProvider
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import java.io.File
import java.util.*

/**
 * Description : Local or Remote 처리 하는
 * DataSource Class
 *
 * Created by hmju on 2020-12-21
 */
class DataSourceImpl(
    private val loginManager: LoginManager,
    private val imgFileProvider: ImageFileProvider,
    private val dataBase: AppDataBase,
    private val apiService: ApiService
) : DataSource {

    /**
     * Local or Network
     * Add or Update Memo Func..
     * @param form Request Memo Item Form
     * @return MemoResponse
     */
    override fun postMemo(form: MemoItemForm): Single<MemoResponse> {
        // API Call
        if (loginManager.isLogin().value == true) {
            return if (form.manageNo == null) {
                // Add Memo
                apiService.postMemo(form)
            } else {
                // Update Memo
                apiService.updateMemo(form)
            }
        } else {
            // Local Data Base
            val memoDataBase = if (form.manageNo == null)
                Memo(
                    tag = form.tag,
                    title = form.title,
                    contents = form.contents
                )
            else
                Memo(
                    manageNo = form.manageNo,
                    tag = form.tag,
                    title = form.title,
                    contents = form.contents
                )

            return if (form.manageNo == null) {
                dataBase.memoDao().insertMemo(memoDataBase)
            } else {
                dataBase.memoDao().updateMemo(memoDataBase)
            }
                .flatMap { id ->
                    Single.create<MemoResponse> {
                        if (id.toInt() > 0) {
                            it.onSuccess(MemoResponse(status = true, manageNo = id.toInt()))
                        } else {
                            it.onError(Throwable("Room Database Insert And Update Fail.."))
                        }
                    }
                }
        }
    }

    /**
     * Local or Network
     * Delete Memo Func..
     * @param manageNo Memo Unique Id
     * @return BaseResponse
     */
    override fun deleteMemo(manageNo: Int): Single<BaseResponse> {
        // Api Call..
        if (loginManager.isLogin().value == true) {
            return apiService.deleteMemo(memoId = manageNo)
        } else {
            return dataBase.memoDao().deleteMemo(manageNo = manageNo).flatMap { id ->
                Single.create<BaseResponse> {
                    if (id > 0) {
                        it.onSuccess(BaseResponse())
                    } else {
                        it.onError(Throwable("Room Database Delete Memo Error"))
                    }
                }
            }
        }

    }

    /**
     * Local or Network
     * Add Memo Images Func..
     * @param manageNo Memo Id
     * @param pathList Image File Path List
     * @return MemoFileResponse..
     */
    override fun postMemoImages(manageNo: Int, pathList: List<String>): Single<MemoFileResponse> {
        // Api Call..
        if (loginManager.isLogin().value == true) {
            val tmpFileList = arrayListOf<File>()
            return Single.fromCallable {
                val multiParts = arrayListOf<MultipartBody.Part>()
                for (path in pathList) {
                    imgFileProvider.createMultiPartBody(path)?.also {
                        multiParts.add(
                            MultipartBody.Part.createFormData(
                                name = "files",
                                filename = it.second.name,
                                body = it.first
                            )
                        )
                        // TmpFile Add.
                        tmpFileList.add(it.second)
                    }
                }
                return@fromCallable multiParts
            }.io()
                .flatMap { parts ->
                    apiService.uploadFile(memoId = manageNo, files = parts)
                }
                .ui()
                .doOnSuccess { tmpFileList.forEach { imgFileProvider.deleteFile(it) } }
                .doOnError { tmpFileList.forEach { imgFileProvider.deleteFile(it) } }
        } else {
            // Room DataBase
            return Single.fromCallable {
                val list = arrayListOf<FileItem>()
                pathList.forEach { path ->
                    val id = dataBase.memoDao().insertMemoImage(
                        MemoImage(
                            memoId = manageNo,
                            path = path
                        )
                    )
                    list.add(FileItem(manageNo = id.toInt(), filePath = path))
                }
                return@fromCallable list
            }.withIo().flatMap { list ->
                Single.create<MemoFileResponse> {
                    if (list.size == pathList.size) {
                        it.onSuccess(
                            MemoFileResponse(
                                status = true,
                                successMsg = "Room Database Success Insert",
                                pathList = list
                            )
                        )
                    } else {
                        // DB Delete 처리로직 추가.
                        it.onError(Throwable("Room DataBase Fail Insert"))
                    }

                }
            }
        }
    }

    /**
     * Local or Network
     * Delete Images Func..
     * @param files ImageFile Item List
     * @return BaseResponse
     */
    override fun deleteMemoImages(files: List<FileItem>): Single<BaseResponse> {
        // Api Call..
        return if (loginManager.isLogin().value == true) {
            apiService.deleteFiles(
                manageNoList = files.map { it.manageNo }.toList(),
                pathList = files.map { it.filePath }.toList()
            )
        } else {
            // Room DataBase
            Flowable.fromIterable(files).flatMap {
                dataBase.memoDao().deleteMemoImage(it.manageNo).toFlowable()
            }.toList().flatMap {
                Single.create<BaseResponse> { it.onSuccess(BaseResponse()) }
            }
        }
    }

    override fun fetchMemoList(params: MemoListParam): PagingModel<MemoItem> {
        TODO("Not yet implemented")
    }
}