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

package ch.berta.fabio.popularmovies.presentation.ui.adapters.listeners;

/**
 * Defines the interaction of the adapter.
 */
public interface MovieDetailsInteractionListener {

    /**
     * Called when the movie poster is loaded.
     */
    void onPosterLoaded();

    /**
     * Called when the user clicks on a video row.
     *
     * @param position the position of the row clicked
     */
    void onVideoRowItemClick(int position);
}
