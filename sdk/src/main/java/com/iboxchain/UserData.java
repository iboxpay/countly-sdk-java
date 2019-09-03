package com.iboxchain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This class hold the data of user
 */
public class UserData {
  private static final String NAME_KEY = "name";
  private static final String USERNAME_KEY = "username";
  private static final String EMAIL_KEY = "email";
  private static final String ORG_KEY = "organization";
  private static final String PHONE_KEY = "phone";
  private static final String PICTURE_KEY = "picture";
  private static final String PICTURE_PATH_KEY = "picturePath";
  private static final String GENDER_KEY = "gender";
  private static final String BYEAR_KEY = "byear";
  private static final String CUSTOM_KEY = "custom";

  private static String name;
  private static String username;
  private static String email;
  private static String org;
  private static String phone;
  private static String picture;
  private static String picturePath;
  private static String gender;
  private static Map<String, String> custom;
  private static Map<String, JSONObject> customMods;
  private static int byear = 0;

  private static boolean isSynced = true;

  public void setUserData(Map<String, String> data) {
    setUserData(data, null);
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
  public void setUserData(Map<String, String> data, Map<String, String> customData) {
    UserData.setData(data);
    if (customData != null)
      UserData.setCustomData(customData);
  }

  public void setCustomUserData(Map<String, String> customdata) {
    if (customdata != null)
      UserData.setCustomData(customdata);
  }

  public void setProperty(String key, String value) {
    UserData.setCustomProperty(key, value);
  }

  /**
   * Clear all submitted information
   */
  public static void clear() {
    name = null;
    username = null;
    email = null;
    org = null;
    phone = null;
    picture = null;
    picturePath = null;
    gender = null;
    custom = null;
    customMods = null;
    byear = 0;
    isSynced = true;
  }


  public static void setData(Map<String, String> data) {
    if (data == null) {
      return;
    }
    if (data.containsKey(NAME_KEY))
      name = data.get(NAME_KEY);
    if (data.containsKey(USERNAME_KEY))
      username = data.get(USERNAME_KEY);
    if (data.containsKey(EMAIL_KEY))
      email = data.get(EMAIL_KEY);
    if (data.containsKey(ORG_KEY))
      org = data.get(ORG_KEY);
    if (data.containsKey(PHONE_KEY)) {
      phone = data.get(PHONE_KEY);
    }
    if (data.containsKey(PICTURE_PATH_KEY)) {
      picturePath = data.get(PICTURE_PATH_KEY);
    }
    if (picturePath != null) {
      File sourceFile = new File(picturePath);
      if (!sourceFile.isFile()) {
        picturePath = null;
      }
    }
    if (data.containsKey(PICTURE_KEY)) {
      picture = data.get(PICTURE_KEY);
    }
    if (data.containsKey(GENDER_KEY)) {
      gender = data.get(GENDER_KEY);
    }
    if (data.containsKey(BYEAR_KEY)) {
      try {
        byear = Integer.parseInt(data.get(BYEAR_KEY));
      } catch (NumberFormatException e) {
        byear = 0;
      }
    }
    isSynced = false;
  }

  /**
   * Sets user custom properties and values.
   *
   * @param data Map with user custom key/values
   */
  public static void setCustomData(Map<String, String> data) {
    if (custom == null)
      custom = new HashMap<String, String>();
    custom.putAll(data);
    isSynced = false;
  }

  /**
   * Sets custom provide key/value as custom property.
   *
   * @param key   String with key for the property
   * @param value String with value for the property
   */
  public static void setCustomProperty(String key, String value) {
    if (custom == null)
      custom = new HashMap<String, String>();
    custom.put(key, value);
    isSynced = false;
  }

  /**
   * Get picture path from url
   *
   * @return picture path
   */
  public static String getPicturePathFromQuery(URL url) {
    String query = url.getQuery();
    String[] pairs = query.split("&");
    String ret = "";
    if (url.getQuery().contains(PICTURE_PATH_KEY)) {
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        if (pair.substring(0, idx).equals(PICTURE_PATH_KEY)) {
          try {
            ret = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            ret = "";
          }
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Creates and returns a JSONObject containing the user data from this object.
   *
   * @return a JSONObject containing the user data from this object
   */
  public static JSONObject toJSON() {
    final JSONObject json = new JSONObject();

    try {
      if (name != null)
        if (name.equals(""))
          json.put(NAME_KEY, JSONObject.NULL);
        else
          json.put(NAME_KEY, name);
      if (username != null)
        if (username.equals(""))
          json.put(USERNAME_KEY, JSONObject.NULL);
        else
          json.put(USERNAME_KEY, username);
      if (email != null)
        if (email.equals(""))
          json.put(EMAIL_KEY, JSONObject.NULL);
        else
          json.put(EMAIL_KEY, email);
      if (org != null)
        if (org.equals(""))
          json.put(ORG_KEY, JSONObject.NULL);
        else
          json.put(ORG_KEY, org);
      if (phone != null)
        if (phone.equals(""))
          json.put(PHONE_KEY, JSONObject.NULL);
        else
          json.put(PHONE_KEY, phone);
      if (picture != null)
        if (picture.equals(""))
          json.put(PICTURE_KEY, JSONObject.NULL);
        else
          json.put(PICTURE_KEY, picture);
      if (gender != null)
        if (gender.equals(""))
          json.put(GENDER_KEY, JSONObject.NULL);
        else
          json.put(GENDER_KEY, gender);
      if (byear != 0)
        if (byear > 0)
          json.put(BYEAR_KEY, byear);
        else
          json.put(BYEAR_KEY, JSONObject.NULL);

      JSONObject ob;
      if (custom != null) {
        ob = new JSONObject(custom);
      } else {
        ob = new JSONObject();
      }
      if (customMods != null) {
        for (Map.Entry<String, JSONObject> entry : customMods.entrySet()) {
          ob.put(entry.getKey(), entry.getValue());
        }
      }
      json.put(CUSTOM_KEY, ob);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }

  /**
   * Returns &user_details= prefixed url to add to request data when making request to server
   *
   * @return a String user_details url part with provided user data
   */
  public static String getDataForRequest() {
    if (!isSynced) {
      isSynced = true;
      final JSONObject json = UserData.toJSON();
      if (json != null) {
        String result = json.toString();
        try {
          result = java.net.URLEncoder.encode(result, "UTF-8");

          if (result != null && !result.equals("")) {
            result = "&user_details=" + result;
            if (picturePath != null)
              result += "&" + PICTURE_PATH_KEY + "=" + java.net.URLEncoder.encode(picturePath, "UTF-8");
          } else {
            result = "";
            if (picturePath != null)
              result += "&user_details&" + PICTURE_PATH_KEY + "=" + java.net.URLEncoder.encode(picturePath, "UTF-8");
          }
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }

        return result;
      }
    }
    return "";
  }
}
