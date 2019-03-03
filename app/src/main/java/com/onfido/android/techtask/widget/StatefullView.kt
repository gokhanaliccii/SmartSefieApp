package com.onfido.android.techtask.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.onfido.android.techtask.R
import java.util.*

typealias ViewReadyCallback = () -> Unit

/**
 * Created by gokhan.alici on 03.03.2019
 */
open class StatefulView : FrameLayout {
    private var initialized: Boolean = false
    private var requestStateBeforeDraw: String? = null

    private val mStateNames = HashSet<String>()
    private val backAwareStates = HashSet<String>()
    private val viewStack = Stack<StateChangeHistory>()
    protected var viewReadyCallback: ViewReadyCallback? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        attrs?.let {
            processAttributes(context, attrs)
        }

        attachChildStatesBeforeDraw()
    }

    private fun processAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatefulView)
        typedArray.getString(R.styleable.StatefulView_initialState)?.let {
            viewStack.push(StateChangeHistory(it, false))
        }

        typedArray.recycle()
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

    protected fun addViewState(viewStatePair: Triple<View, String, Boolean>) {
        addViewState(viewStatePair.first, viewStatePair.second, viewStatePair.third)
    }

    protected fun addViewState(view: View, state: String, keepOnStack: Boolean = false) {
        mStateNames.add(state)
        view.setTag(R.id.statefulViewTagId, state)
        if (keepOnStack) {
            backAwareStates.add(state)
        }
    }

    fun viewReadyCallback(viewReadyCallback: ViewReadyCallback) {
        this.viewReadyCallback = viewReadyCallback
    }

    fun onBackPressed(): Boolean {
        var backConsumed = viewStack.size > 1

        if (viewStack.size > 1) {
            viewStack.pop()
            val previousHistory = viewStack.peek()
            if (previousHistory.visibilityChanged) {
                changeState(previousHistory.stateName)
            }
        }

        return backConsumed
    }

    fun latestState(): String? {
        return if (viewStack.size > 0) {
            viewStack.peek().stateName
        } else
            null
    }

    fun changeState(state: String?) {
        if (state != null && mStateNames.contains(state)) {
            if (backAwareStates.contains(state)) {
                viewStack.push(StateChangeHistory(state, false))
            }
        }
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

            view?.let {
                it.visibility = View.VISIBLE
                if (backAwareStates.contains(state)) {
                    viewStack.push(
                        StateChangeHistory(
                            state,
                            true
                        )
                    )
                }
            }
        }
    }

    fun <T : View> getView(state: String): View {
        return IntRange(0, childCount)
            .map { currentIndex -> getChildAt(currentIndex) }
            .first { view -> state == view.getTag(R.id.statefulViewTagId) }
                as T
    }

    private fun toViewState(index: Int): Triple<View, String, Boolean>? {
        val child = getChildAt(index)
        if (child == null || child.layoutParams !is StatefulLayoutParams)
            return null

        val state = (child.layoutParams as StatefulLayoutParams).state ?: return null
        val keepOnStack = (child.layoutParams as StatefulLayoutParams).keepOnStack

        return Triple(child, state, keepOnStack)
    }

    private fun withoutAlreadyAddedStates(viewStatePair: Triple<View, String, Boolean>): Boolean {
        return viewStatePair.second != null && !mStateNames.contains(viewStatePair.second)
    }

    override fun generateLayoutParams(attrs: AttributeSet): FrameLayout.LayoutParams {
        return StatefulLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(params: ViewGroup.LayoutParams): Boolean {
        return params is StatefulLayoutParams
    }

    class StatefulLayoutParams(c: Context, attrs: AttributeSet?) :
        FrameLayout.LayoutParams(c, attrs) {

        var state: String? = null
        var keepOnStack: Boolean = false

        init {
            val typedArray = c.obtainStyledAttributes(attrs, R.styleable.StatefulView)
            state = typedArray.getString(R.styleable.StatefulView_state)
            keepOnStack = typedArray.getBoolean(R.styleable.StatefulView_keepOnStack, false)
            typedArray.recycle()
        }
    }
}