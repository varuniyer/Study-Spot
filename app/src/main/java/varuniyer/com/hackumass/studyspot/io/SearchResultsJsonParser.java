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

package varuniyer.com.hackumass.studyspot.io;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import varuniyer.com.hackumass.studyspot.model.Highlight;
import varuniyer.com.hackumass.studyspot.model.HighlightedResult;
import varuniyer.com.hackumass.studyspot.model.StudySpot;

/**
 * Parses the JSON output of a search query.
 */
public class SearchResultsJsonParser
{
    private StudySpotJsonParser studySpotParser = new StudySpotJsonParser();

    /**
     * Parse the root result JSON object into a list of results.
     *
     * @param jsonObject The result's root object.
     * @return A list of results (potentially empty), or null in case of error.
     */
    public List<HighlightedResult<StudySpot>> parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null) {
            Log.i("Failure", "jsonObject is null");
            return null;
        }

        List<HighlightedResult<StudySpot>> results = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null) {
            Log.i("Failure", "Hits array is null");
            return null;
        }

        for (int i = 0; i < hits.length(); ++i) {
            JSONObject hit = hits.optJSONObject(i);
            if (hit == null) {
                Log.i("Failure", "Hit is null");
                continue;
            }

            StudySpot spot = studySpotParser.parse(hit);
            if (spot == null) {
                Log.i("Failure", "StudySpot is null");
                continue;
            }

            JSONObject highlightResult = hit.optJSONObject("_highlightResult");
            if (highlightResult == null) {
                Log.i("Failure", "hiresult is null");
                continue;
            }
            JSONObject highlightTitle = highlightResult.optJSONObject("Name");
            if (highlightTitle == null) {
                Log.i("Failure", "Hit is null");
                continue;
            }
            String value = highlightTitle.optString("value");
            Log.i("Value: ", value);
            if (value == null) {
                Log.i("Failure", "Value String is null");
                continue;
            }
            HighlightedResult<StudySpot> result = new HighlightedResult<>(spot);
            result.addHighlight("Name", new Highlight("Name", value));
            results.add(result);
        }
        return results;
    }
}
