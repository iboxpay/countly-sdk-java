package iboxchain;

import com.iboxchain.Config;
import com.iboxchain.Countly;
import com.iboxchain.Event;
import com.iboxchain.UserData;
import com.iboxchain.consumer.HttpConsumer;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CountlyApITest {

  @InjectMocks
  Countly countly;
  @Mock
  HttpConsumer httpConsumer;
  @Mock
  ConcurrentHashMap<String, Object> superProperties = new ConcurrentHashMap<String, Object>();
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Before
  public void setup() {
    Config.SERVER_URL = "https://asia-try.count.ly";
    Config.APP_KEY = "739d0f87d910caf808ea9209888c25d64b6a802a";
    Config.CONSUMER_TYPE = Config.TYPE_HTTP_CONSUMER;
  }

  @Test
  public void recordEvent() throws IOException {
    countly.recordEvent("event1", 2, 2);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(httpConsumer).prepareRequestData(captor.capture(), eq(Config.TYPE_SEND_EVENT));
    assert (captor.getValue().contains("\"sum\":2"));
    assert (captor.getValue().contains("count\":2"));
    assert (captor.getValue().contains("\"key\":\"event1\""));
  }

  @Test
  public void recordEmptyEvent() throws IOException {
    countly.recordEvent(null, 2, 2);
    verify(httpConsumer, times(0)).prepareRequestData(anyString(), anyInt());

    countly.recordEvent("", 2, 2);
    verify(httpConsumer, times(0)).prepareRequestData(anyString(), anyInt());
  }


  @Test
  public void callSendDataMethod() throws IOException, HttpConsumer.HttpConsumerException {
    countly.recordEvent("event1", 2, 2);
    verify(httpConsumer, times(1)).sendData(anyString());

    HashMap<String, String> data = new HashMap<String, String>();
    data.put("name", "First name Last name");
    data.put("username", "nickname");
    data.put("email", "test@test.com");
    data.put("organization", "Tester");
    data.put("phone", "+123456789");
    data.put("gender", "M");
    //provide url to picture
    data.put("picture", "http://example.com/pictures/profile_pic.png");
    data.put("byear", "1987");
    countly.sendUserData(data);
    verify(httpConsumer, times(2)).sendData(anyString());
  }

  @Test
  public void sendUserData() {
    final HashMap<String, String> data = new HashMap<String, String>();
    data.put("name", "First name Last name");
    data.put("username", "nickname");
    data.put("email", "test@test.com");
    data.put("organization", "Tester");
    data.put("phone", "+123456789");
    data.put("gender", "M");
    //provide url to picture
    data.put("picture", "http://example.com/pictures/profile_pic.png");
    data.put("byear", "1987");

    when(countly.sendUserData(data)).then(
        new Answer() {
          @Override
          public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            String userData = UserData.toJSON().toString();
            assert (userData.contains("\"name\":\"First name Last name\""));
            assert (userData.contains("\"username\":\"nickname\""));
            assert (userData.contains("\"email\":\"test@test.com\""));
            assert (userData.contains("\"organization\":\"Tester\""));
            assert (userData.contains("\"phone\":\"+123456789\""));
            assert (userData.contains("\"gender\":\"M\""));
            assert (userData.contains("\"picture\":\"http://example.com/pictures/profile_pic.png\""));
            assert (userData.contains("\"byear\":1987"));
            assert (userData.contains("\"custom\":{}"));
            return null;
          }
        }
    );
    countly.sendUserData(data);
  }

  @Test
  public void sendCustomUserData() {
    final HashMap<String, String> data = new HashMap<String, String>();
    data.put("name", "First name Last name");
    data.put("username", "nickname");
    data.put("email", "test@test.com");
    data.put("organization", "Tester");
    data.put("phone", "+123456789");
    data.put("gender", "M");
    //provide url to picture
    data.put("picture", "http://example.com/pictures/profile_pic.png");
    data.put("byear", "1987");

    //providing any custom key values to store with user
    HashMap<String, String> custom = new HashMap<String, String>();
    custom.put("country", "Turkey");
    custom.put("city", "Istanbul");
    custom.put("address", "My house 11");

    when(countly.sendUserData(data, custom)).then(
        new Answer() {
          @Override
          public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            String userData = UserData.toJSON().toString();
            assert (userData.contains("\"name\":\"First name Last name\""));
            assert (userData.contains("\"username\":\"nickname\""));
            assert (userData.contains("\"email\":\"test@test.com\""));
            assert (userData.contains("\"organization\":\"Tester\""));
            assert (userData.contains("\"phone\":\"+123456789\""));
            assert (userData.contains("\"gender\":\"M\""));
            assert (userData.contains("\"picture\":\"http://example.com/pictures/profile_pic.png\""));
            assert (userData.contains("\"byear\":1987"));
            assert (userData.contains("\"custom\":{\"country\":\"Turkey\",\"address\":\"My house 11\",\"city\":\"Istanbul\"}"));
            return null;
          }
        }
    );
    countly.sendUserData(data, custom);
  }

  @Test
  public void sendNullUserData() {
    when(countly.sendUserData(null, null)).then(
        new Answer() {
          @Override
          public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            String userData = UserData.toJSON().toString();
            assert (userData.equals("{\"custom\":{}}"));
            return null;
          }
        }
    );
    countly.sendUserData(null, null);
  }

  @Test
  public void registerSuperProperties() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("ip", "127.0.0.1");
    map.put("port", "1080");
    countly.registerSuperProperties(map);

    verify(superProperties, times(1)).put("ip", "127.0.0.1");
    verify(superProperties, times(1)).put("port", "1080");
  }

  @Test
  public void clearSuperProperties() {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("ip", "127.0.0.1");
    map.put("port", "1080");
    countly.registerSuperProperties(map);

    verify(superProperties, times(1)).put("ip", "127.0.0.1");
    verify(superProperties, times(1)).put("port", "1080");

    countly.clearSuperProperties();
    verify(superProperties, times(1)).clear();
  }

  @Test
  public void prepareRequestData() throws JSONException, UnsupportedEncodingException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("param1", 1);
    map.put("param2", "2");
    map.put("param3", 3.1);
    Event event = new Event("eventTest");
    event.setCount(1);
    event.setSum(1);
    event.setSegmentation(map);
    HttpConsumer consumer = new HttpConsumer(null);

    String requestData = consumer.prepareRequestData(event.toJSON().toString(), Config.TYPE_SEND_EVENT);
    requestData = java.net.URLDecoder.decode(requestData, "UTF-8");
    assert (requestData.contains("app_key=739d0f87d910caf808ea9209888c25d64b6a802a"));
    assert (requestData.contains("timestamp="));
    assert (requestData.contains("hour="));
    assert (requestData.contains("dow="));
    assert (requestData.contains("tz="));
    assert (requestData.contains("sdk_version=1.0.0"));
    assert (requestData.contains("sdk_name=countly-sdk-java"));
    assert (requestData.contains("events="));
    assert (requestData.contains("device_id="));
    assert (requestData.contains("checksum="));
    assert (requestData.contains("\"key\":\"eventTest\""));
    assert (requestData.contains("\"param1\":1"));
    assert (requestData.contains("\"param2\":\"2\""));
    assert (requestData.contains("\"param3\":3.1"));

  }
}
