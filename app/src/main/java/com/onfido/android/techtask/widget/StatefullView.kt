package com.onfido.android.techtask.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.onfido.android.techtask.R
import java.util.*


typealias ViewLoadCallback = () -> Unit

/**
 * Created by gokhan.alici on 03.03.2019
 */
open class StatefulView : FrameLayout {
    private var initialized: Boolean = false
    private var requestStateBeforeDraw: String? = null

    private val mStateNames = HashSet<String>()
    protected var viewReadyCallback: ViewLoadCallback? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attachChildStatesBeforeDraw()
    }

    private fun attachChildStatesBeforeDraw() {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                IntRange(0, childCount).asSequence()
                    .map { currentIndex -> toViewState(currentIndex) }
                    .filter { it != null }
                    .filter { viewStatePair -> withoutAlreadyAddedStates(viewStatePair!!) }
                    .forEach { viewState -> addViewState(viewState!!) }

                initialized = true

                if (requestStateBeforeDraw != null)
                    changeVisibleState(requestStateBeforeDraw)

                viewReadyCallback?.invoke()

                return true
            }
        })
    }

    protected fun addViewState(viewStatePair: Pair<View, String>) {
        addViewState(viewStatePair.first, viewStatePair.second)
    }

    protected fun addViewState(view: View, state: String) {
        mStateNames.add(state)
        view.setTag(R.id.statefulViewTagId, state)
    }

    fun viewReadyCallback(viewReadyCallback: ViewLoadCallback) {
        this.viewReadyCallback = viewReadyCallback
    }

    fun changeVisibleState(state: String?) {
        if (!initialized) {
            requestStateBeforeDraw = state
            return
        }

        if (state != null && mStateNames.contains(state)) {
            IntRange(0, childCount)
                .map { currentIndex -> getChildAt(currentIndex) }
                .forEach { v -> v.visibility = View.GONE }

            val view: View? = IntRange(0, childCount)
                .map { currentIndex -> getChildAt(currentIndex) }
                .firstOrNull { view -> state == view.getTag(R.id.statefulViewTagId) }

            view?.let { it.visibility = View.VISIBLE }
        }
    }

    fun <T : View> getView(state: String): View {
        return IntRange(0, childCount)
            .map { currentIndex -> getChildAt(currentIndex) }
            .first { view -> state == view.getTag(R.id.statefulViewTagId) }
                as T
    }

    private fun toViewState(index: Int): Pair<View, String>? {
        val child = getChildAt(index)
        if (child == null || child.layoutParams !is StatefulLayoutParams)
            return null

        val state = (child.layoutParams as StatefulLayoutParams).state ?: return null

        return Pair(child, state)
    }

    private fun withoutAlreadyAddedStates(viewStatePair: Pair<View, String>): Boolean {
        return viewStatePair.second != null && !mStateNames.contains(viewStatePair.second)
    }

    override fun generateLayoutParams(attrs: AttributeSet): FrameLayout.LayoutParams {
        return StatefulLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is StatefulLayoutParams
    }

    class StatefulLayoutParams(c: Context, attrs: AttributeSet?) :
        FrameLayout.LayoutParams(c, attrs) {

        var state: String? = null

        init {
            val typedArray = c.obtainStyledAttributes(attrs, R.styleable.StatefulView)
            state = typedArray.getString(R.styleable.StatefulView_state)
            typedArray.recycle()
        }
    }
}