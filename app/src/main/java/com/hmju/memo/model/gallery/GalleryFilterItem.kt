package com.hmju.memo.model.gallery

/**
 * Description : 갤럴리 필터 데이터 모델 클래스.
 *
 * Created by juhongmin on 2020/09/24
 */
data class GalleryFilterItem(
    val id: String,
    var name: String,
    var isSelected: Boolean,
    val contentId: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other is GalleryFilterItem) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + (contentId?.hashCode() ?: 0)
        return result
    }
}