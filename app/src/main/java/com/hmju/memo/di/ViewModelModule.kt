package com.hmju.memo.di

import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewModels.*
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
    viewModel { MemoAddViewModel(get(), get(),get()) }

    viewModel { (pos: Int, memoInfo: MemoItem) ->
        MemoDetailViewModel(
            memoPosition = pos,
            originData = memoInfo,
            apiService = get(),
            provider = get(),
            resProvider = get()
        )
    }

    viewModel { (limitSize: Int) ->
        GalleryViewModel(
            limitImageSize = limitSize,
            provider = get(),
            resProvider = get()
        )
    }

    viewModel { (list: ArrayList<String>) ->
        ImageEditViewModel(
            photoList = list,
            provider = get()
        )
    }
}