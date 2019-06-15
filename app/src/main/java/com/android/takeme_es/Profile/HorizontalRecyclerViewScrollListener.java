package com.android.takeme_es.Profile;

import android.support.v7.widget.RecyclerView;

public class HorizontalRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final int OFFSET_RANGE = 50;
    private static final double COVER_FACTOR = 0.7;

    private int[] itemBounds = null;
    private final OnItemCoverListener listener;

    public HorizontalRecyclerViewScrollListener(final OnItemCoverListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (itemBounds == null) fillItemBounds(recyclerView.getAdapter().getItemCount(), recyclerView);
        for (int i = 0; i < itemBounds.length; i++) {
            if (isInChildItemsRange(recyclerView.computeVerticalScrollOffset(), itemBounds[i], OFFSET_RANGE)) listener.onItemCover(i);
        }
    }

    private void fillItemBounds(final int itemsCount, final RecyclerView recyclerView) {
        itemBounds = new int[itemsCount];
        int childWidth = (recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()) / itemsCount;
        for (int i = 0; i < itemsCount; i++) {
            itemBounds[i] = (int) (((childWidth * i + childWidth * (i + 1)) / 2) * COVER_FACTOR);
        }
    }

    private boolean isInChildItemsRange(final int offset, final int itemBound, final int range) {
        int rangeMin = itemBound - range;
        int rangeMax = itemBound + range;
        return (Math.min(rangeMin, rangeMax) <= offset) && (Math.max(rangeMin, rangeMax) >= offset);
    }

    public interface OnItemCoverListener {
        void onItemCover(final int position);
    }
}