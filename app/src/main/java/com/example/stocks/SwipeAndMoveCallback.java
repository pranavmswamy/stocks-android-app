package com.example.stocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class SwipeAndMoveCallback extends ItemTouchHelper.Callback {

    Context mContext;
    private Paint mClearPaint;
    private ColorDrawable mBackground;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private ArrayList<StockListingDataModel> portfolio;
    private  ArrayList<StockListingDataModel> watchlist;
    private RecyclerView parentRecyclerView;

    SwipeAndMoveCallback(Context context, ArrayList<StockListingDataModel> portfolio, ArrayList<StockListingDataModel> watchlist, RecyclerView parentRecyclerView) {
        mContext = context;
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor("#b80f0a");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_delete_24);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();
        this.watchlist = watchlist;
        this.portfolio = portfolio;
        this.parentRecyclerView = parentRecyclerView;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN , ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        Log.e("tag", "fromPosition = " + fromPosition);
        Log.e("tag", "toPosition = " + toPosition);

        if(fromPosition <= portfolio.size() &&
                fromPosition > 0 &&
                toPosition > 0 &&
                toPosition <= portfolio.size()) {
            Log.e("tag", "Entered if condition in portfolio section" );
            Collections.swap(portfolio, fromPosition-1, toPosition-1);
            parentRecyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        if(fromPosition > portfolio.size() + 1 &&
                toPosition < watchlist.size() + portfolio.size() + 2 &&
                toPosition > portfolio.size() + 1 &&
                fromPosition < watchlist.size() + portfolio.size() + 2) {
            Log.e("tag", "Entered if condition in watchlist section" );
            Collections.swap(watchlist, fromPosition - portfolio.size() - 2, toPosition - portfolio.size() - 2);
            parentRecyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        if(position > portfolio.size() + 1 &&
                position < watchlist.size() + portfolio.size() + 2) {

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    Log.e("tag", "onSwiped: "+"item being removed" );

                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("stock_app", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Set<String> watchlistSetSharedPref = sharedPreferences.getStringSet("favorites", null);
                    watchlistSetSharedPref.remove(watchlist.get(position - portfolio.size() - 2).ticker.toLowerCase());
                    editor.putStringSet("favorites", watchlistSetSharedPref);
                    editor.commit();

                    Log.e("adfa", "onSwiped: after removal " + watchlistSetSharedPref );
                    Toast.makeText(mContext, watchlist.get(position - portfolio.size() - 2).ticker.toUpperCase() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    watchlist.remove( position - portfolio.size() - 2);
                    parentRecyclerView.getAdapter().notifyItemRemoved(position);
                    break;
            }
            parentRecyclerView.getAdapter().notifyDataSetChanged();
        }
        else if(position <= portfolio.size() &&
                position > 0) {
            parentRecyclerView.getAdapter().notifyDataSetChanged();
            //Log.d("tag", "onSwiped: " + parentRecyclerView.getAdapter());
        }
        else {
            parentRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;


        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }
}
