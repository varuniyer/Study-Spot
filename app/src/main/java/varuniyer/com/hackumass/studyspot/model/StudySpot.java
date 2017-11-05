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

package varuniyer.com.hackumass.studyspot.model;

/**
 * A StudySpot object from the data model.
 */
public class StudySpot
{
    public String name;
    public String volume;
    public String solo;
    public String group;
    public String sca;
    public String outlets;
    public String charging;
    public String whiteboard;
    public String printer;
    public double dist;
    public double lat;
    public double lon;

    public StudySpot(String name, String volume, String solo, String group, String sca,
                     String outlets, String charging, String whiteboard, String printer,
                     double dist, double lat, double lon)
    {
        this.name = name;
        this.volume = volume;
        this.solo = solo;
        this.group = group;
        this.sca = sca;
        this.outlets = outlets;
        this.charging = charging;
        this.whiteboard = whiteboard;
        this.printer = printer;
        this.dist = dist;
        this.lat = lat;
        this.lon = lon;
    }
}
