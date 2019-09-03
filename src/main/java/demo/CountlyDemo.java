package demo;

import com.iboxchain.Config;
import com.iboxchain.Countly;

import java.util.HashMap;

public class CountlyDemo {
  public static void main(String[] args) throws Exception {
    HashMap<String, String> data = new HashMap<String, String>();
    data.put("name", "First name Last name");
    data.put("username", "nickname");
    data.put("email", "test@test.com");
    data.put("organization", "Tester");
    data.put("phone", "+123456789");
    data.put("gender", "M");
    // provide url to picture
    data.put("picture", "http://example.com/pictures/profile_pic.png");
    // or locally from device
    // data.put("picturePath", "src/example.png");

    data.put("byear", "1987");

    // providing any custom key values to store with user
    HashMap<String, String> custom = new HashMap<String, String>();
    custom.put("country", "Turkey");
    custom.put("city", "Istanbul");
    custom.put("address", "My house 11");

    // enable debig logger
    Countly.sharedInstance().setLoggingEnabled(true);
    // init countly sdk
    Countly.sharedInstance().init("https://asia-try.count.ly", "739d0f87d910caf808ea9209888c25d64b6a802a", Config.TYPE_HTTP_CONSUMER);

    HashMap<String, Object> segmentation = new HashMap<String, Object>();
    segmentation.put("country", "Turkey");
    segmentation.put("city", "Istanbul");
    segmentation.put("address", "My house 11");

    HashMap<String, Object> superProperties = new HashMap<String, Object>();
    superProperties.put("ip", "127.0.0.1");
    superProperties.put("port", "1080");

    // set public properties
    Countly.sharedInstance().registerSuperProperties(superProperties);
    // send user data
    Countly.sharedInstance().sendUserData(data, custom);
    // send event data
    Countly.sharedInstance().recordEvent("testEvent", segmentation, 1, 1);
  }

}
