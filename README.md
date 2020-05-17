# ExpandLayout
可拓展缩放布局
## GIF:
<img src="https://github.com/ShowMeThe/ExpandLayout/blob/master/gif/2020517.gif" alt = "gif" width = "200"/> 

### 实现原理
在ExpandableLayout继承FrameLayout，通过requestLayout修改onMeasure的设置调整高度，控制view的上下偏移
```
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(childCount>1){
            throw IllegalStateException("childCount more than 1")
        }

        childView = children.first()
        if(parent is ExpandableParentLayout){
            patchOffset = (parent as ExpandableParentLayout).getScaleOffset()
        }


        expansionDelta = measuredHeight - (measuredHeight * expansion)
        childView.translationY = - expansionDelta
        translationY = - patchOffset
        setMeasuredDimension(measuredWidth, (measuredHeight - expansionDelta).roundToInt())
    }

```
结合ExpandableParentLayout，继承LinearLayout,实现主view的缩放和拓展view的展示，适用场景多为，拓展的view部分较小，主体view较大，展现的效果如gif</br>
实际上反过来拓展的view较大，主体view小的情况，暂未去测试，因为较少使用，特别是列表的多会采用Expandablelistview去处理，而非这个这种效果。
```
 override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(childCount > 2){
            throw IllegalStateException("childCount more than 2")
        }
        if(children.last() !is ExpandableLayout){
            throw IllegalStateException("Second ChildView should be ExpandableLayout")
        }
        mainView =  children.first()
        expandView = children.last() as ExpandableLayout

        patchOffset = mainView.measuredHeight - mainView.measuredHeight * expansion.coerceAtLeast(scaleDelta)

        mainView.scaleX = expansion.coerceAtLeast(scaleDelta)
        mainView.scaleY = expansion.coerceAtLeast(scaleDelta)
        expandView.scaleX = expansion.coerceAtLeast(scaleDelta)
    }
    //patchOffset 用于反馈偏移的大小
    
```
### 注意留意布局过度绘制问题，因为两个都是ViewGroup来的

