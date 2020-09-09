@file:Suppress("UNCHECKED_CAST")

package com.hmju.memo.widget.bottomSheetBehavior

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.view.*
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.view.ViewCompat.ScrollAxis
import androidx.customview.view.AbsSavedState
import androidx.customview.widget.ViewDragHelper
import com.hmju.memo.R
import com.hmju.memo.utils.JLogger
import java.lang.ref.WeakReference

/**
 * Description : Custom Bottom Sheet Behavior Class
 * BottomSheetBehavior 기반으로 만듬.
 *
 * Created by juhongmin on 2020/09/07
 */
open class CustomBottomSheetBehavior<V : View?>(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<V?>(context, attrs) {

    interface BottomSheetListener {
        fun onStateChanged(bottomSheet: View, @State newState: Int)
        fun onSlide(bottomSheet: View, offset: Float)
    }

    @IntDef(
        STATE_DRAGGING,
        STATE_SETTLING,
        STATE_EXPANDED,
        STATE_COLLAPSED,
        STATE_HIDDEN,
        STATE_HALF_EXPANDED,
        STATE_FORCE_HIDDEN
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class State

    private var mHalfExpandedRatio = 0.50f // 중간 펼처짐 비율
    private val mMaximumVelocity: Float
    private var mPeekHeight = 0
    private var mMinOffset = 0
    private var mMaxOffset = 0
    private var mHalfExpandedOffset = 0
    private var mHideable = false
    private var mSkipCollapsed = false
    private var mSkipHalfExpand = false

    @State
    private var mState = STATE_COLLAPSED
    private var mViewDragHelper: ViewDragHelper? = null
    private var mIgnoreEvents = false
    private var mLastNestedScrollDy = 0
    private var mNestedScrolled = false
    private var mParentHeight = 0
    private var mViewRef: WeakReference<V>? = null
    private var mNestedScrollingChildRef: WeakReference<View?>? = null
    private var mListener: BottomSheetListener? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mActivePointerId = 0
    private var mInitialY = 0
    private var mTouchingScrollingChild = false

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: V): Parcelable {
        return SavedState(super.onSaveInstanceState(parent, child), mState)
    }

    override fun onRestoreInstanceState(
        parent: CoordinatorLayout, child: V, state: Parcelable
    ) {
        val ss = state as SavedState
        super.onRestoreInstanceState(parent, child, ss.superState!!)
        // Intermediate states are restored as collapsed state
        mState = if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            STATE_COLLAPSED
        } else {
            ss.state
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout, child: V, layoutDirection: Int
    ): Boolean {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child!!)) {
            child.fitsSystemWindows = true
        }
        val savedTop = child!!.top
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection)
        // Offset the bottom sheet
        mParentHeight = parent.height
        mMinOffset = Math.max(0, mParentHeight - child.height)
        mMaxOffset = Math.max(mParentHeight - mPeekHeight, mMinOffset)
        // HALF EXPAND 값 설정.
        mHalfExpandedOffset = if (mSkipHalfExpand) {
            mParentHeight + mPeekHeight /*1000*/
        } else {
            Math.max(mParentHeight * mHalfExpandedRatio, mMinOffset.toFloat()).toInt()
        }
        JLogger.d("onLayoutChild saveTop\t$savedTop")
        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset)
        } else if (mState == STATE_HALF_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mHalfExpandedOffset)
        } else if (mHideable && mState == STATE_HIDDEN || mState == STATE_FORCE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, mParentHeight)
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset)
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.top)
        }
        // init mViewDragHelper
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback)
        }
        mViewRef = WeakReference(child)
        mNestedScrollingChildRef = WeakReference(findScrollingChild(child))
        return true
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V, event: MotionEvent
    ): Boolean {
        if (!child!!.isShown) {
            mIgnoreEvents = true
            return false
        }
        val action = event.actionMasked
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mTouchingScrollingChild = false
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false
                    return false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                val initialX = event.x.toInt()
                mInitialY = event.y.toInt()
                val scroll =
                    if (mNestedScrollingChildRef != null) mNestedScrollingChildRef!!.get() else null
                if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                    mActivePointerId = event.getPointerId(event.actionIndex)
                    mTouchingScrollingChild = true
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, initialX, mInitialY)
            }
        }
        if (!mIgnoreEvents
            && mViewDragHelper!!.shouldInterceptTouchEvent(event)
        ) {
            return true
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        val scroll = mNestedScrollingChildRef!!.get()
        return (action == MotionEvent.ACTION_MOVE && scroll != null && !mIgnoreEvents
                && mState != STATE_DRAGGING && !parent.isPointInChildBounds(
            scroll,
            event.x.toInt(),
            event.y.toInt()
        )
                && Math.abs(mInitialY - event.y) > mViewDragHelper!!.touchSlop)
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout, child: V, event: MotionEvent
    ): Boolean {
        if (!child!!.isShown || mViewDragHelper == null) {
            return false
        }
        val action = event.actionMasked
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true
        }
        mViewDragHelper!!.processTouchEvent(event)
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
            if (Math.abs(mInitialY - event.y) > mViewDragHelper!!.touchSlop) {
                mViewDragHelper!!.captureChildView(child, event.getPointerId(event.actionIndex))
            }
        }
        return !mIgnoreEvents
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        @ScrollAxis nestedScrollAxes: Int,
        @NestedScrollType type: Int
    ): Boolean {
        mLastNestedScrollDy = 0
        mNestedScrolled = false
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    // NSV Scroll Event CallBack.
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        @NestedScrollType type: Int
    ) {
        val scrollingChild = mNestedScrollingChildRef!!.get()
        JLogger.d("NSVPreScroll Top\t" + child!!.top + "\tScrollOffset\t" + dy + "\tState\t" + mState)
        if (target !== scrollingChild) {
            return
        }
        val currentTop = child.top
        val newTop = currentTop - dy
        if (dy > 0) { // Upward
            if (newTop < mMinOffset) {
                consumed[1] = currentTop - mMinOffset
                ViewCompat.offsetTopAndBottom(child, -consumed[1])
                setStateInternal(STATE_EXPANDED)
            } else {
                consumed[1] = dy
                ViewCompat.offsetTopAndBottom(child, -dy)
                setStateInternal(STATE_DRAGGING)
            }
        } else if (dy < 0) { // Downward
            if (!target.canScrollVertically(-1)) {
                if (newTop <= mMaxOffset || mHideable) {
                    consumed[1] = dy
                    ViewCompat.offsetTopAndBottom(child, -dy)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[1] = currentTop - mMaxOffset
                    ViewCompat.offsetTopAndBottom(child, -consumed[1])
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        }
        dispatchOnSlide(child.top)
        mLastNestedScrollDy = dy
        mNestedScrolled = true
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        @NestedScrollType type: Int
    ) {
        // Child -> layout_behavior 세팅한 View.
        JLogger.d("StopNSVScroll Top\t" + child!!.top + "\tLastScrollOffset\t" + mLastNestedScrollDy + "\tState\t" + mState)
        if (child.top == mMinOffset) {
            setStateInternal(STATE_EXPANDED)
            return
        }
        if (mNestedScrollingChildRef == null || target !== mNestedScrollingChildRef!!.get() || !mNestedScrolled) {
            return
        }
        val top: Int
        var targetState: Int
        if (mLastNestedScrollDy > 0) { // Scroll Up
            val currentTop = child.top
            if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mHalfExpandedOffset)) {
                top = mMinOffset
                targetState = STATE_EXPANDED
            } else {
                top = mHalfExpandedOffset
                targetState = STATE_HALF_EXPANDED
            }
        } else if (mHideable && shouldHide(child, getYVelocity())) {
            top = mParentHeight
            targetState = STATE_HIDDEN
        } else if (mLastNestedScrollDy == 0) {
            val currentTop = child.top
            if (Math.abs(currentTop - mHalfExpandedOffset) < Math.abs(currentTop - mMinOffset)) {
                top = mHalfExpandedOffset
                targetState = STATE_HALF_EXPANDED
            } else if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                top = mMinOffset
                targetState = STATE_EXPANDED
            } else {
                top = mMaxOffset
                targetState = STATE_COLLAPSED
            }
        } else {
            val currentTop = child.top
            if (Math.abs(currentTop - mHalfExpandedOffset) < Math.abs(currentTop - mMaxOffset)) {
                top = mHalfExpandedOffset
                targetState = STATE_HALF_EXPANDED
            } else {
                top = mMaxOffset
                targetState = STATE_COLLAPSED
            }
        }
        if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState))
        } else {
            JLogger.d("onStopNestedScroll TargetState\t$targetState")
            if (mSkipHalfExpand && targetState == STATE_HALF_EXPANDED) {
                targetState = STATE_EXPANDED
            }
            setStateInternal(targetState)
        }
        mNestedScrolled = false
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val isFling = target === mNestedScrollingChildRef!!.get() &&
                (mState != STATE_EXPANDED ||
                        super.onNestedPreFling(
                            coordinatorLayout, child, target,
                            velocityX, velocityY
                        ))
        JLogger.d("onNestedPreFling Fling$isFling")
        return isFling
    }

    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels.
     */
    fun setPeekHeight(peekHeight: Int) {
        var layout = false
        if (mPeekHeight != peekHeight) {
            mPeekHeight = Math.max(0, peekHeight)
            mMaxOffset = mParentHeight - peekHeight
            layout = true
        }
        if (layout && mState == STATE_COLLAPSED && mViewRef != null) {
            val view = mViewRef!!.get()
            view?.requestLayout()
        }
    }

    fun getPeekHeight(): Int {
        return mPeekHeight
    }

    /**
     * Gets the offset from the panel till the top
     *
     * @return the offset in pixel size
     */
    fun getPanelOffset(): Int {
        if (mState == STATE_EXPANDED) {
            return mMinOffset
        } else if (mState == STATE_HALF_EXPANDED) {
            return mHalfExpandedOffset
        } else if (mHideable && mState == STATE_HIDDEN) {
            return mParentHeight
        }
        return mMaxOffset
    }

    /**
     * Get the size in pixels from the anchor state to the top of the parent (Expanded state)
     *
     * @return pixel size of the anchor state
     */
    fun getAnchorOffset(): Int {
        return mHalfExpandedOffset
    }

    /**
     * The multiplier between 0..1 to calculate the Anchor offset
     *
     * @return float between 0..1
     */
    fun getAnchorThreshold(): Float {
        return mHalfExpandedRatio
    }

    /**
     * Set the offset for the anchor state. Number between 0..1
     * i.e: Anchor the panel at 1/3 of the screen: setAnchorOffset(0.25)
     *
     * @param threshold [Float] from 0..1
     */
    fun setAnchorOffset(threshold: Float) {
        mHalfExpandedRatio = threshold
        mHalfExpandedOffset =
            Math.max(mParentHeight * mHalfExpandedRatio, mMinOffset.toFloat()).toInt()
    }

    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable `true` to make this bottom sheet hideable.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_hideable
     */
    fun setHideable(hideable: Boolean) {
        mHideable = hideable
    }

    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return `true` if this bottom sheet can hide.
     * @attr ref android.support.design.R.styleable#AnchorBehavior_Params_behavior_hideable
     */
    fun isHideable(): Boolean {
        return mHideable
    }

    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once. Setting this to true has no effect unless the sheet is hideable.
     *
     * @param skipCollapsed True if the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    fun setSkipCollapsed(skipCollapsed: Boolean) {
        mSkipCollapsed = skipCollapsed
    }

    fun setSkipHalfExpand(isValue: Boolean) {
        mSkipHalfExpand = isValue
    }

    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once.
     *
     * @return Whether the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    fun getSkipCollapsed(): Boolean {
        return mSkipCollapsed
    }

    /**
     * Sets a callback to be notified of bottom sheet events.
     *
     * @param listener The callback to notify when bottom sheet events occur.
     */
    fun setListener(listener: BottomSheetListener?) {
        mListener = listener
    }

    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of [.STATE_COLLAPSED], [.STATE_EXPANDED], or
     * [.STATE_HIDDEN].
     */
    fun setState(@State state: Int) {
        if (state == mState) {
            return
        }
        if (mViewRef == null) {
            // The view is not laid out yet; modify mState and let onLayoutChild handle it later
            if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_HALF_EXPANDED ||
                mHideable && state == STATE_HIDDEN || state == STATE_FORCE_HIDDEN
            ) {
                mState = state
            }
            return
        }
        val child = mViewRef!!.get() ?: return
        // Start the animation; wait until a pending layout if there is one.
        val parent = child.parent
        if (parent != null && parent.isLayoutRequested && ViewCompat.isAttachedToWindow(child)) {
            child.post { startSettlingAnimation(child, state) }
        } else {
            startSettlingAnimation(child, state)
        }
    }

    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of [.STATE_EXPANDED], [.STATE_COLLAPSED], [.STATE_DRAGGING],
     * and [.STATE_SETTLING].
     */
    @State
    fun getState(): Int {
        return mState
    }

    private fun setStateInternal(@State state: Int) {
        if (mState == state) {
            return
        }
        mState = state
        val bottomSheet: View? = mViewRef!!.get()
        if (bottomSheet != null && mListener != null) {
            mListener!!.onStateChanged(bottomSheet, state)
        }
    }

    private fun reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    fun shouldHide(child: View, yvel: Float): Boolean {
        if (mSkipCollapsed) {
            return true
        }
        if (child.top < mMaxOffset) {
            // It should not hide, but collapse.
            return false
        }
        val newTop = child.top + yvel * HIDE_FRICTION
        return Math.abs(newTop - mMaxOffset) / mPeekHeight.toFloat() > HIDE_THRESHOLD
    }

    @VisibleForTesting
    fun findScrollingChild(view: View?): View? {
        if (ViewCompat.isNestedScrollingEnabled(view!!)) {
            return view
        }
        if (view is ViewGroup) {
            val group = view
            var i = 0
            val count = group.childCount
            while (i < count) {
                val scrollingChild = findScrollingChild(group.getChildAt(i))
                if (scrollingChild != null) {
                    return scrollingChild
                }
                i++
            }
        }
        return null
    }

    private fun getYVelocity(): Float {
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity)
        return mVelocityTracker!!.getYVelocity(mActivePointerId)
    }

    fun startSettlingAnimation(child: View, state: Int) {
        var state = state
        JLogger.d("startSettlingAnimation $state")
        val top: Int
        top = if (state == STATE_HALF_EXPANDED) {
            mHalfExpandedOffset
        } else if (state == STATE_COLLAPSED) {
            mMaxOffset
        } else if (state == STATE_EXPANDED) {
            mMinOffset
        } else if (mHideable && state == STATE_HIDDEN || state == STATE_FORCE_HIDDEN) {
            mParentHeight
        } else {
            throw IllegalArgumentException("Illegal state argument: $state")
        }
        if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, state))
        } else {
            JLogger.d("startSettlingAnimation State\t$state")
            if (mSkipHalfExpand && state == STATE_HALF_EXPANDED) {
                state = STATE_EXPANDED
            }
            setStateInternal(state)
        }
    }

    private val mDragCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        // 특정 뷰에 대해서 컨트롤 하고 싶을때 return 값 true 로 하면된다.
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
            //            JLogger.d("tryCaptureView\t" + mState);
//            if (mState == STATE_DRAGGING) {
//                return false;
//            }
//            if (mTouchingScrollingChild) {
//                return false;
//            }
//            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
//                View scroll = mNestedScrollingChildRef.get();
//                if (scroll != null && scroll.canScrollVertically(-1)) {
//                    // Let the content scroll up
//                    return false;
//                }
//            }
//            return mViewRef != null && mViewRef.get() == child;
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            JLogger.d("onViewPositionChanged Top\t$top")
            if (mParentHeight < top) {
                JLogger.d("TEST:: 사리진다!!")
                setStateInternal(STATE_HIDDEN)
            } else {
                dispatchOnSlide(top)
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            JLogger.d("onViewDragStateChanged State \t$state")
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            JLogger.d("onViewReleased\t" + yvel + "\tTop\t" + releasedChild.top)
            val top: Int
            @State var targetState: Int
            if (yvel < 0) { // Moving up
                val currentTop = releasedChild.top
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mHalfExpandedOffset)) {
                    top = mMinOffset
                    targetState = STATE_EXPANDED
                } else {
                    top = mHalfExpandedOffset
                    targetState = STATE_HALF_EXPANDED
                }
            } else if (mHideable && shouldHide(releasedChild, yvel)) {
                top = mParentHeight
                targetState = STATE_HIDDEN
            } else if (yvel == 0f) {
                val currentTop = releasedChild.top
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mHalfExpandedOffset)) {
                    top = mMinOffset
                    targetState = STATE_EXPANDED
                } else if (Math.abs(currentTop - mHalfExpandedOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mHalfExpandedOffset
                    targetState = STATE_HALF_EXPANDED
                } else {
                    top = mMaxOffset
                    targetState = STATE_COLLAPSED
                }
            } else {
                top = mMaxOffset
                targetState = STATE_COLLAPSED
            }
            if (mViewDragHelper!!.settleCapturedViewAt(releasedChild.left, top)) {
                JLogger.d("onViewReleased Setting?\t$targetState")
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(
                    releasedChild,
                    SettleRunnable(releasedChild, targetState)
                )
            } else {
                JLogger.d("onViewReleased TargetState\t$targetState")
                if (mSkipHalfExpand && targetState == STATE_HALF_EXPANDED) {
                    targetState = STATE_EXPANDED
                }
                setStateInternal(targetState)
            }
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            JLogger.d("clampViewPositionVertical Top $top\tDy\t$dy")
            return MathUtils.clamp(top, mMinOffset, if (mHideable) mParentHeight else mMaxOffset)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return if (mHideable) {
                mParentHeight - mMinOffset
            } else {
                mMaxOffset - mMinOffset
            }
        }
    }

    fun dispatchOnSlide(top: Int) {
        val bottomSheet: View? = mViewRef!!.get()
        if (bottomSheet != null && mListener != null) {
            if (top > mMaxOffset) {
                mListener!!.onSlide(
                    bottomSheet, (mMaxOffset - top).toFloat() /
                            (mParentHeight - mMaxOffset)
                )
            } else {
                mListener!!.onSlide(
                    bottomSheet,
                    (mMaxOffset - top).toFloat() / (mMaxOffset - mMinOffset)
                )
            }
        }
    }

    private inner class SettleRunnable internal constructor(
        private val mView: View,
        @field:State @param:State private val mTargetState: Int
    ) :
        Runnable {
        override fun run() {
            if (mViewDragHelper != null && mViewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this)
            } else {
                setStateInternal(mTargetState)
            }
        }
    }

    protected class SavedState : AbsSavedState {
        @State
        val state: Int

        @JvmOverloads
        constructor(source: Parcel, loader: ClassLoader? = null) : super(source, loader) {
            state = source.readInt()
        }

        constructor(superState: Parcelable?, @State state: Int) : super(
            superState!!
        ) {
            this.state = state
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : ClassLoaderCreator<SavedState> {
                override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState {
                    return SavedState(`in`, loader)
                }

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`, null)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        fun <V : View?> from(view: V): CustomBottomSheetBehavior<V> {
            val params = view!!.layoutParams
            require(params is CoordinatorLayout.LayoutParams) { "The view is not a child of CoordinatorLayout" }
            val behavior = params.behavior
            require(behavior is CustomBottomSheetBehavior<*>) { "The view is not associated with AnchorSheetBehavior" }
            return behavior as CustomBottomSheetBehavior<V>
        }

        const val STATE_DRAGGING = 1 // Bottom Sheet Drag
        const val STATE_SETTLING = 2 // Bottom Sheet Setting
        const val STATE_EXPANDED = 3 // Bottom Sheet Expand
        const val STATE_COLLAPSED = 4 // Bottom Sheet Collapsed
        const val STATE_HIDDEN = 5 // Bottom Sheet Hide
        const val STATE_HALF_EXPANDED = 6 // Bottom Sheet Half Expand
        const val STATE_FORCE_HIDDEN =
            7 // The bottom sheet is forced to be hidden programmatically.
        private const val HIDE_THRESHOLD = 0.5f
        private const val HIDE_FRICTION = 0.1f
    }

    /**
     * Default constructor for inflating AnchorSheetBehavior from layout.
     *
     * @param context The [Context].
     * @param attrs   The [AttributeSet].
     */
    init {
        if (attrs != null) {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomSheetBehavior)
            setPeekHeight(
                attr.getDimensionPixelOffset(
                    R.styleable.CustomBottomSheetBehavior_bottomSheet_peekHeight,
                    0
                )
            )
            setHideable(
                attr.getBoolean(
                    R.styleable.CustomBottomSheetBehavior_bottomSheet_hideable,
                    false
                )
            )
            setSkipCollapsed(
                attr.getBoolean(
                    R.styleable.CustomBottomSheetBehavior_bottomSheet_skipCollapsed,
                    false
                )
            )
            setSkipHalfExpand(
                attr.getBoolean(
                    R.styleable.CustomBottomSheetBehavior_bottomSheet_skipHalfExpand,
                    false
                )
            )
            attr.recycle()
        }
        val configuration = ViewConfiguration.get(context)
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()
    }
}