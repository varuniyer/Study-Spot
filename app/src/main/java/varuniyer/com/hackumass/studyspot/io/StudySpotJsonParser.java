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

import org.json.JSONObject;

import varuniyer.com.hackumass.studyspot.model.StudySpot;

/**
 * Parses `StudySpot` instances from their JSON representation.
 */
public class StudySpotJsonParser
{
    /**
     * Parse a single study spot record.
     *
     * @param jsonObject JSON object.
     * @return Parsed study spot, or null if error.
     */
    public StudySpot parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;

        String name = jsonObject.optString("Name");
        String volume = jsonObject.optString("Volume");
        Log.i("Volume", volume);
        String solo = jsonObject.optString("Solo Study");
        String group = jsonObject.optString("Group Study");
        String sca = jsonObject.optString("Student Computer Access");
        String outlets = jsonObject.optString("Outlets");
        String charging = jsonObject.optString("Charging");
        String whiteboard = jsonObject.optString("Whiteboard");
        String printer = jsonObject.optString("Printer");
        double dist = jsonObject.optDouble("Distance");
        double lat = jsonObject.optDouble("latitude");
        double lon = jsonObject.optDouble("longitude");
        if (name != null && volume != null && solo != null && group != null &&
                sca != null && outlets != null && charging != null && whiteboard != null &&
                printer != null && dist >= 0) {
            return new StudySpot(name, volume, solo, group, sca, outlets, charging, whiteboard,
                    printer, dist, lat, lon);
        }
        return null;
    }
}
