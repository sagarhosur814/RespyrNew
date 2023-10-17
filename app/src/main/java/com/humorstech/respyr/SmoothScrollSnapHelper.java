package com.humorstech.respyr;

import android.graphics.PointF;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SmoothScrollSnapHelper extends LinearSnapHelper {

    RecyclerView.Adapter<?> adapter;
    RecyclerView recyclerView;

    public SmoothScrollSnapHelper(RecyclerView.Adapter<?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        int itemCount = adapter.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            return RecyclerView.NO_POSITION;
        }

        int targetPosition = -1;
        if (layoutManager.canScrollHorizontally()) {
            targetPosition = calculateTargetPositionForHorizontalLayout(layoutManager, velocityX);
        } else if (layoutManager.canScrollVertically()) {
            targetPosition = calculateTargetPositionForVerticalLayout(layoutManager, velocityY);
        }

        return targetPosition;
    }

    private int calculateTargetPositionForHorizontalLayout(RecyclerView.LayoutManager layoutManager, int velocityX) {
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        if (velocityX > 0) {
            currentPosition++;
        } else if (velocityX < 0) {
            currentPosition--;
        }

        currentPosition = Math.max(0, Math.min(itemCount - 1, currentPosition));
        return currentPosition;
    }

    private int calculateTargetPositionForVerticalLayout(RecyclerView.LayoutManager layoutManager, int velocityY) {
        // Similar logic as horizontal layout
        return RecyclerView.NO_POSITION;
    }

    @Override
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) throws IllegalStateException {
        this.recyclerView = recyclerView;
        super.attachToRecyclerView(recyclerView);
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = super.calculateDistanceToFinalSnap(layoutManager, targetView);

        if (layoutManager.canScrollHorizontally()) {
            out[0] = calculateDxSmooth(targetView, layoutManager);
            out[1] = 0;
        } else if (layoutManager.canScrollVertically()) {
            // Implement similar logic for vertical scrolling
        }

        return out;
    }

    @Override
    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                super.onTargetFound(targetView, state, action);
            }
        };
    }



    private int calculateDxSmooth(View targetView, RecyclerView.LayoutManager layoutManager) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) targetView.getLayoutParams();
        int leftMargin = params.leftMargin;
        int rightMargin = params.rightMargin;
        int dx = targetView.getLeft() - leftMargin - layoutManager.getPaddingLeft();
        return dx;
    }
}