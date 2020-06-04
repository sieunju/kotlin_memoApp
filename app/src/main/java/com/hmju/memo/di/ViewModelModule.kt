package com.hmju.memo.di

import com.hmju.memo.viewModels.LoginViewModel
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Description: ViewModel Module
 *
 * Created by juhongmin on 2020/06/04
 */

val viewModule = module {

    viewModel { LoginViewModel() }
}