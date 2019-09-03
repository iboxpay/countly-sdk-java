## Countly Java SDK
### Init SDK
When program startup (such as the public static void main(String[] args) method), you should call init() method to init SDK
```
/**
 * Initializes the Countly SDK. Call from your main method.
 * Must be called before other SDK methods can be used.
 *
 * @param serverURL   URL of the Countly server to submit data to
 * @param appKey      App key for the application being tracked
 * @param type        Must be Config.TYPE_HTTP_CONSUMER
 * @param httpHeaders Customize httpHeaders
 */
Countly.sharedInstance().init(String serverURL, String appKey, int type);

Countly.sharedInstance().init(String serverURL, String appKey, int type, Map<String, String> httpHeaders);
```
### Send event data
Call recordEvent() method to transfer event data.
```
 /**
  * Records a custom event with the specified values.
  *
  * @param key          name of the custom event, required, must not be the empty string
  * @param segmentation segmentation dictionary to associate with the event, can be null
  * @param count        count to associate with the event, should be more than zero
  * @param sum          sum to associate with the event
  * @param dur          duration of an event
  */
 Countly.sharedInstance().recordEvent(String key);
 Countly.sharedInstance().recordEvent(String key, int count);
 Countly.sharedInstance().recordEvent(String key, int count, double sum);
 Countly.sharedInstance().recordEvent(String key, Map<String, Object> segmentation, int count);
 Countly.sharedInstance().recordEvent(String key, Map<String, Object> segmentation, int count, double sum);
 Countly.sharedInstance().recordEvent(String key, Map<String, Object> segmentation, int count, double sum, double dur)
```
### Set public properties
Call registerSuperProperties() method to set public properties.

Call clearSuperProperties() method to clear public properties.

When you set public properties, all events will contain it.

```
  Countly.sharedInstance().registerSuperProperties(Map<String, Object> properties);
  Countly.sharedInstance().clearSuperProperties();
```
### Send user data
Call setUserData() method to transfer user data.
```
  HashMap<String, String> data = new HashMap<String, String>();
  // providing user's full name
  data.put("name", "First name Last name");
  // providing user's nickname
  data.put("username", "nickname");
  // providing user's email address address
  data.put("email", "test@test.com");
  // providing user's organization's name where user works
  data.put("organization", "Tester");
  // providing user's phone number
  data.put("phone", "+123456789");
  // providing user's gender as M for male and F for female
  data.put("gender", "M");
  // providing WWW URL to user's avatar or profile picture
  data.put("picture", "http://example.com/pictures/profile_pic.png");
  // providing local path to user's avatar or profile picture
  data.put("picturePath", "src/main/resources/example.png");
  // providing user's year of birth as integer
  data.put("byear", "1987");

  // with custom key values for this user
  HashMap<String, String> custom = new HashMap<String, String>();
  custom.put("country", "Turkey");
  custom.put("city", "Istanbul");
  custom.put("address", "My house 11");

  Countly.sharedInstance().init("server url", "app key", Config.TYPE_HTTP_CONSUMER);

  Countly.sharedInstance().setUserData(data, custom);
   
  Countly.sharedInstance().setUserData(data);
```

### Enabled debug logger
Call setLoggingEnabled() method to disable or enable debug logger
```    
  // Enabled debug logger
  Countly.sharedInstance().setLoggingEnabled(true);
```

### Build jar
You should build a jar with dependencies by using maven-assembly-plugin.