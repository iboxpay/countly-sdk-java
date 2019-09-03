package com.iboxchain;

public class Config {
  public static String SERVER_URL;
  public static String APP_KEY;
  public static String DEVICE_ID;
  public static int CONSUMER_TYPE;
  public static boolean IS_LOG_ENABLE = false;

  public static final String SDK_NAME = "countly-sdk-java";
  public static final String SDK_VERSION = "1.0.0";

  public static final int TYPE_HTTP_CONSUMER = 100;
  public static final int TYPE_LOG_CONSUMER = 101;
  public static final int TYPE_SEND_EVENT = 200;
  public static final int TYPE_SEND_USER_DATA = 201;
}
