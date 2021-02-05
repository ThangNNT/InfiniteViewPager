package org.nnt.infiniteviewpager

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager


class Indicator : View{
    private var activePaint: Paint = Paint()
    private var inactivePaint: Paint = Paint()

    private var activeColor = Color.WHITE
    private var inactiveColor = Color.LTGRAY

    private var activeWidth = 100
    private var inactiveWidth = 25

    /** the number of indicator item **/
    private var pageCount = 0
    /**---**/

    private var selectedPosition = 3
    private var indicatorHeight =  20

    private var activeOpacity = 1F
    private var inactiveOpacity = 1F

    /**the spacing between indicator items **/
    private var spacing = 20


    /** define radius corner of the indicator item **/
    private var radiusX = 10
    private var radiusY = 10
    /** **/

    private var viewPager: ViewPager? = null
    private var viewPagerId= -1

    private var rectArray: ArrayList<RectF> = ArrayList()

    private var isHaveListener = false

    /** animation type **/
    private var animationType = AnimationType.SCALE

    constructor(context: Context) : super(context) {
        val typedArray = context.obtainStyledAttributes(R.styleable.Indicator)
        getAttribute(typedArray)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Indicator, defStyle, 0)
        getAttribute(typedArray)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Indicator)
        getAttribute(typedArray)
    }

    private fun getAttribute(typedArray: TypedArray){
        activeColor = typedArray.getColor(R.styleable.Indicator_nnt_activeColor, activeColor)
        inactiveColor = typedArray.getColor(R.styleable.Indicator_nnt_inactiveColor, inactiveColor)
        indicatorHeight =typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_indicatorHeight, indicatorHeight)
        activeWidth =typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_activeWidth, activeWidth)
        inactiveWidth =typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_inactiveWidth, inactiveWidth)
        spacing = typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_spacing, spacing)
        radiusX = typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_corner_radiusX, radiusX)
        radiusY = typedArray.getDimensionPixelSize(R.styleable.Indicator_nnt_corner_radiusY, radiusY)
        activeOpacity = typedArray.getFloat(R.styleable.Indicator_nnt_activeOpacity, activeOpacity)
        inactiveOpacity = typedArray.getFloat(R.styleable.Indicator_nnt_inactiveOpacity, inactiveOpacity)

        viewPagerId = typedArray.getResourceId(R.styleable.Indicator_nnt_viewPager, -1)
        init()
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(viewPagerId!=-1){
            viewPager = (parent as View).findViewById(viewPagerId)
        }
        viewPager?.let {
            setViewPager(it)
        }
    }

    private fun init(){
        activePaint.color = activeColor
        activePaint.alpha = ((activeOpacity*255F).toInt())
        activePaint.isAntiAlias = true
        inactivePaint.color = inactiveColor
        inactivePaint.alpha =((inactiveOpacity*255F).toInt())
        inactivePaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desireWidth = paddingLeft+ paddingRight + activeWidth + (pageCount-1)*inactiveWidth + spacing*(pageCount-1)
        val desireHeight = paddingTop + paddingBottom + indicatorHeight
        setMeasuredDimension(desireWidth, desireHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (pageCount >1) {
            for(i in 0 until pageCount){
                val rectF = rectArray[i]
                if (i == selectedPosition){
                    canvas?.drawRoundRect(rectF, radiusX.toFloat(), radiusY.toFloat(), activePaint)
                }
                else {
                    canvas?.drawRoundRect(rectF, radiusX.toFloat(), radiusY.toFloat(), inactivePaint)

                }
            }
        }
    }
    fun radius(radiusX: Int, radiusY: Int){
        this.radiusX = radiusX
        this.radiusY = radiusY
        invalidate()
    }
    fun setActiveColor(color: Int){
        activeColor = color
        activePaint.color = color
        invalidate()
    }
    fun setInactiveColor(color: Int){
        inactiveColor = color
        inactivePaint.color = color
        invalidate()
    }
    fun setIndicatorSpacing(spacing: Int){
        this.spacing = spacing
        updateWhenAnySizeChanged()
        requestLayout()
    }

    private var tempPositionOffset = 0F
    private var isSwipingLeft = false
    private var isSwipingRight = false


    private val pagerChangerListener= object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            this@Indicator.viewPager?.adapter?.let {
                if(it is InfinitePagerAdapter){
                    if(positionOffset<tempPositionOffset && positionOffset !=0F && tempPositionOffset!=0F){
                        //swipe to right
                        isSwipingRight = true
                        isSwipingLeft = false
                        setRectArrayWhenPositionOffsetChange(it.getRealPosition(position), positionOffset)
                        invalidate()
                    }
                    else if(positionOffset>tempPositionOffset && positionOffset !=0F && tempPositionOffset!=0F){
                        //swipe to left
                        isSwipingLeft = true
                        isSwipingRight = false
                        setRectArrayWhenPositionOffsetChange(it.getRealPosition(position), positionOffset)
                        invalidate()
                    }
                    else if(positionOffset==0F){
                        isSwipingLeft = false
                        isSwipingRight = false
                        selectedPosition = it.getRealPosition(position)
                        updateWhenAnySizeChanged()
                        invalidate()
                    }
                    tempPositionOffset = positionOffset
                }
                else {
                    if(positionOffset<tempPositionOffset && positionOffset !=0F && tempPositionOffset!=0F){
                        //swipe to right
                        isSwipingRight = true
                        isSwipingLeft = false
                        setRectArrayWhenPositionOffsetChange(position, positionOffset)
                        invalidate()
                    }
                    else if(positionOffset>tempPositionOffset && positionOffset !=0F && tempPositionOffset!=0F){
                        //swipe to left
                        isSwipingLeft = true
                        isSwipingRight = false
                        setRectArrayWhenPositionOffsetChange(position, positionOffset)
                        invalidate()
                    }
                    else if(positionOffset==0F){
                        isSwipingLeft = false
                        isSwipingRight = false
                        selectedPosition = position
                        updateWhenAnySizeChanged()
                        invalidate()
                    }
                    tempPositionOffset = positionOffset
                }
            }
        }

        override fun onPageSelected(position: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    private val adapterChangeListener =
        ViewPager.OnAdapterChangeListener { _, _, newAdapter ->
            newAdapter?.let {
                if(it is InfinitePagerAdapter){
                    this@Indicator.pageCount = it.getRealCount()
                    selectedPosition = 0
                } else {
                    this@Indicator.pageCount = it.count
                    selectedPosition = 0
                }
                var left = paddingLeft.toFloat()
                val top = paddingTop.toFloat()
                rectArray.clear()
                for (i in 0 until this@Indicator.pageCount){
                    if(i !=0){
                        left +=spacing
                    }
                    val rectF = RectF()
                    if (i == selectedPosition){
                        rectF.set(left, top, left + activeWidth, top + indicatorHeight)
                        left += activeWidth
                    } else {
                        rectF.set(left, top, left + inactiveWidth, top + indicatorHeight)
                        left += inactiveWidth
                    }
                    rectArray.add(rectF)
                }
                requestLayout()
            }
        }
    fun setViewPager(viewPager: ViewPager){
        if (isHaveListener) {
            this.viewPager?.removeOnPageChangeListener(pagerChangerListener)
            this.viewPager?.removeOnAdapterChangeListener(adapterChangeListener)
        }
        this.viewPager = viewPager
        this.viewPager?.adapter?.let {
            if(it is InfinitePagerAdapter){
                this.pageCount = it.getRealCount()
                selectedPosition = 0
            }
            else {
                this.pageCount = it.count
                selectedPosition = 0
            }
            var left = paddingLeft.toFloat()
            val top = paddingTop.toFloat()
            rectArray.clear()
            for (i in 0 until this.pageCount){
                if(i !=0){
                    left +=spacing
                }
                val rectF = RectF()
                left += if (i == selectedPosition){
                    rectF.set(left, top, left + activeWidth, top + indicatorHeight)
                    activeWidth
                } else {
                    rectF.set(left, top, left + inactiveWidth, top + indicatorHeight)
                    inactiveWidth
                }
                rectArray.add(rectF)
            }
            requestLayout()
        }
        this.viewPager?.addOnPageChangeListener(pagerChangerListener)
        this.viewPager?.addOnAdapterChangeListener(adapterChangeListener)
        isHaveListener = true
    }
    fun setActiveWidth(activeWidth: Int){
        this.activeWidth = activeWidth
        updateWhenAnySizeChanged()
        requestLayout()
    }
    fun setInactiveWidth(inactiveWidth: Int){
        this.inactiveWidth = inactiveWidth
        updateWhenAnySizeChanged()
        requestLayout()
    }
    fun setActiveOpacity(opacity: Float){
        this.activeOpacity = opacity
        activePaint.alpha = ((activeOpacity*100).toInt())
        invalidate()
    }
    fun setInactiveOpacity(opacity: Float){
        this.inactiveOpacity = opacity
        inactivePaint.alpha = ((inactiveOpacity*100).toInt())
        invalidate()
    }
    private fun updateWhenAnySizeChanged(){
        var left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        for (i in 0 until this.pageCount){
            val rectF = rectArray[i]
            if(i !=0){
                left +=spacing
            }
            left += if (i == selectedPosition){
                rectF.set(left, top, left + activeWidth, top + indicatorHeight)
                activeWidth
            } else {
                rectF.set(left, top, left + inactiveWidth, top + indicatorHeight)
                inactiveWidth
            }
        }
    }
    private fun increasePosition(position: Int): Int{
        return if (position == pageCount - 1) {
            0
        } else {
            position + 1
        }
    }
    private fun decreasePosition(position: Int): Int{
        return if (position == 0) {
            pageCount - 1
        } else {
            position - 1
        }
    }

    private fun setRectArrayWhenPositionOffsetChange(position: Int, positionOffset: Float){
        var realPosition = position
        var nextPosition = 0
        var nextPositionWidthIncreasePercent = 0F
        if (isSwipingLeft) {
            nextPosition = increasePosition(realPosition)
            nextPositionWidthIncreasePercent = positionOffset
            if (nextPositionWidthIncreasePercent>=0.5&& selectedPosition==realPosition){
                selectedPosition = increasePosition(selectedPosition)
            }
            else if(nextPositionWidthIncreasePercent<0.5&& selectedPosition==realPosition+1)
            {
                selectedPosition = decreasePosition(selectedPosition)
            }
        } else if(isSwipingRight){
            nextPosition = position
            realPosition = increasePosition(realPosition)
            nextPositionWidthIncreasePercent = 1 - positionOffset
            if (nextPositionWidthIncreasePercent>=0.5&& selectedPosition==realPosition){
                selectedPosition = decreasePosition(selectedPosition)
            }
            else if(nextPositionWidthIncreasePercent<0.5&& selectedPosition==realPosition-1)
            {
                selectedPosition = increasePosition(selectedPosition)
            }
        }
        when(animationType){
            AnimationType.NONE ->{

            }
            AnimationType.WORM->{

            }
            AnimationType.THIN_WORM->{

            }
            AnimationType.SLIDE->{

            }
            AnimationType.SWAP->{

            }
            AnimationType.SCALE ->{
                setScaleAnimation(realPosition, nextPosition, nextPositionWidthIncreasePercent)
            }
        }
    }


    private fun setScaleAnimation(realPosition: Int,nextPosition: Int , nextPositionWidthIncreasePercent: Float){
        var left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        for (i in rectArray.indices){
            val rectF = rectArray[i]
            if(i !=0){
                left +=spacing
            }
            when (i) {
                realPosition -> {
                    val width = inactiveWidth+(activeWidth-inactiveWidth)*(1-nextPositionWidthIncreasePercent)
                    rectF.set(left, top, left + width, top + indicatorHeight)
                    left += width
                }
                nextPosition -> {
                    val width = inactiveWidth+(activeWidth-inactiveWidth)*nextPositionWidthIncreasePercent
                    rectF.set(left, top, left + width, top + indicatorHeight)
                    left += width
                }
                else -> {
                    rectF.set(left, top, left + inactiveWidth, top + indicatorHeight)
                    left += inactiveWidth
                }
            }
        }
    }


    enum class AnimationType{
        NONE,
        WORM,
        THIN_WORM,
        SLIDE,
        SWAP,
        SCALE
    }
}