package com.github.ness.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
  
  public static String now() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(cal.getTime());
  }

  
  public static long nowlong() { return System.currentTimeMillis(); }

  
  public static String when(long time) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    return sdf.format(Long.valueOf(time));
  }
  
  public static long convert(String a) {
    if (a.endsWith("s")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 1000L;
    }
    if (a.endsWith("m")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 60000L;
    }
    if (a.endsWith("h")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 3600000L;
    }
    if (a.endsWith("d")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 86400000L;
    }
    if (a.endsWith("m")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 2592000000L;
    }
    if (a.endsWith("y")) {
      return Long.valueOf(a.substring(0, a.length() - 1)).longValue() * 31104000000L;
    }
    return -1L;
  }
  
  public static String date() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(cal.getTime());
  }
  
  public static String getTime(int time) {
    Date timeDiff = new Date();
    timeDiff.setTime((time * 1000));
    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    String eventTimeDisplay = timeFormat.format(timeDiff);
    return eventTimeDisplay;
  }
  
  public static boolean elapsed(long from, long required) {
    if (System.currentTimeMillis() - from > required) {
      return true;
    }
    return false;
  }

  
  public static long elapsed(long starttime) { return System.currentTimeMillis() - starttime; }


  
  public static long left(long start, long required) { return required + start - System.currentTimeMillis(); }
  
  public enum TimeUnit
  {
    FIT, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS;
  }
}
