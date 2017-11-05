/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package varuniyer.com.hackumass.studyspot.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import varuniyer.com.hackumass.studyspot.R;
import varuniyer.com.hackumass.studyspot.io.SearchResultsJsonParser;
import varuniyer.com.hackumass.studyspot.model.HighlightedResult;
import varuniyer.com.hackumass.studyspot.model.StudySpot;

public class StudySpotSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AbsListView.OnScrollListener {
    // BL:
    private Client client;
    private Index index;
    private Query query;
    private SearchResultsJsonParser resultsParser = new SearchResultsJsonParser();
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;
    public static String filter;

    // UI:
    private SearchView searchView;
    private ListView studySpotListView;
    private StudySpotAdapter studySpotListAdapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;
    private HighlightRenderer highlightRenderer;
    public static StudySpot current;

    // Constants

    private static final int HITS_PER_PAGE = 20;

    /** Number of items before the end of the list past which we start loading more content. */
    private static final int LOAD_MORE_THRESHOLD = 5;

    // Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studyspot_search);



        // Bind UI components.
        studySpotListView = (ListView) findViewById(R.id.listview_studyspots);
        studySpotListView.setAdapter(studySpotListAdapter = new StudySpotAdapter(this, R.layout.cell_studyspot));
        studySpotListView.setOnScrollListener(this);

        // Init Algolia.
        client = new Client("WR38GHZLH6", "b69b2db5bc9250105dd18d1b6f7b5b9d");
        index = client.getIndex("studyspot");

        //Log.i("Our data",obj);
        // Pre-build query.
        query = new Query();
        Log.i("Initialization", "Not Complete");
        query.setAttributesToRetrieve("Name", "Distance", "Volume", "Solo Study", "Group Study",
                "Student Computer Access", "Outlets", "Charging", "Whiteboard", "Printer",
                "latitude","longitude");
        query.setAttributesToHighlight("Name");
        query.setHitsPerPage(HITS_PER_PAGE);
        query.setFilters(filter);

        highlightRenderer = new HighlightRenderer(this);

        Log.i("Initialization", "Complete");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_studyspot_search, menu);

        // Configure search view.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery("",true);
        return true;
    }

    // Actions

    private void search() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        query.setQuery(searchView.getQuery().toString());
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                Log.i("Request", "Completed");
                if (content != null && error == null) {
                    // NOTE: Check that the received results are newer that the last displayed results.
                    //
                    // Rationale: Although TCP imposes a server to send responses in the same order as
                    // requests, nothing prevents the system f
                    // rom opening multiple connections to the
                    // same server, nor the Algolia client to transparently switch to another server
                    // between two requests. Therefore the order of responses is not guaranteed.
                    if (currentSearchSeqNo <= lastDisplayedSeqNo) {
                        return;
                    }

                    List<HighlightedResult<StudySpot>> results = resultsParser.parseResults(content);
                    Log.i("size",results.size() + "");
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        studySpotListAdapter.clear();
                        studySpotListAdapter.addAll(results);
                        studySpotListAdapter.notifyDataSetChanged();
                        studySpotListAdapter.sort(new Comparator<HighlightedResult<StudySpot>>() {
                            @Override
                            public int compare(HighlightedResult<StudySpot> o1, HighlightedResult<StudySpot> o2) {
                                return ((Double)(o1.getResult().dist)).compareTo(o2.getResult().dist);
                            }
                        });
                        lastDisplayedSeqNo = currentSearchSeqNo;
                        lastDisplayedPage = 0;
                    }

                    // Scroll the list back to the top.
                    studySpotListView.smoothScrollToPosition(0);
                }
            }
        });
    }

    private void loadMore() {
        Query loadMoreQuery = new Query(query);
        loadMoreQuery.setPage(++lastRequestedPage);
        final int currentSearchSeqNo = lastSearchedSeqNo;
        index.searchAsync(loadMoreQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {
                    // Ignore results if they are for an older query.
                    if (lastDisplayedSeqNo != currentSearchSeqNo) {
                        return;
                    }

                    List<HighlightedResult<StudySpot>> results = resultsParser.parseResults(content);
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        studySpotListAdapter.addAll(results);
                        studySpotListAdapter.notifyDataSetChanged();
                        lastDisplayedPage = lastRequestedPage;
                    }
                }
            }
        });
    }

    // Data sources

    private class StudySpotAdapter extends ArrayAdapter<HighlightedResult<StudySpot>> {
        public StudySpotAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup cell = (ViewGroup) convertView;
            if (cell == null) {
                cell = (ViewGroup) getLayoutInflater().inflate(R.layout.cell_studyspot, null);
            }

            TextView nameTextView = (TextView) cell.findViewById(R.id.textview_title);
            TextView distTextView = (TextView) cell.findViewById(R.id.textview_year);

            HighlightedResult<StudySpot> result = studySpotListAdapter.getItem(position);

            nameTextView.setText(highlightRenderer.renderHighlights(result
                    .getHighlight("Name").getHighlightedValue()));
            distTextView.setText(result.getResult().dist + " miles");

            final int p = position;
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = studySpotListAdapter.getItem(p).getResult();
                    Intent i = new Intent(StudySpotAdapter.super.getContext(), DetailActivity.class);
                    startActivity(i);
                }
            });

            return cell;
        }

        @Override
        public void addAll(Collection<? extends HighlightedResult<StudySpot>> items) {
            Log.i("Size",items.size() + "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.addAll(items);
            } else {
                for (HighlightedResult<StudySpot> item : items) {
                    add(item);
                }
            }
        }
    }

    // SearchView.OnQueryTextListener

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Nothing to do: the search has already been performed by `onQueryTextChange()`.
        // We do try to close the keyboard, though.
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search();
        return true;
    }

    // AbsListView.OnScrollListener

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Nothing to do.
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Abort if list is empty or the end has already been reached.
        if (totalItemCount == 0 || endReached) {
            return;
        }

        // Ignore if a new page has already been requested.
        if (lastRequestedPage > lastDisplayedPage) {
            return;
        }

        // Load more if we are sufficiently close to the end of the list.
        int firstInvisibleItem = firstVisibleItem + visibleItemCount;
        if (firstInvisibleItem + LOAD_MORE_THRESHOLD >= totalItemCount) {
            loadMore();
        }
    }
}
