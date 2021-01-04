package com.hmju.memo.convenience

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

/**
 * Description : Reactive X 확장 함수
 *
 * Created by hmju on 2020-06-09
 */
// subscribeOn -> Observable.create(작업 쓰레드), Observable.just(작업 쓰레드) 에서 사용할 쓰레드
// observeOn -> 메인 작업을 완료하고 그다음 어떤 쓰레드를 사용할건지 정하는 것.

// observeOn()이 여러번 쓰였을 경우 immediate()를 선언한 바로 윗쪽의 스레드를 따라갑니다.
// Schedulers.computation() - 이벤트 룹에서 간단한 연산이나 콜백 처리를 위해 사용됩니다. (데이터 가공, 이나 단순 계산들)
// RxComputationThreadPool라는 별도의 스레드 풀에서 돌아갑니다. 최대 cㅔu갯수 개의 스레드 풀이 순환하면서 실행됩니다.
// Schedulers.immediate() - 현재 스레드에서 즉시 수행합니다.
// Schedulers.from(executor) - 특정 executor를 스케쥴러로 사용합니다.
// Schedulers.io() - 동기 I/O를 별도로 처리시켜 비동기 효율을 얻기 위한 스케줄러입니다. (Network, DB Query 같은 것들)
// 자체적인 스레드 풀 CachedThreadPool을 사용합니다. API 호출 등 네트워크를 사용한 호출 시 사용됩니다.
// Schedulers.newThread() - 새로운 스레드를 만드는 스케쥴러입니다.
// Schedulers.trampoline() - 큐에 있는 일이 끝나면 이어서 현재 스레드에서 수행하는 스케쥴러.
// AndroidSchedulers.mainThread() - 안드로이드의 UI 스레드에서 동작합니다.
// HandlerScheduler.from(handler) - 특정 핸들러 handler에 의존하여 동작합니다.

// NetWork
fun <T> Single<T>.withIo(): Single<T> =
        subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.io(): Single<T> = subscribeOn(Schedulers.io())
fun <T> Single<T>.ui(): Single<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Single<T>.compute(): Single<T> = observeOn(Schedulers.computation())

fun <T> Flowable<T>.withCompute(): Flowable<T> =
        subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.withIo(): Flowable<T> = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.compute() = subscribeOn(Schedulers.computation())
fun <T> Flowable<T>.ui(): Flowable<T> =
        observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.io() = subscribeOn(Schedulers.io())
fun <T> Observable<T>.ui(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

open class SimpleDisposableSubscriber<T> : DisposableSubscriber<T>() {
    override fun onNext(t: T) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onComplete() {
    }
}

// MultiPle Null Check.
inline fun <A, B, R> let(a: A?, b: B?, function: (A, B) -> R) {
    if (a != null && b != null) {
        function(a, b)
    }
}

// MultiPle Null Check.
inline fun <A, B, C, R> let(a: A?, b: B?, c: C?, function: (A, B, C) -> R) {
    if (a != null && b != null && c != null) {
        function(a, b, c)
    }
}