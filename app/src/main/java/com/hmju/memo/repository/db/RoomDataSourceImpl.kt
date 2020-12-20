package com.hmju.memo.repository.db

import com.hmju.memo.model.form.MemoItemForm
import com.hmju.memo.repository.db.dto.Memo
import io.reactivex.Completable
import io.reactivex.Single

class RoomDataSourceImpl(
    private val dataBase: AppDataBase

) : RoomDataSource {
    override fun fetchMemoList(): Single<List<Memo>> {
        return dataBase.memoDao().fetchMemoList()
    }

    override fun insertMemo(memo: MemoItemForm): Long {
        return dataBase.memoDao().insertMemo(
            Memo(
                tag = memo.tag,
                title = memo.title,
                contents = memo.contents,
                date = System.currentTimeMillis(),
                test = "EEE??"
            )
        )
    }

    override fun deleteMemo(manageNo: Int): Int {
        TODO("Not yet implemented")
    }
}