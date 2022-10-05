package com.legend.modular_contract_sdk.widget.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @link https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
 * Created by Spencer on 6/11/18.
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private boolean mDrawColor;
    private boolean mDrawVerticalColor = true;
    private boolean mDrawHorizontalColor = true;
    private Drawable mDivider;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this(spanCount, spacing, includeEdge, false, Color.TRANSPARENT);
    }

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, boolean drawColor, int color) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        mDrawColor = drawColor;
        mDivider = new ColorDrawable(color);
    }

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, boolean drawColor, boolean drawVerticalColor, boolean drawHorizontalColor, int color) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        mDrawColor = drawColor;
        mDivider = new ColorDrawable(color);
        mDrawVerticalColor = drawVerticalColor;
        mDrawHorizontalColor = drawHorizontalColor;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (!mDrawColor) {
            return;
        }

        if (mDrawHorizontalColor) {
            drawHorizontal(c, parent);
        }
        if (mDrawVerticalColor) {
            drawVertical(c, parent);
        }

    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        mDivider.setBounds(0, 0, parent.getRight(), spacing);
        mDivider.draw(c);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + spacing;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + spacing;
            final int left = child.getRight() + params.rightMargin;
            int right = left + spacing;
//            //满足条件( 最后一行 && 不绘制 ) 将vertical多出的一部分去掉;
            if (i == childCount - 1) {
                right -= spacing;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}
