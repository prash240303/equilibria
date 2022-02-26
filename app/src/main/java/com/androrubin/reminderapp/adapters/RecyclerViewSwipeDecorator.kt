package com.androrubin.reminderapp.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSwipeDecorator private constructor() {
    private lateinit var canvas: Canvas
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewHolder: RecyclerView.ViewHolder
    private var dX: Float=0f
    private var dY: Float=0f
    private var actionState: Int = 0
    private var isCurrentlyActive: Boolean =false
    private var swipeLeftBackgroundColor = 0
    private var swipeLeftActionIconId = 0
    private var swipeLeftActionIconTint: Int? = null
    private var swipeRightBackgroundColor = 0
    private var swipeRightActionIconId = 0
    private var swipeRightActionIconTint: Int? = null
    private var iconHorizontalMargin: Int = 0
    private var mSwipeLeftText: String? = null
    private var mSwipeLeftTextSize = 14f
    private var mSwipeLeftTextUnit = TypedValue.COMPLEX_UNIT_SP
    private var mSwipeLeftTextColor = Color.DKGRAY
    private var mSwipeLeftTypeface = Typeface.SANS_SERIF
    private var mSwipeRightText: String? = null
    private var mSwipeRightTextSize = 14f
    private var mSwipeRightTextUnit = TypedValue.COMPLEX_UNIT_SP
    private var mSwipeRightTextColor = Color.DKGRAY
    private var mSwipeRightTypeface = Typeface.SANS_SERIF
    private var mSwipeLeftCornerRadius = 0f
    private var mSwipeRightCornerRadius = 0f
    private var mSwipeLeftPadding: IntArray
    private var mSwipeRightPadding: IntArray

    /**
     * Create a @RecyclerViewSwipeDecorator
     * @param context A valid Context object for the RecyclerView
     * @param canvas The canvas which RecyclerView is drawing its children
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position
     * @param dX The amount of horizontal displacement caused by user's action
     * @param dY The amount of vertical displacement caused by user's action
     * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state
     *
     */
    @Deprecated("in RecyclerViewSwipeDecorator 1.2.2")
    constructor(
        context: Context?,
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) : this(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive) {
    }

    /**
     * Create a @RecyclerViewSwipeDecorator
     * @param canvas The canvas which RecyclerView is drawing its children
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position
     * @param dX The amount of horizontal displacement caused by user's action
     * @param dY The amount of vertical displacement caused by user's action
     * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state
     */
    constructor(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) : this() {
        this.canvas = canvas
        this.recyclerView = recyclerView
        this.viewHolder = viewHolder
        this.dX = dX
        this.dY = dY
        this.actionState = actionState
        this.isCurrentlyActive = isCurrentlyActive
        iconHorizontalMargin =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                recyclerView.context.resources.displayMetrics
            )
                .toInt()
    }

    /**
     * Set the background color for either (left/right) swipe directions
     * @param backgroundColor The resource id of the background color to be set
     */
    fun setBackgroundColor(backgroundColor: Int) {
        swipeLeftBackgroundColor = backgroundColor
        swipeRightBackgroundColor = backgroundColor
    }

    /**
     * Set the action icon for either (left/right) swipe directions
     * @param actionIconId The resource id of the icon to be set
     */
    fun setActionIconId(actionIconId: Int) {
        swipeLeftActionIconId = actionIconId
        swipeRightActionIconId = actionIconId
    }

    /**
     * Set the tint color for either (left/right) action icons
     * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
     */
    fun setActionIconTint(color: Int) {
        setSwipeLeftActionIconTint(color)
        setSwipeRightActionIconTint(color)
    }

    /**
     * Set the background corner radius for either (left/right) swipe directions
     * @param unit @TypedValue the unit to convert from
     * @param size the radius value
     */
    fun setCornerRadius(unit: Int, size: Float) {
        setSwipeLeftCornerRadius(unit, size)
        setSwipeRightCornerRadius(unit, size)
    }

    /**
     * Set the background padding for either (left/right) swipe directions
     * @param unit @TypedValue the unit to convert from
     * @param top the top padding value
     * @param side the side padding value
     * @param bottom the bottom padding value
     */
    fun setPadding(unit: Int, top: Float, side: Float, bottom: Float) {
        setSwipeLeftPadding(unit, top, side, bottom)
        setSwipeRightPadding(unit, top, side, bottom)
    }

    /**
     * Set the background color for left swipe direction
     * @param swipeLeftBackgroundColor The resource id of the background color to be set
     */
    fun setSwipeLeftBackgroundColor(swipeLeftBackgroundColor: Int) {
        this.swipeLeftBackgroundColor = swipeLeftBackgroundColor
    }

    /**
     * Set the action icon for left swipe direction
     * @param swipeLeftActionIconId The resource id of the icon to be set
     */
    fun setSwipeLeftActionIconId(swipeLeftActionIconId: Int) {
        this.swipeLeftActionIconId = swipeLeftActionIconId
    }

    /**
     * Set the tint color for action icon drawn while swiping left
     * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
     */
    fun setSwipeLeftActionIconTint(color: Int) {
        swipeLeftActionIconTint = color
    }

    /**
     * Set the background color for right swipe direction
     * @param swipeRightBackgroundColor The resource id of the background color to be set
     */
    fun setSwipeRightBackgroundColor(swipeRightBackgroundColor: Int) {
        this.swipeRightBackgroundColor = swipeRightBackgroundColor
    }

    /**
     * Set the action icon for right swipe direction
     * @param swipeRightActionIconId The resource id of the icon to be set
     */
    fun setSwipeRightActionIconId(swipeRightActionIconId: Int) {
        this.swipeRightActionIconId = swipeRightActionIconId
    }

    /**
     * Set the tint color for action icon drawn while swiping right
     * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
     */
    fun setSwipeRightActionIconTint(color: Int) {
        swipeRightActionIconTint = color
    }

    /**
     * Set the label shown when swiping right
     * @param label a String
     */
    fun setSwipeRightLabel(label: String?) {
        mSwipeRightText = label
    }

    /**
     * Set the size of the text shown when swiping right
     * @param unit TypedValue (default is COMPLEX_UNIT_SP)
     * @param size the size value
     */
    fun setSwipeRightTextSize(unit: Int, size: Float) {
        mSwipeRightTextUnit = unit
        mSwipeRightTextSize = size
    }

    /**
     * Set the color of the text shown when swiping right
     * @param color the color to be set
     */
    fun setSwipeRightTextColor(color: Int) {
        mSwipeRightTextColor = color
    }

    /**
     * Set the Typeface of the text shown when swiping right
     * @param typeface the Typeface to be set
     */
    fun setSwipeRightTypeface(typeface: Typeface) {
        mSwipeRightTypeface = typeface
    }

    /**
     * Set the horizontal margin of the icon in DPs (default is 16dp)
     * @param iconHorizontalMargin the margin in pixels
     *
     */
    @Deprecated("in RecyclerViewSwipeDecorator 1.2, use {@link #setIconHorizontalMargin(int, int)} instead.")
    fun setIconHorizontalMargin(iconHorizontalMargin: Int) {
        setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_DIP, iconHorizontalMargin)
    }

    /**
     * Set the horizontal margin of the icon in the given unit (default is 16dp)
     * @param unit TypedValue
     * @param iconHorizontalMargin the margin in the given unit
     */
    fun setIconHorizontalMargin(unit: Int, iconHorizontalMargin: Int) {
        this.iconHorizontalMargin =
            TypedValue.applyDimension(
                unit,
                iconHorizontalMargin.toFloat(),
                recyclerView.context.resources.displayMetrics
            )
                .toInt()
    }

    /**
     * Set the label shown when swiping left
     * @param label a String
     */
    fun setSwipeLeftLabel(label: String?) {
        mSwipeLeftText = label
    }

    /**
     * Set the size of the text shown when swiping left
     * @param unit TypedValue (default is COMPLEX_UNIT_SP)
     * @param size the size value
     */
    fun setSwipeLeftTextSize(unit: Int, size: Float) {
        mSwipeLeftTextUnit = unit
        mSwipeLeftTextSize = size
    }

    /**
     * Set the color of the text shown when swiping left
     * @param color the color to be set
     */
    fun setSwipeLeftTextColor(color: Int) {
        mSwipeLeftTextColor = color
    }

    /**
     * Set the Typeface of the text shown when swiping left
     * @param typeface the Typeface to be set
     */
    fun setSwipeLeftTypeface(typeface: Typeface) {
        mSwipeLeftTypeface = typeface
    }

    /**
     * Set the background corner radius for left swipe direction
     * @param unit @TypedValue the unit to convert from
     * @param size the radius value
     */
    fun setSwipeLeftCornerRadius(unit: Int, size: Float) {
        mSwipeLeftCornerRadius =
            TypedValue.applyDimension(unit, size, recyclerView.context.resources.displayMetrics)
    }

    /**
     * Set the background corner radius for right swipe direction
     * @param unit @TypedValue the unit to convert from
     * @param size the radius value
     */
    fun setSwipeRightCornerRadius(unit: Int, size: Float) {
        mSwipeRightCornerRadius =
            TypedValue.applyDimension(unit, size, recyclerView.context.resources.displayMetrics)
    }

    /**
     * Set the background padding for left swipe direction
     * @param unit @TypedValue the unit to convert from
     * @param top the top padding value
     * @param right the right padding value
     * @param bottom the bottom padding value
     */
    fun setSwipeLeftPadding(unit: Int, top: Float, right: Float, bottom: Float) {
        mSwipeLeftPadding = intArrayOf(
            TypedValue.applyDimension(unit, top, recyclerView.context.resources.displayMetrics)
                .toInt(),
            TypedValue.applyDimension(unit, right, recyclerView.context.resources.displayMetrics)
                .toInt(),
            TypedValue.applyDimension(unit, bottom, recyclerView.context.resources.displayMetrics)
                .toInt()
        )
    }

    /**
     * Set the background padding for right swipe direction
     * @param unit @TypedValue the unit to convert from
     * @param top the top padding value
     * @param left the left padding value
     * @param bottom the bottom padding value
     */
    fun setSwipeRightPadding(unit: Int, top: Float, left: Float, bottom: Float) {
        mSwipeRightPadding = intArrayOf(
            TypedValue.applyDimension(unit, top, recyclerView.context.resources.displayMetrics)
                .toInt(),
            TypedValue.applyDimension(unit, left, recyclerView.context.resources.displayMetrics)
                .toInt(),
            TypedValue.applyDimension(unit, bottom, recyclerView.context.resources.displayMetrics)
                .toInt()
        )
    }

    /**
     * Decorate the RecyclerView item with the chosen backgrounds and icons
     */
    fun decorate() {
        try {
            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return
            if (dX > 0) {
                // Swiping Right
                canvas.clipRect(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.left + dX.toInt(),
                    viewHolder.itemView.bottom
                )
                if (swipeRightBackgroundColor != 0) {
                    if (mSwipeRightCornerRadius != 0f) {
                        val background = GradientDrawable()
                        background.setColor(swipeRightBackgroundColor)
                        background.setBounds(
                            viewHolder.itemView.left + mSwipeRightPadding[1],
                            viewHolder.itemView.top + mSwipeRightPadding[0],
                            viewHolder.itemView.left + dX.toInt(),
                            viewHolder.itemView.bottom - mSwipeRightPadding[2]
                        )
                        background.cornerRadii = floatArrayOf(
                            mSwipeLeftCornerRadius,
                            mSwipeLeftCornerRadius,
                            0f,
                            0f,
                            0f,
                            0f,
                            mSwipeLeftCornerRadius,
                            mSwipeLeftCornerRadius
                        )
                        background.draw(canvas)
                    } else {
                        val background = ColorDrawable(swipeRightBackgroundColor)
                        background.setBounds(
                            viewHolder.itemView.left + mSwipeRightPadding[1],
                            viewHolder.itemView.top + mSwipeRightPadding[0],
                            viewHolder.itemView.left + dX.toInt(),
                            viewHolder.itemView.bottom - mSwipeRightPadding[2]
                        )
                        background.draw(canvas)
                    }
                }
                var iconSize = 0
                if (swipeRightActionIconId != 0 && dX > iconHorizontalMargin) {
                    val icon =
                        ContextCompat.getDrawable(recyclerView.context, swipeRightActionIconId)
                    if (icon != null) {
                        iconSize = icon.intrinsicHeight
                        val halfIcon = iconSize / 2
                        val top =
                            viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - halfIcon)
                        icon.setBounds(
                            viewHolder.itemView.left + iconHorizontalMargin + mSwipeRightPadding[1],
                            top,
                            viewHolder.itemView.left + iconHorizontalMargin + mSwipeRightPadding[1] + icon.intrinsicWidth,
                            top + icon.intrinsicHeight
                        )
                        if (swipeRightActionIconTint != null) icon.setColorFilter(
                            swipeRightActionIconTint!!, PorterDuff.Mode.SRC_IN
                        )
                        icon.draw(canvas)
                    }
                }
                if (mSwipeRightText != null && mSwipeRightText!!.length > 0 && dX > iconHorizontalMargin + iconSize) {
                    val textPaint = TextPaint()
                    textPaint.isAntiAlias = true
                    textPaint.textSize = TypedValue.applyDimension(
                        mSwipeRightTextUnit,
                        mSwipeRightTextSize,
                        recyclerView.context.resources.displayMetrics
                    )
                    textPaint.color = mSwipeRightTextColor
                    textPaint.typeface = mSwipeRightTypeface
                    val textTop =
                        (viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2.0 + textPaint.textSize / 2).toInt()
                    canvas.drawText(
                        mSwipeRightText!!,
                        (viewHolder.itemView.left + iconHorizontalMargin + mSwipeRightPadding[1] + iconSize + if (iconSize > 0) iconHorizontalMargin / 2 else 0).toFloat(),
                        textTop.toFloat(),
                        textPaint
                    )
                }
            } else if (dX < 0) {
                // Swiping Left
                canvas.clipRect(
                    viewHolder.itemView.right + dX.toInt(),
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )
                if (swipeLeftBackgroundColor != 0) {
                    if (mSwipeLeftCornerRadius != 0f) {
                        val background = GradientDrawable()
                        background.setColor(swipeLeftBackgroundColor)
                        background.setBounds(
                            viewHolder.itemView.right + dX.toInt(),
                            viewHolder.itemView.top + mSwipeLeftPadding[0],
                            viewHolder.itemView.right - mSwipeLeftPadding[1],
                            viewHolder.itemView.bottom - mSwipeLeftPadding[2]
                        )
                        background.cornerRadii = floatArrayOf(
                            0f,
                            0f,
                            mSwipeLeftCornerRadius,
                            mSwipeLeftCornerRadius,
                            mSwipeLeftCornerRadius,
                            mSwipeLeftCornerRadius,
                            0f,
                            0f
                        )
                        background.draw(canvas)
                    } else {
                        val background = ColorDrawable(swipeLeftBackgroundColor)
                        background.setBounds(
                            viewHolder.itemView.right + dX.toInt(),
                            viewHolder.itemView.top + mSwipeLeftPadding[0],
                            viewHolder.itemView.right - mSwipeLeftPadding[1],
                            viewHolder.itemView.bottom - mSwipeLeftPadding[2]
                        )
                        background.draw(canvas)
                    }
                }
                var iconSize = 0
                var imgLeft = viewHolder.itemView.right
                if (swipeLeftActionIconId != 0 && dX < -iconHorizontalMargin) {
                    val icon =
                        ContextCompat.getDrawable(recyclerView.context, swipeLeftActionIconId)
                    if (icon != null) {
                        iconSize = icon.intrinsicHeight
                        val halfIcon = iconSize / 2
                        val top =
                            viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - halfIcon)
                        imgLeft =
                            viewHolder.itemView.right - iconHorizontalMargin - mSwipeLeftPadding[1] - halfIcon * 2
                        icon.setBounds(
                            imgLeft,
                            top,
                            viewHolder.itemView.right - iconHorizontalMargin - mSwipeLeftPadding[1],
                            top + icon.intrinsicHeight
                        )
                        if (swipeLeftActionIconTint != null) icon.setColorFilter(
                            swipeLeftActionIconTint!!, PorterDuff.Mode.SRC_IN
                        )
                        icon.draw(canvas)
                    }
                }
                if (mSwipeLeftText != null && mSwipeLeftText!!.length > 0 && dX < -iconHorizontalMargin - mSwipeLeftPadding[1] - iconSize) {
                    val textPaint = TextPaint()
                    textPaint.isAntiAlias = true
                    textPaint.textSize = TypedValue.applyDimension(
                        mSwipeLeftTextUnit,
                        mSwipeLeftTextSize,
                        recyclerView.context.resources.displayMetrics
                    )
                    textPaint.color = mSwipeLeftTextColor
                    textPaint.typeface = mSwipeLeftTypeface
                    val width = textPaint.measureText(mSwipeLeftText)
                    val textTop =
                        (viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2.0 + textPaint.textSize / 2).toInt()
                    canvas.drawText(
                        mSwipeLeftText!!,
                        imgLeft - width - if (imgLeft == viewHolder.itemView.right) iconHorizontalMargin else iconHorizontalMargin / 2,
                        textTop.toFloat(),
                        textPaint
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message!!)
        }
    }

    /**
     * A Builder for the RecyclerViewSwipeDecorator class
     */
    class Builder(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        private val mDecorator: RecyclerViewSwipeDecorator

        /**
         * Create a builder for a RecyclerViewsSwipeDecorator
         * @param context A valid Context object for the RecyclerView
         * @param canvas The canvas which RecyclerView is drawing its children
         * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to
         * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position
         * @param dX The amount of horizontal displacement caused by user's action
         * @param dY The amount of vertical displacement caused by user's action
         * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.
         * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state
         *
         */
        @Deprecated("in RecyclerViewSwipeDecorator 1.2.2")
        constructor(
            context: Context?,
            canvas: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) : this(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive) {
        }

        /**
         * Add a background color to both swiping directions
         * @param color A single color value in the form 0xAARRGGBB
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addBackgroundColor(color: Int): Builder {
            mDecorator.setBackgroundColor(color)
            return this
        }

        /**
         * Add an action icon to both swiping directions
         * @param drawableId The resource id of the icon to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addActionIcon(drawableId: Int): Builder {
            mDecorator.setActionIconId(drawableId)
            return this
        }

        /**
         * Set the tint color for either (left/right) action icons
         * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setActionIconTint(color: Int): Builder {
            mDecorator.setActionIconTint(color)
            return this
        }

        /**
         * Add a corner radius to swipe background for both swipe directions
         * @param unit @TypedValue the unit to convert from
         * @param size the corner radius in the given unit
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addCornerRadius(unit: Int, size: Int): Builder {
            mDecorator.setCornerRadius(unit, size.toFloat())
            return this
        }

        /**
         * Add padding to the swipe background for both swipe directions
         * @param unit @TypedValue the unit to convert from
         * @param top the top padding value
         * @param side the side padding value
         * @param bottom the bottom padding value
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addPadding(unit: Int, top: Float, side: Float, bottom: Float): Builder {
            mDecorator.setPadding(unit, top, side, bottom)
            return this
        }

        /**
         * Add a background color while swiping right
         * @param color A single color value in the form 0xAARRGGBB
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightBackgroundColor(color: Int): Builder {
            mDecorator.setSwipeRightBackgroundColor(color)
            return this
        }

        /**
         * Add an action icon while swiping right
         * @param drawableId The resource id of the icon to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightActionIcon(drawableId: Int): Builder {
            mDecorator.setSwipeRightActionIconId(drawableId)
            return this
        }

        /**
         * Set the tint color for action icon shown while swiping right
         * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeRightActionIconTint(color: Int): Builder {
            mDecorator.setSwipeRightActionIconTint(color)
            return this
        }

        /**
         * Add a label to be shown while swiping right
         * @param label The string to be shown as label
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightLabel(label: String?): Builder {
            mDecorator.setSwipeRightLabel(label)
            return this
        }

        /**
         * Set the color of the label to be shown while swiping right
         * @param color the color to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeRightLabelColor(color: Int): Builder {
            mDecorator.setSwipeRightTextColor(color)
            return this
        }

        /**
         * Set the size of the label to be shown while swiping right
         * @param unit the unit to convert from
         * @param size the size to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeRightLabelTextSize(unit: Int, size: Float): Builder {
            mDecorator.setSwipeRightTextSize(unit, size)
            return this
        }

        /**
         * Set the Typeface of the label to be shown while swiping right
         * @param typeface the Typeface to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeRightLabelTypeface(typeface: Typeface): Builder {
            mDecorator.setSwipeRightTypeface(typeface)
            return this
        }

        /**
         * Adds a background color while swiping left
         * @param color A single color value in the form 0xAARRGGBB
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftBackgroundColor(color: Int): Builder {
            mDecorator.setSwipeLeftBackgroundColor(color)
            return this
        }

        /**
         * Add an action icon while swiping left
         * @param drawableId The resource id of the icon to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftActionIcon(drawableId: Int): Builder {
            mDecorator.setSwipeLeftActionIconId(drawableId)
            return this
        }

        /**
         * Set the tint color for action icon shown while swiping left
         * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeLeftActionIconTint(color: Int): Builder {
            mDecorator.setSwipeLeftActionIconTint(color)
            return this
        }

        /**
         * Add a label to be shown while swiping left
         * @param label The string to be shown as label
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftLabel(label: String?): Builder {
            mDecorator.setSwipeLeftLabel(label)
            return this
        }

        /**
         * Set the color of the label to be shown while swiping left
         * @param color the color to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeLeftLabelColor(color: Int): Builder {
            mDecorator.setSwipeLeftTextColor(color)
            return this
        }

        /**
         * Set the size of the label to be shown while swiping left
         * @param unit the unit to convert from
         * @param size the size to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeLeftLabelTextSize(unit: Int, size: Float): Builder {
            mDecorator.setSwipeLeftTextSize(unit, size)
            return this
        }

        /**
         * Set the Typeface of the label to be shown while swiping left
         * @param typeface the Typeface to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeLeftLabelTypeface(typeface: Typeface): Builder {
            mDecorator.setSwipeLeftTypeface(typeface)
            return this
        }

        /**
         * Set the horizontal margin of the icon in DPs (default is 16dp)
         * @param pixels margin in pixels
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         *
         */
        @Deprecated("in RecyclerViewSwipeDecorator 1.2, use {@link #setIconHorizontalMargin(int, int)} instead.")
        fun setIconHorizontalMargin(pixels: Int): Builder {
            mDecorator.setIconHorizontalMargin(pixels)
            return this
        }

        /**
         * Set the horizontal margin of the icon in the given unit (default is 16dp)
         * @param unit @TypedValue the unit to convert from
         * @param iconHorizontalMargin the margin in the given unit
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setIconHorizontalMargin(unit: Int, iconHorizontalMargin: Int): Builder {
            mDecorator.setIconHorizontalMargin(unit, iconHorizontalMargin)
            return this
        }

        /**
         * Set the background corner radius for left swipe direction
         * @param unit @TypedValue the unit to convert from
         * @param size the radius value
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftCornerRadius(unit: Int, size: Float): Builder {
            mDecorator.setSwipeLeftCornerRadius(unit, size)
            return this
        }

        /**
         * Set the background corner radius for right swipe direction
         * @param unit @TypedValue the unit to convert from
         * @param size the radius value
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightCornerRadius(unit: Int, size: Float): Builder {
            mDecorator.setSwipeRightCornerRadius(unit, size)
            return this
        }

        /**
         * Add padding to the swipe background for left swipe direction
         * @param unit @TypedValue the unit to convert from
         * @param top the top padding value
         * @param right the right padding value
         * @param bottom the bottom padding value
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftPadding(unit: Int, top: Float, right: Float, bottom: Float): Builder {
            mDecorator.setSwipeLeftPadding(unit, top, right, bottom)
            return this
        }

        /**
         * Add padding to the swipe background for right swipe direction
         * @param unit @TypedValue the unit to convert from
         * @param top the top padding value
         * @param left the left padding value
         * @param bottom the bottom padding value
         *
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightPadding(unit: Int, top: Float, left: Float, bottom: Float): Builder {
            mDecorator.setSwipeRightPadding(unit, top, left, bottom)
            return this
        }

        /**
         * Create a RecyclerViewSwipeDecorator
         * @return The created @RecyclerViewSwipeDecorator
         */
        fun create(): RecyclerViewSwipeDecorator {
            return mDecorator
        }

        /**
         * Create a builder for a RecyclerViewsSwipeDecorator
         * @param canvas The canvas which RecyclerView is drawing its children
         * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to
         * @param viewHolder The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position
         * @param dX The amount of horizontal displacement caused by user's action
         * @param dY The amount of vertical displacement caused by user's action
         * @param actionState The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.
         * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state
         */
        init {
            mDecorator = RecyclerViewSwipeDecorator(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    init {
        mSwipeLeftPadding = intArrayOf(0, 0, 0)
        mSwipeRightPadding = intArrayOf(0, 0, 0)
    }
}