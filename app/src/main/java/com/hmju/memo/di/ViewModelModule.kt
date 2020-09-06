package com.hmju.memo.di

import com.hmju.memo.model.memo.MemoDetailInfo
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.LoginViewModel
import com.hmju.memo.viewModels.MainViewModel
import com.hmju.memo.viewModels.MemoAddViewModel
import com.hmju.memo.viewModels.MemoDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Description: ViewModel Module
 *
 * Created by juhongmin on 2020/06/04
 */

val viewModelModule = module {

    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MemoAddViewModel(get(), get()) }
//    viewModel { (
//                    memoInfo: MemoDetailInfo
//                ) ->
//        MemoDetailViewModel(memoInfo, get())
//    }
    viewModel { (memoInfo: MemoItem) ->
        MemoDetailViewModel(memoInfo = memoInfo, get())
    }
}