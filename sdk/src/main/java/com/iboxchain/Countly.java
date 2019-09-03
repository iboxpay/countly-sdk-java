package com.iboxchain;

import com.iboxchain.consumer.HttpConsumer;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is the public API for the Countly Java SDK.
 * init: init Countly Config
 * recordEvent: send event data
 * sendUserData: send user data
 * registerSuperProperties: set public properties
 */
public class Countly {

  private HttpConsumer mHttpConsumer;

  private ConcurrentHashMap<String, Object> superProperties;

  private Logger log = Logger.getLogger(Countly.class);

  private Countly() {

  }

  public Countly init(String serverURL, String appKey, int type) {
    return init(serverURL, appKey, type, null);
  }

  /**
   * Initializes the Countly SDK. Call from your main method.
   * Must be called before other SDK methods can be used.
   *
   * @param serverURL   URL of the Countly server to submit data to
   * @param appKey      app key for the application being tracked; find in the Countly Dashboard under Management &gt; Applications
   * @param type        Must be Config.TYPE_HTTP_CONSUMER
   * @param httpHeaders customize httpHeaders
   * @return Countly instance for easy method chaining
   */
  public Countly init(String serverURL, String appKey, int type, Map<String, String> httpHeaders) {
    if (Config.IS_LOG_ENABLE) {
      log.info("Init Countly(countly-sdk-java): \n serverURL=" + serverURL + ",appKey=" + appKey + "\n type=" + type + ",httpHeaders=" + httpHeaders);
    }
    Config.SERVER_URL = serverURL;
    Config.APP_KEY = appKey;
    if (type == Config.TYPE_LOG_CONSUMER) {
      Config.CONSUMER_TYPE = Config.TYPE_LOG_CONSUMER;
    } else {
      Config.CONSUMER_TYPE = Config.TYPE_HTTP_CONSUMER;
    }
    Config.DEVICE_ID = new BigInteger(64, new SecureRandom()).toString(16);
    mHttpConsumer = new HttpConsumer(httpHeaders);
    superProperties = new ConcurrentHashMap<String, Object>();
    return this;
  }


  public void recordEvent(String key) {
    recordEvent(key, null, 1, 0);
  }

  public void recordEvent(String key, int count) {
    recordEvent(key, null, count, 0);
  }

  public void recordEvent(String key, int count, double sum) {
    recordEvent(key, null, count, sum);
  }

  public void recordEvent(String key, Map<String, Object> segmentation, int count) {
    recordEvent(key, segmentation, count, 0);
  }

  public void recordEvent(String key, Map<String, Object> segmentation, int count, double sum) {
    recordEvent(key, segmentation, count, sum, 0);
  }

  /**
   * Records a custom event with the specified values.
   *
   * @param key          name of the custom event, required, must not be the empty string
   * @param segmentation segmentation dictionary to associate with the event, can be null
   * @param count        count to associate with the event, should be more than zero
   * @param sum          sum to associate with the event
   * @param dur          duration of an event
   */
  public void recordEvent(String key, Map<String, Object> segmentation, int count, double sum, double dur) {

    if (key == null || key.length() < 1){
      return;
    }

    if (Config.IS_LOG_ENABLE) {
      log.info("RecordEvent params(countly-sdk-java): \n key=" + key + ",segmentation=" + segmentation + ",count=" + count
          + ",sun=" + sum + ",dur=" + dur);
    }

    for (Map.Entry<String, Object> item : superProperties.entrySet()) {
      segmentation.put(item.getKey(), item.getValue());
    }

    Event event = new Event(key);
    event.setSegmentation(segmentation);
    event.setCount(count);
    event.setSum((int) sum);
    event.setDur(dur);

    JSONArray jsonArray = new JSONArray();
    try {
      jsonArray.put(event.toJSON());
      String url = Config.SERVER_URL + "/i?" + mHttpConsumer.prepareRequestData(jsonArray.toString(), Config.TYPE_SEND_EVENT);
      mHttpConsumer.sendData(url);
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (HttpConsumer.HttpConsumerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public synchronized Countly sendUserData(Map<String, String> data) {
    return sendUserData(data, null);
  }

  /**
   * Sets information about user with custom properties.
   * In custom properties you can provide any string key values to be stored with user
   * Possible keys are:
   * <ul>
   * <li>
   * name - (String) providing user's full name
   * </li>
   * <li>
   * username - (String) providing user's nickname
   * </li>
   * <li>
   * email - (String) providing user's email address
   * </li>
   * <li>
   * organization - (String) providing user's organization's name where user works
   * </li>
   * <li>
   * phone - (String) providing user's phone number
   * </li>
   * <li>
   * picture - (String) providing WWW URL to user's avatar or profile picture
   * </li>
   * <li>
   * picturePath - (String) providing local path to user's avatar or profile picture
   * </li>
   * <li>
   * gender - (String) providing user's gender as M for male and F for female
   * </li>
   * <li>
   * byear - (int) providing user's year of birth as integer
   * </li>
   * </ul>
   *
   * @param data       Map&lt;String, String&gt; with user data
   * @param customData Map&lt;String, String&gt; with custom key values for this user
   */
  public synchronized Countly sendUserData(Map<String, String> data, Map<String, String> customData) {
    if (Config.IS_LOG_ENABLE) {
      log.info("SendUserData(countly-sdk-java): \n data=" + data + "\n customData=" + customData);
    }
    UserData.setData(data);
    if (customData != null) {
      UserData.setCustomData(customData);
    }

    try {
      String url = Config.SERVER_URL + "/i?" + mHttpConsumer.prepareRequestData(UserData.getDataForRequest(), Config.TYPE_SEND_USER_DATA);
      mHttpConsumer.sendData(url);
    } catch (HttpConsumer.HttpConsumerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    UserData.clear();
    return this;
  }

  /**
   * Add public properties which will be added to all events
   *
   * @param properties
   */
  public void registerSuperProperties(Map<String, Object> properties) {
    if (Config.IS_LOG_ENABLE) {
      log.info("RegisterSuperProperties(countly-sdk-java): \n properties=" + properties);
    }
    for (Map.Entry<String, Object> item : properties.entrySet()) {
      superProperties.put(item.getKey(), item.getValue());
    }
  }

  /**
   * clear public properties
   */
  public void clearSuperProperties() {
    superProperties.clear();
  }

  /**
   * Sets whether debug logging is turned on or off. Logging is disabled by default.
   *
   * @param enable true to enable logging, false to disable logging
   */
  public void setLoggingEnabled(boolean enable) {
    Config.IS_LOG_ENABLE = enable;
  }

  /**
   * provide a Countly instance
   *
   * @return Countly instance
   */
  public static Countly sharedInstance() {
    return SingletonHolder.instance;
  }

  private static class SingletonHolder {
    static final Countly instance = new Countly();
  }

}
