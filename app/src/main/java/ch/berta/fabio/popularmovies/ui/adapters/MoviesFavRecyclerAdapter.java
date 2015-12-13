/*
 * Copyright (c) 2015 Fabio Berta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.berta.fabio.popularmovies.ui.adapters;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import ch.berta.fabio.popularmovies.R;
import ch.berta.fabio.popularmovies.Utils;
import ch.berta.fabio.popularmovies.data.repositories.MovieRepository;
import ch.berta.fabio.popularmovies.ui.adapters.listeners.MovieInteractionListener;
import ch.berta.fabio.popularmovies.ui.adapters.rows.MovieRow;

/**
 * Provides the adapter for a movie poster images grid.
 */
public class MoviesFavRecyclerAdapter extends RecyclerView.Adapter {

    private static final int VIEW_RESOURCE = R.layout.row_movie;
    private static final String LOG_TAG = MoviesFavRecyclerAdapter.class.getSimpleName();
    private Cursor mCursor;
    private View mViewEmpty;
    private int mRowIdColumn;
    private boolean mDataIsValid;
    private Fragment mLifecycleFragment;
    private MovieInteractionListener mListener;
    private MovieRepository mMovieRepo;
    private int mItemHeight;

    public MoviesFavRecyclerAdapter(@Nullable Cursor cursor, @NonNull View emptyView,
                                    int layoutWidth, int columnCount,
                                    @NonNull MovieRepository movieRepo, @NonNull Fragment fragment,
                                    @NonNull MovieInteractionListener listener) {
        mCursor = cursor;
        mViewEmpty = emptyView;
        mDataIsValid = cursor != null;
        mRowIdColumn = mDataIsValid ? cursor.getColumnIndexOrThrow(BaseColumns._ID) : -1;
        setHasStableIds(true);

        mMovieRepo = movieRepo;
        mLifecycleFragment = fragment;
        mListener = listener;

        mItemHeight = Utils.calcPosterHeight(columnCount, layoutWidth);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(VIEW_RESOURCE,
                parent, false);
        return new MovieRow(view, mItemHeight, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        MovieRow movieRow = (MovieRow) holder;

        String title = mMovieRepo.getMovieTitleFromFavMoviesCursor(mCursor);
        Date date = mMovieRepo.getMovieReleaseDateFromFavMoviesCursor(mCursor);
        String poster = mMovieRepo.getMoviePosterFromFavMoviesCursor(mCursor);

        movieRow.setMovie(title, Utils.formatDateShort(date), poster, mLifecycleFragment);
    }

    @Override
    public int getItemCount() {
        return mDataIsValid ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataIsValid) {
            return mCursor.moveToPosition(position) ?
                    mCursor.getLong(mRowIdColumn) :
                    RecyclerView.NO_ID;
        } else {
            return RecyclerView.NO_ID;
        }
    }

    /**
     * Returns the TheMovieDB id for the movie at position.
     *
     * @param position the position of the movie
     * @return the TheMovieDB id
     */
    public int getMovieDbIdForPosition(int position) {
        if (!mCursor.moveToPosition(position)) {
            return -1;
        }

        return mMovieRepo.getMovieDbIdFromFavMoviesCursor(mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(@Nullable Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is returned.
     */
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final int itemCount = getItemCount();
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;

        if (newCursor != null) {
            mRowIdColumn = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataIsValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataIsValid = false;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, itemCount);
        }

        toggleEmptyViewVisibility();
        return oldCursor;
    }

    private void toggleEmptyViewVisibility() {
        mViewEmpty.setVisibility(getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }
}