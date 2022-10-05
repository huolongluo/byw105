package com.legend.modular_contract_sdk.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

/**
 * Created by Spencer on 5/21/18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final boolean mShowTop;
    private final boolean mShowBottom;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private int[] mSkipPositions;

    public SpaceItemDecoration(int left, int top, int right, int bottom, int[] skipPositions, boolean showTop, boolean showBottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        mSkipPositions = skipPositions == null ? new int[]{-1} : skipPositions;
        mShowTop = showTop;
        mShowBottom = showBottom;

        Arrays.sort(mSkipPositions);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();

        // (only show top as first holder || only show bottom as last holder) && top or bottom has value
        if (((mShowTop && itemPosition == 0) || (mShowBottom && parent.getAdapter() != null && itemPosition == parent.getAdapter().getItemCount() - 1)) && (mTop > 0 || mBottom > 0)) {
            int temp = mTop > 0 ? mTop : mBottom;
            outRect.set(mLeft, temp, mRight, temp);
        }
        // skip
        else if (Arrays.binarySearch(mSkipPositions, itemPosition) >= 0) {
        }
        // normal
        else {
            outRect.set(mLeft, mTop, mRight, mBottom);
        }
    }

    public static class Builder {

        private int mLeft;
        private int mTop;
        private int mRight;
        private int mBottom;
        private int[] mSkipPositions;
        private boolean mShowTop;
        private boolean mShowBottom;

        public static Builder start() {
            return new Builder();
        }

        public Builder setLeft(int left) {
            mLeft = left;
            return this;
        }

        public Builder setTop(int top) {
            mTop = top;
            return this;
        }

        public Builder setRight(int right) {
            mRight = right;
            return this;
        }

        public Builder setBottom(int bottom) {
            mBottom = bottom;
            return this;
        }

        public Builder setSkipPosition(int... positions) {
            mSkipPositions = positions;
            return this;
        }

        public Builder setShowFirstHolderTopDecoration() {
            mShowTop = true;
            return this;
        }

        public Builder setShowLastHolderBottomDecoration() {
            mShowBottom = true;
            return this;
        }

        public RecyclerView.ItemDecoration build() {
            return new SpaceItemDecoration(mLeft, mTop, mRight, mBottom, mSkipPositions, mShowTop, mShowBottom);
        }
    }
}
