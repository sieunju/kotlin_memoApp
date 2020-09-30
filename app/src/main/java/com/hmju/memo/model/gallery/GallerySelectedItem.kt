package com.hmju.memo.model.gallery

/**
 * Description : 선택한 갤러리 데이터 모델 클래스
 * @param id -> 갤러리 콘텐츠 아이디
 * @param isSelected -> 선택 유무 (필요 없으면 제거 예정)
 * @param pos -> RecyclerView 선택한 포지션
 *
 * Created by juhongmin on 2020/09/25
 */
data class GallerySelectedItem(
    var id: String,
    var isSelected: Boolean? = false,
    var pos : Int
) {
    override fun equals(other: Any?): Boolean {
        return if(other is GallerySelectedItem) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + pos
        return result
    }
}