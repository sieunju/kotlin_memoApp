package com.hmju.memo.repository.db

import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.db.dto.Memo
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Description : 비로그인 상태시 DB로 저장 관련 DataSource Class
 *
 * Created by juhongmin on 12/20/20
 */
interface RoomDataSource {
    fun fetchMemoList(): Single<List<Memo>>
    fun insertMemo(memo: MemoItemForm) : Long
    fun deleteMemo(manageNo: Int) : Int
}