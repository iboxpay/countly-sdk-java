package com.iboxchain.utils;

import com.iboxchain.TimeUniquesEnsurer;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtil {

  private static final TimeUniquesEnsurer timeGenerator = new TimeUniquesEnsurer();

  /**
   * Get TimeZone Info
   *
   * @return TimeZone Info
   */
  public static int getTimezoneOffset() {
    return TimeZone.getDefault().getOffset(new Date().getTime()) / 60000;
  }

  public static synchronized long currentTimestampMs() {
    return timeGenerator.uniqueTimestamp();
  }

  public static int currentHour() {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
  }

  public static int currentDayOfWeek() {
    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    switch (day) {
      case Calendar.MONDAY:
        return 1;
      case Calendar.TUESDAY:
        return 2;
      case Calendar.WEDNESDAY:
        return 3;
      case Calendar.THURSDAY:
        return 4;
      case Calendar.FRIDAY:
        return 5;
      case Calendar.SATURDAY:
        return 6;
    }
    return 0;
  }
}
