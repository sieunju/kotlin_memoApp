package com.hmju.memo.repository.db.dao

import androidx.room.*
import com.hmju.memo.model.memo.MemoItem
import com.hmju.memo.repository.db.dto.Memo
import com.hmju.memo.repository.db.dto.MemoImage
import com.hmju.memo.repository.db.dto.RoomMemoItem
import io.reactivex.Single

/**
 * Description :
 *
 * Created by juhongmin on 12/17/20
 */
@Dao
interface MemoDao {

    @Insert
    fun insertMemo(memo: Memo): Single<Long>

    @Update
    fun updateMemo(memo: Memo): Single<Int>

    @Query("DELETE FROM MEMO WHERE manageNo = :manageNo")
    fun deleteMemo(manageNo: Int): Single<Int>

    @Insert
    fun insertMemoImage(memoImage: MemoImage): Long

    @Query("DELETE FROM MEMO_IMG WHERE manageNo = :manageNo")
    fun deleteMemoImage(manageNo: Int): Single<Int>

//    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
//    @Query(
//        "SELECT M.manageNo, M.TAG, M.title, " +
//                "M.CONTENTS, F.manageNo AS imgNo, F.IMG_PATH, " +
//                "M.REGISTER_DATE " +
//                "FROM MEMO M " +
//                "ORDER BY TAG ASC LIMIT :pageSize OFFSET :offset " +
//                "LEFT JOIN MEMO_IMG F ON M.manageNo = F.MEMO_ID "
//    )
//    fun fetchMemoList(offset : Int, pageSize: Int): Single<List<RoomMemoItem>>
}