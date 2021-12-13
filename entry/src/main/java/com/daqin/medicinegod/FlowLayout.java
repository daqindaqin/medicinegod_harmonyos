package com.daqin.medicinegod;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义流式布局
 *
 * @author 裴云飞
 * @date 2021/5/5
 */
public class FlowLayout extends ComponentContainer implements Component.EstimateSizeListener, ComponentContainer.ArrangeListener {

    /**
     * 在onArrange方法里面需要通过每行的高度来确定每个子组件的摆放位置，这里创建一个存储每行高度的集合
     */
    private final List<Integer> lineHeight = new ArrayList<>();
    /**
     * 在onArrange方法里面需要获取到流式布局中所有的子组件，这里创建一个集合，用于存储流式布局中每一行的子组件，一行一行的存储
     */
    private final List<List<Component>> listLineComponent = new ArrayList<>();

    /**
     * 流式布局的宽度
     */
    private int mEstimateFlowLayoutWidth = 0;
    /**
     * 流式布局布局高度
     */
    private int mEstimateFlowLayoutHeight = 0;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttrSet attrSet) {
        this(context, attrSet, "");
    }

    public FlowLayout(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        // 让onEstimateSize方法能够执行
        setEstimateSizeListener(this);
        // 让onArrange方法能够执行
        setArrangeListener(this);
    }

    /**
     * 测量流式布局的宽高，由于是自定义布局，所以不仅需要测量流式布局自身的宽高，还需要测量子组件的宽高
     *
     * @param widthEstimatedConfig 父组件提供给流式布局的宽度的测量规格
     * @param heightEstimatedConfig 父组件提供给流式布局的宽度的测量规格
     * @return 调用setEstimatedSize方法来将测量好宽高传递给父组件，并且返回true让测量的宽高生效
     */
    @Override
    public boolean onEstimateSize(int widthEstimatedConfig, int heightEstimatedConfig) {
        // 得到宽度的测量模式
        int widthMode = EstimateSpec.getMode(widthEstimatedConfig);
        // 得到高度的测量模式
        int heightMode = EstimateSpec.getMode(heightEstimatedConfig);
        // 得到宽度的测量大小
        int width = EstimateSpec.getSize(widthEstimatedConfig);
        // 得到高度的测量大小
        int height = EstimateSpec.getSize(heightEstimatedConfig);
        // 宽高都是精确的模式
        if (widthMode == EstimateSpec.PRECISE && heightMode == EstimateSpec.PRECISE) {
            // 此时流式布局的宽高就是调用EstimateSpec.getSize方法得到的数值
            mEstimateFlowLayoutWidth = width;
            mEstimateFlowLayoutHeight =  height;
            // 在精确模式下测量子组件的宽高
            estimateChildByPrecise(width, widthEstimatedConfig, heightEstimatedConfig);
        } else {
            // 不是精确模式，测量流式布局的宽高和子组件的宽高
            estimateChildByNotExceed(widthEstimatedConfig, heightEstimatedConfig, width);
        }
        // 调用setEstimatedSize方法来将测量好的宽高传递给父组件
        setEstimatedSize(mEstimateFlowLayoutWidth, mEstimateFlowLayoutHeight);
        // 返回true让测量的宽高生效
        return true;
    }

    /**
     * 测量模式不是精确模式，测量流式布局的宽高和子组件的宽高
     *
     * @param widthEstimatedConfig 测量流式布局宽度的测量规格
     * @param heightEstimatedConfig 测量流式布局高度的测量规格
     * @param width 通过EstimateSpec.getSize得到的高度
     */
    private void estimateChildByNotExceed(int widthEstimatedConfig, int heightEstimatedConfig, int width) {
        // 子组件的宽度
        int childWidth;
        // 子组件的高度
        int childHeight;
        // 流式布局可以有多行，这个变量表示当前行的宽度
        int curLineWidth = 0;
        // 流式布局可以有多行，这个变量表示当前行的高度
        int curLineHeight = 0;
        // 子组件的总数
        int childCount = getChildCount();
        // 用于存储每行的子组件
        List<Component> lineComponent = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            // 得到布局里面的每一个子组件
            Component child = getComponentAt(i);
            // 获取子组件的布局参数
            LayoutConfig layoutConfig = child.getLayoutConfig();
            /*
             * 调用getChildSizeWithMode方法获取子组件的宽度的测量规格，getChildSizeWithMode方法有三个参数，
             * 第一个参数是子组件的大小，由于希望获取子组件的宽度的测量规格，所以第一个参数传递子组件的宽度。
             * 第二个参数是父组件的测量规格，由于希望获取子组件的宽度的测量规格，所以第二个参数传递父组件的宽度的测量规格。
             * 第三个参数是子组件的测量规格，对于流式布局里面的子组件来说，我们并不希望流式布局去限定子组件的宽度，子组件想要多宽就有多宽，
             * 所以第三个参数就传EstimateSpec.UNCONSTRAINT
             */
            int childWidthMeasureSpec = EstimateSpec.getChildSizeWithMode(
                    layoutConfig.width, widthEstimatedConfig, EstimateSpec.UNCONSTRAINT);
            /*
             * 调用getChildSizeWithMode获取子组件的高度的测量规格，getChildSizeWithMode方法有三个参数，
             * 第一个参数是子组件的大小，由于希望获取子组件的高度的测量规格，所以第一个参数传递子组件的高度。
             * 第二个参数是父组件的测量规格，由于希望获取子组件的高度的测量规格，所以第二个参数传递父组件的高度的测量规格。
             * 第三个参数是子组件的测量规格，对于流式布局里面的子组件来说，我们并不希望流式布局去限定子组件的高度，子组件想要多高就有多高，
             * 所以第三个参数就传EstimateSpec.UNCONSTRAINT
             */
            int childHeightMeasureSpec = EstimateSpec.getChildSizeWithMode(
                    layoutConfig.height, heightEstimatedConfig, EstimateSpec.UNCONSTRAINT);
            /*
             * 调用子组件的estimateSize方法测量子组件，子组件的estimateSize方法会调用到子组件重写的onEstimateSize方法，
             * 所有的组件都是在onEstimateSize方法进行测量。如果子组件是系统提供的组件，比如Text，那就不需要开发者手动在
             * 子组件的onEstimateSize方法进行测量了，因为系统已经帮开发者测量好了。如果子组件是开发者自定义的，
             * 那就需要开发者手动在自定义的子组件的onEstimateSize方法进行测量
             */
            child.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
            /*
             * 测量完成后，就可以获取到子组件测量后的宽度了，由于子组件设置了外边距，
             * 子组件最终的宽度等于子组件测量后的宽度 + 子组件的左边的外边距 + 子组件的右边的外边距
             */
            childWidth = child.getEstimatedWidth() + layoutConfig.getMarginLeft() + layoutConfig.getMarginRight();
            /*
             * 测量完成后，就可以获取到子组件测量后的高度了，由于子组件设置了外边距，
             * 子组件最终的高度等于子组件测量后的高度 + 子组件的上边的外边距 + 子组件的底部的外边距
             */
            childHeight = child.getEstimatedHeight() + layoutConfig.getMarginTop() + layoutConfig.getMarginBottom();
            /*
             * 如果当前行没有足够位置来显示下一个子组件，那么就需要换行，把下一个组件显示在下一行
             * 如何判断当前行没有足够位置来显示下一个子组件？如果子组件的宽度 + 当前行的宽度 > 测量出来的宽度，
             * 那就说明当前行没有足够位置来显示下一个子组件，需要换行了。
             * 换行之前，先保存当前行的信息，先判断流式布局的宽高，对比当前流式布局的宽度和当前行的宽度，
             * 哪个大，哪个就是流式布局的宽度，而流式布局的高度就是当前流式布局的高度加上当前行的高度
             * 再把当前行的高度保存到集合，把当前行里面所有的子组件保存到集合。
             * 保存当前行信息后，更新新行信息。由于换行了，新行的宽度就是下一个子组件的宽度，新行的高度就是下一个子组件的高度，
             * 同时需要创建一个新的存储每行子组件的集合。
             */
            if (childWidth + curLineWidth > width) {
                // 换行之前，保存当前行信息
                // 判断流式布局的宽度，对比当前流式布局的宽度和当前行的宽度，哪个大，哪个就是流式布局的宽度
                mEstimateFlowLayoutWidth = Math.max(mEstimateFlowLayoutWidth, curLineWidth);
                // 判断流式布局的高度，流式布局的高度就是当前流式布局的高度加上当前行的高度
                mEstimateFlowLayoutHeight += curLineHeight;
                // 把当前行的高度保存到集合
                lineHeight.add(curLineHeight);
                // 把当前行里面所有的子组件保存到集合
                listLineComponent.add(lineComponent);
                // 更新新行信息
                // 由于换行了，新行的宽度就是下一个子组件的宽度
                curLineWidth = childWidth;
                // 由于换行了，新行的高度就是下一个子组件的高度
                curLineHeight = childHeight;
                // 由于换行了，创建一个新的存储每行子组件的集合
                lineComponent = new ArrayList<>();
            } else {
                // 当前行还有位置来显示下一个子组件，那就计算当前行的宽度
                // 当前行的宽度就是当前行的宽度 + 子组件的宽高
                curLineWidth += childWidth;
                // 对比当前行的高度和子组件的高度，哪个高，哪个就是当前行的高度
                curLineHeight = Math.max(curLineHeight, childHeight);

            }
            // 将子组件添加到集合
            lineComponent.add(child);
            /*
             * 上面的计算方式会漏掉最后一个子组件，需要计算最后一个子组件。先判断流式布局的宽高，
             * 对比当前流式布局的宽度和当前行的宽度，哪个大，哪个就是流式布局的宽度，
             * 而流式布局的高度就是当前流式布局的高度加上当前行的高度
             * 最后将当前行的高度保存到集合，将当前行的所有子组件添加到集合
             */
            if (i == childCount - 1) {
                // 判断流式布局的宽度，对比当前流式布局的宽度和当前行的宽度，哪个大，哪个就是流式布局的宽度
                mEstimateFlowLayoutWidth = Math.max(mEstimateFlowLayoutWidth, curLineWidth);
                // 流式布局的高度就是当前流式布局的高度加上当前行的高度
                mEstimateFlowLayoutHeight += curLineHeight;
                // 将当前行的高度保存到集合
                lineHeight.add(curLineHeight/4);
                // 将当前行的所有子组件添加到集合
                listLineComponent.add(lineComponent);

            }

        }
    }

    /**
     * 在精确模式下测量子组件的宽高，在调用estimateChildByPrecise方法之前，就已经确定好了流式布局的宽高，
     * 所以不需要再次处理流式布局的宽高了，在estimateChildByPrecise方法里面只需要测量子组件的宽高
     *
     * @param width 通过EstimateSpec.getSize得到的高度
     * @param widthEstimatedConfig 流式布局宽度的测量规格
     * @param heightEstimatedConfig 流式布局高度的测量规格
     */
    private void estimateChildByPrecise(int width, int widthEstimatedConfig, int heightEstimatedConfig) {
        // 子组件的宽度
        int childWidth;
        // 子组件的高度
        int childHeight;
        // 流式布局可以有多行，这个变量表示当前行的宽度
        int curLineWidth = 0;
        // 流式布局可以有多行，这个变量表示当前行的高度
        int curLineHeight = 0;
        // 子组件的总数
        int childCount = getChildCount();
        // 用于存储每行的子组件
        List<Component> lineComponent = new ArrayList<>();
        // 遍历子组件
        for (int i = 0; i < childCount; i++) {
            // 得到每个子组件
            Component child = getComponentAt(i);
            if (child.getVisibility() == HIDE) {
                continue;
            }
            // 得到子组件的布局参数
            LayoutConfig layoutConfig = child.getLayoutConfig();
            /*
             * 调用getChildSizeWithMode获取子组件的宽度的测量规格，getChildSizeWithMode方法有三个参数，
             * 第一个参数是子组件的大小，由于希望获取子组件的宽度的测量规格，所以第一个参数传递子组件的宽度。
             * 第二个参数是父组件的测量规格，由于希望获取子组件的宽度的测量规格，所以第二个参数传递父组件的宽度的测量规格。
             * 第三个参数是子组件的测量规格，对于流式布局里面的子组件来说，我们并不希望流式布局去限定子组件的宽度，子组件想要多宽就有多宽，
             * 所以第三个参数就传EstimateSpec.UNCONSTRAINT
             */
            int childWidthMeasureSpec = EstimateSpec.getChildSizeWithMode(
                    layoutConfig.width, widthEstimatedConfig, EstimateSpec.UNCONSTRAINT);
            /*
             * 调用getChildSizeWithMode获取子组件的高度的测量规格，getChildSizeWithMode方法有三个参数，
             * 第一个参数是子组件的大小，由于希望获取子组件的高度的测量规格，所以第一个参数传递子组件的高度。
             * 第二个参数是父组件的测量规格，由于希望获取子组件的高度的测量规格，所以第二个参数传递父组件的高度的测量规格。
             * 第三个参数是子组件的测量规格，对于流式布局里面的子组件来说，我们并不希望流式布局去限定子组件的高度，子组件想要多高就有多高，
             * 所以第三个参数就传EstimateSpec.UNCONSTRAINT
             */
            int childHeightMeasureSpec = EstimateSpec.getChildSizeWithMode(
                    layoutConfig.height, heightEstimatedConfig, EstimateSpec.UNCONSTRAINT);
            /*
             * 调用子组件的estimateSize方法测量子组件，子组件的estimateSize方法会调用到子组件重写的onEstimateSize方法，
             * 所有的组件都是在onEstimateSize方法进行测量。如果子组件是系统提供的组件，比如Text，那就不需要开发者手动在
             * 子组件的onEstimateSize方法进行测量了，因为系统已经帮开发者测量好了。如果子组件是开发者自定义的，
             * 那就需要开发者手动在自定义的子组件的onEstimateSize方法进行测量
             */
            child.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
            /*
             * 测量完成后，就可以获取到子组件测量后的宽度了，由于子组件设置了外边距，
             * 子组件最终的宽度等于子组件测量后的宽度 + 子组件的左边的外边距 + 子组件的右边的外边距
             */
            childWidth = child.getEstimatedWidth() + layoutConfig.getMarginLeft() + layoutConfig.getMarginRight();
            /*
             * 测量完成后，就可以获取到子组件测量后的高度了，由于子组件设置了外边距，
             * 子组件最终的高度等于子组件测量后的高度 + 子组件的上边的外边距 + 子组件的底部的外边距
             */
            childHeight = child.getEstimatedHeight() + layoutConfig.getMarginTop() + layoutConfig.getMarginBottom();
            /*
             * 如果当前行没有足够位置来显示下一个子组件，那么就需要换行，把下一个组件显示在下一行
             * 如何判断当前行没有足够位置来显示下一个子组件？如果子组件的宽度 + 当前行的宽度 > 测量出来的宽度，
             * 那就说明当前行没有足够位置来显示下一个子组件，需要换行了。
             * 换行之前，先保存当前行的信息，把当前行的高度保存到集合，把当前行里面所有的子组件保存到集合。
             * 保存当前行信息后，更新新行信息。由于换行了，新行的宽度就是下一个子组件的宽度，新行的高度就是下一个子组件的高度，
             * 同时需要创建一个新的存储每行子组件的集合。
             */
            if (childWidth + curLineWidth > width) {
                // 换行之前，保存当前行信息
                // 把当前行的高度保存到集合
                lineHeight.add(curLineHeight);
                // 把当前行里面所有的子组件保存到集合
                listLineComponent.add(lineComponent);
                // 更新新行信息
                // 由于换行了，新行的宽度就是下一个子组件的宽度
                curLineWidth = childWidth;
                // 由于换行了，新行的高度就是下一个子组件的高度
                curLineHeight = childHeight;
                // 由于换行了，创建一个新的存储每行子组件的集合
                lineComponent = new ArrayList<>();
            } else {
                // 当前行还有位置来显示下一个子组件，那就计算当前行的宽度
                // 当前行的宽度就是当前行的宽度 + 子组件的宽度
                curLineWidth += childWidth;
                // 对比当前行的高度和子组件的高度，哪个高，哪个就是当前行的高度
                curLineHeight = Math.max(curLineHeight, childHeight);
            }
            // 将子组件添加到集合
            lineComponent.add(child);
            /*
             * 上面的计算方式会漏掉最后一个子组件，需要将最后一个子组件添加到集合。
             * 将当前行的高度保存到集合，将当前行的所有子组件添加到集合
             */
            if (i == childCount - 1) {
                // 将当前行的高度保存到集合
                lineHeight.add(curLineHeight);
                // 将当前行的所有子组件添加到集合
                listLineComponent.add(lineComponent);
            }
        }
    }

    /**
     * 确定子组件的摆放位置
     *
     * @param left 自定义布局的左上角到父组件左边的距离，也就是流式布局的左上角到父组件左边的距离
     * @param top 自定义布局的左上角到父组件上边的距离，也就是流式布局的左上角到父组件上边的距离
     * @param width 自定义布局测量出来宽度，也就是流式布局的宽度
     * @param height 自定义布局测量出来高度，也就是流式布局的高度
     * @return true表示此组件已在onArrange方法中处理完成
     */
    @Override
    public boolean onArrange(int left, int top, int width, int height) {
        // 当前行到流式布局左边的距离
        int curLineLeft = 0;
        // 当前行到流式布局上边的距离
        int curLineTop = 0;
        // 子组件左上角到流式布局左边的距离
        int l;
        // 子组件左上角到流式布局上边的距离
        int t;
        /*
         * 我们需要把子组件一行一行的显示出来，之前用集合存储了每行的子组件，此时就可以遍历了，
         * 先拿到每行的子组件，再遍历每行中具体的一个子组件
         */
        for (int i = 0; i < listLineComponent.size(); i++) {

            // 得到每行的子组件
            List<Component> lineComponent = listLineComponent.get(i);
            // 得到每行中具体的一个子组件
            for (Component component : lineComponent) {
                if (component.getVisibility() == Component.HIDE) {
                    // 子组件隐藏了，不做处理
                    continue;
                }
                LayoutConfig layoutConfig = component.getLayoutConfig();
                // 得到子组件的左边的外边距
                int marginLeft = layoutConfig.getMarginLeft();
                // 得到子组件的上边的外边距
                int marginTop = layoutConfig.getMarginTop();
                // 得到子组件的右边的外边距
                int marginRight = layoutConfig.getMarginRight();
                // 得到子组件测量后的宽度
                int estimatedWidth = component.getEstimatedWidth();
                // 得到子组件测量后的高度
                int estimatedHeight = component.getEstimatedHeight();
                // 子组件左上角到流式布局左边的距离 = 当前行到流式布局左边的距离 + 子组件的左边的外边距
                l = curLineLeft + marginLeft;
                // 子组件左上角到流式布局上边的距离 = 当前行到流式布局上边的距离 + 子组件的上边的外边距
                t =  curLineTop + marginTop;
                // 计算完子组件的左上以及子组件的宽高，调用arrange方法确定子组件的位置
                component.arrange(l, t, estimatedWidth, estimatedHeight);
                // 更新当前行到流式布局左边的距离，当前行到流式布局左边的距离 = 子组件测量后的宽度 + 子组件的左边的外边距 + 子组件的右边的外边距
                curLineLeft += estimatedWidth + marginLeft + marginRight;
            }
            // 遍历完一行后，当前行到流式布局左边的距离就要为0，因为又要从头开始了
            curLineLeft = 0;
            // 遍历完一行后，当前行到流式布局上边的距离 = 前行到流式布局上边的距离 + 当前行的高度，因为开始在下一行确定子组件的摆放位置了
            curLineTop += lineHeight.get(i);
        }
        return true;
    }
}
