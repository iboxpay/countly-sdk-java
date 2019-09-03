/*
Copyright (c) 2012, 2013, 2014 Countly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.iboxchain;

import com.iboxchain.utils.CommonUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * This class holds the data for a single Count.ly custom event instance.
 * It also knows how to read & write itself to the Count.ly custom event JSON syntax.
 */
public class Event {
  private static final String SEGMENTATION_KEY = "segmentation";
  private static final String KEY_KEY = "key";
  private static final String COUNT_KEY = "count";
  private static final String SUM_KEY = "sum";
  private static final String DUR_KEY = "dur";
  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String DAY_OF_WEEK = "dow";
  private static final String HOUR = "hour";

  private String key;
  private Map<String, Object> segmentation;
  private int count;
  private int sum;
  private double dur;
  private long timestamp;
  private int hour;
  private int dow;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Map<String, Object> getSegmentation() {
    return segmentation;
  }

  public void setSegmentation(Map<String, Object> segmentation) {
    this.segmentation = segmentation;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getSum() {
    return sum;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }

  public double getDur() {
    return dur;
  }

  public void setDur(double dur) {
    this.dur = dur;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getDow() {
    return dow;
  }

  public void setDow(int dow) {
    this.dow = dow;
  }

  public Event(String key) {
    this.key = key;
    this.timestamp = CommonUtil.currentTimestampMs();
    this.hour = CommonUtil.currentHour();
    this.dow = CommonUtil.currentDayOfWeek();
  }

  /**
   * Creates and returns a JSONObject containing the event data from this object.
   *
   * @return a JSONObject containing the event data from this object
   */
  public JSONObject toJSON() throws JSONException {
    final JSONObject json = new JSONObject();


    json.put(KEY_KEY, key);
    json.put(COUNT_KEY, count);
    json.put(TIMESTAMP_KEY, timestamp);
    json.put(HOUR, hour);
    json.put(DAY_OF_WEEK, dow);

    JSONObject jobj = new JSONObject();
    if (segmentation != null) {
      for (Map.Entry<String, Object> pair : segmentation.entrySet()) {
        jobj.put(pair.getKey(), pair.getValue());
      }
    }

    json.put(SEGMENTATION_KEY, jobj);

    // we put in the sum last, the only reason that a JSONException would be thrown
    // would be if sum is NaN or infinite, so in that case, at least we will return
    // a JSON object with the rest of the fields populated
    json.put(SUM_KEY, sum);

    if (dur > 0) {
      json.put(DUR_KEY, dur);
    }


    return json;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || !(o instanceof Event)) {
      return false;
    }

    final Event e = (Event) o;

    return (key == null ? e.key == null : key.equals(e.key)) &&
        timestamp == e.timestamp &&
        hour == e.hour &&
        dow == e.dow &&
        (segmentation == null ? e.segmentation == null : segmentation.equals(e.segmentation));
  }

  @Override
  public int hashCode() {
    return (key != null ? key.hashCode() : 1) ^
        (segmentation != null ? segmentation.hashCode() : 1) ^
        (timestamp != 0 ? (int) timestamp : 1);
  }
}
