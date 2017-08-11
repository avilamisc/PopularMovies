/*
 * Copyright (c) 2017 Fabio Berta
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

package ch.berta.fabio.popularmovies.features.grid.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import ch.berta.fabio.popularmovies.NavigationTarget
import ch.berta.fabio.popularmovies.data.MovieStorage
import ch.berta.fabio.popularmovies.data.SharedPrefs
import ch.berta.fabio.popularmovies.features.base.BaseViewModel
import ch.berta.fabio.popularmovies.features.grid.Sort
import ch.berta.fabio.popularmovies.features.grid.component.*
import ch.berta.fabio.popularmovies.features.grid.view.SelectedMovie
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class GridViewModel(
        sharedPrefs: SharedPrefs,
        movieStorage: MovieStorage,
        sortOptions: List<Sort>
) : BaseViewModel() {

    val sortSelections: PublishRelay<Int> = PublishRelay.create()
    val movieClicks: PublishRelay<SelectedMovie> = PublishRelay.create()
    val loadMore: PublishRelay<Unit> = PublishRelay.create()
    val refreshSwipes: PublishRelay<Unit> = PublishRelay.create()

    val state: LiveData<GridState>
    val navigation: Observable<NavigationTarget>

    val uiEvents = GridUiEvents(activityResults, snackbarShown, sortSelections, movieClicks, loadMore, refreshSwipes)

    init {
        val sources = GridSources(uiEvents, sharedPrefs, movieStorage)
        val initialState = GridState(sortOptions[0])
        val sinks = main(sources, initialState, sortOptions)

        state = LiveDataReactiveStreams.fromPublisher(sinks
                .ofType(GridSink.State::class.java)
                .map { it.state }
                .toFlowable(BackpressureStrategy.LATEST)
        )
        navigation = sinks
                .ofType(GridSink.Navigation::class.java)
                .map { it.target }
    }
}