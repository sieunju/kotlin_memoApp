package com.hmju.memo.di

import com.hmju.memo.model.memo.FileItem
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.viewmodels.*
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

    viewModel { (limitSize: Int) ->
        GalleryViewModel(
            limitImageSize = limitSize,
            provider = get(),
            fileProvider = get(),
            resProvider = get()
        )
    }

    viewModel { (list: ArrayList<String>) ->
        ImageEditViewModel(
            photoList = list,
            provider = get()
        )
    }

    viewModel { (item: MemoItem?) ->
        MemoDetailViewModel(
            originData = item,
            apiService = get(),
            provider = get(),
            resProvider = get()
        )
    }

    viewModel { (pos: Int, list: ArrayList<FileItem>) ->
        ImageDetailViewModel(
            pos = pos,
            pathList = list
        )
    }
}