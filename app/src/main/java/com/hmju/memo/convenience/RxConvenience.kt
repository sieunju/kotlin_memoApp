package com.hmju.memo.convenience

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit

/**
 * Description : Reactive X 편의성을 위한 정의한곳
 *
 * Created by hmju on 2020-06-09
 */
// 관련 링크 https://beomseok95.tistory.com/233
// Schedulers.computation() - 이벤트 룹에서 간단한 연산이나 콜백 처리를 위해 사용됩니다.
// RxComputationThreadPool라는 별도의 스레드 풀에서 돌아갑니다. 최대 cㅔu갯수 개의 스레드 풀이 순환하면서 실행됩니다.
//
// Schedulers.immediate() - 현재 스레드에서 즉시 수행합니다.
// observeOn()이 여러번 쓰였을 경우 immediate()를 선언한 바로 윗쪽의 스레드를 따라갑니다.
// Schedulers.from(executor) - 특정 executor를 스케쥴러로 사용합니다.
// Schedulers.io() - 동기 I/O를 별도로 처리시켜 비동기 효율을 얻기 위한 스케줄러입니다.
// 자체적인 스레드 풀 CachedThreadPool을 사용합니다. API 호출 등 네트워크를 사용한 호출 시 사용됩니다.
// Schedulers.newThread() - 새로운 스레드를 만드는 스케쥴러입니다.
// Schedulers.trampoline() - 큐에 있는 일이 끝나면 이어서 현재 스레드에서 수행하는 스케쥴러.
// AndroidSchedulers.mainThread() - 안드로이드의 UI 스레드에서 동작합니다.
// HandlerScheduler.from(handler) - 특정 핸들러 handler에 의존하여 동작합니다.

// 단일로 요청하는 경우.
fun <T> Maybe<T>.single() = subscribeOn(io()).observeOn(AndroidSchedulers.mainThread())

// 여러개의 API 를 한꺼번에 보내는 경우
fun <T> Flowable<T>.multi() = subscribeOn(io())

// 여러개의 API를 한꺼번에 보내는 경우 Delay 타입.
fun <T> Flowable<T>.multiDelay(_delay: Int) = subscribeOn(io()).delay(_delay.toLong(),TimeUnit.SECONDS)

open class SimpleDisposableSubscriber<T> : DisposableSubscriber<T>() {
    override fun onNext(t: T) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onComplete() {
    }
}