package com.iboxchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ensurer the TimeMillis is unique
 **/
public class TimeUniquesEnsurer {
  private final List<Long> lastTsMs = new ArrayList(10);
  private final long addition = 0;

  private long currentTimeMillis() {
    return System.currentTimeMillis() + addition;
  }

  public synchronized long uniqueTimestamp() {
    long ms = currentTimeMillis();

    // change time back case
    if (lastTsMs.size() > 2) {
      long min = Collections.min(lastTsMs);
      if (ms < min) {
        lastTsMs.clear();
        lastTsMs.add(ms);
        return ms;
      }
    }
    // usual case
    while (lastTsMs.contains(ms)) {
      ms += 1;
    }
    while (lastTsMs.size() >= 10) {
      lastTsMs.remove(0);
    }
    lastTsMs.add(ms);
    return ms;
  }
}
