package com.iboxchain.consumer;

import com.iboxchain.Config;
import com.iboxchain.UserData;
import com.iboxchain.utils.CommonUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Map;

/**
 * Prepare request data and send data(event data or user data) to server.
 */
public class HttpConsumer {

  private static final int BUFFER_SIZE = 256;
  private static final int CONNECT_TIMEOUT_MILLIS = 2000;
  private static final int READ_TIMEOUT_MILLIS = 10000;

  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
  private final Map<String, String> httpHeaders;
  private Logger log = Logger.getLogger(HttpConsumer.class);

  public static class HttpConsumerException extends Exception {

    HttpConsumerException(String error, String sendingData, int httpStatusCode, String
        httpContent) {
      super(error);
      this.sendingData = sendingData;
      this.httpStatusCode = httpStatusCode;
      this.httpContent = httpContent;
    }

    String getSendingData() {
      return sendingData;
    }

    int getHttpStatusCode() {
      return httpStatusCode;
    }

    String getHttpContent() {
      return httpContent;
    }

    final String sendingData;
    final int httpStatusCode;
    final String httpContent;
  }

  public HttpConsumer(Map<String, String> httpHeaders) {
    this.httpHeaders = httpHeaders;
  }

  /**
   * Send data to server
   *
   * @param data url and params
   */
  public void sendData(String data) throws IOException, HttpConsumerException {
    URL url = new URL(data);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setReadTimeout(READ_TIMEOUT_MILLIS);
    conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
    conn.setRequestMethod("GET");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf8");

    if (httpHeaders != null) {
      for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (key != null && value != null && !key.isEmpty()) {
          conn.addRequestProperty(key, value);
        }
      }
    }

    String picturePath = UserData.getPicturePathFromQuery(url);
    if (!picturePath.equals("")) {
      // Uploading files:
      // http://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests

      File binaryFile = new File(picturePath);
      conn.setDoOutput(true);
      // Just generate some unique random value.
      String boundary = Long.toHexString(System.currentTimeMillis());
      // Line separator required by multipart/form-data.
      String CRLF = "\r\n";
      String charset = "UTF-8";
      conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      OutputStream output = conn.getOutputStream();
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
      // Send binary file.
      writer.append("--").append(boundary).append(CRLF);
      writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"").append(binaryFile.getName()).append("\"").append(CRLF);
      writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
      writer.append("Content-Transfer-Encoding: binary").append(CRLF);
      writer.append(CRLF).flush();
      FileInputStream fileInputStream = new FileInputStream(binaryFile);
      byte[] buffer = new byte[1024];
      int len;
      try {
        while ((len = fileInputStream.read(buffer)) != -1) {
          output.write(buffer, 0, len);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      // Important before continuing with writer!
      output.flush();
      // CRLF is important! It indicates end of boundary.
      writer.append(CRLF).flush();
      fileInputStream.close();

      // End of multipart/form-data.
      writer.append("--").append(boundary).append("--").append(CRLF).flush();
    }

    InputStream stream = null;
    try {
      conn.connect();
      stream = conn.getInputStream();
      int responseCode = conn.getResponseCode();
      String httpContent = slurp(stream);
      if (responseCode < 200 || responseCode >= 300) {
        throw new HttpConsumerException(
            String.format("Unexpected response %d from countly-SDK-Java: %s", responseCode, httpContent), data,
            responseCode, httpContent);
      } else {
        if (Config.IS_LOG_ENABLE) {
          log.info("Send Data(countly-sdk-java):" + "\n url=" + url + "\n response:" + " message=" + httpContent + ",responseCode=" + responseCode);
        }
      }
    } finally {
      if (stream != null) {
        stream.close();
      }
      conn.disconnect();
    }
  }

  /**
   * Prepare Request Data
   * Config.TYPE_SEND_EVENT: prepare event request data
   * Config.TYPE_SEND_USER_DATA: prepare user request data
   *
   * @param data even data or user data
   * @param type Config.TYPE_SEND_EVENT or Config.TYPE_SEND_USER_DATA
   * @return request data
   */
  public String prepareRequestData(String data, int type) throws UnsupportedEncodingException {
    String requestData = "app_key=" + Config.APP_KEY
        + "&timestamp=" + CommonUtil.currentTimestampMs()
        + "&hour=" + CommonUtil.currentHour()
        + "&dow=" + CommonUtil.currentDayOfWeek()
        + "&tz=" + CommonUtil.getTimezoneOffset()
        + "&sdk_version=" + Config.SDK_VERSION
        + "&sdk_name=" + Config.SDK_NAME;

    if (type == Config.TYPE_SEND_EVENT) {
      requestData += "&events=" + java.net.URLEncoder.encode(data, "UTF-8");
    }
    if (type == Config.TYPE_SEND_USER_DATA) {
      requestData += data;
    }

    requestData += "&device_id=" + Config.DEVICE_ID;
    return requestData + "&checksum=" + sha1Hash(requestData);
  }

  /**
   * Hash request data. Make sure the data is not modified
   *
   * @param toHash
   * @return
   */
  private static String sha1Hash(String toHash) {
    String hash = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] bytes = toHash.getBytes("UTF-8");
      digest.update(bytes, 0, bytes.length);
      bytes = digest.digest();
      // This is ~55x faster than looping and String.formating()
      hash = bytesToHex(bytes);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return hash;
  }

  /**
   * http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java=
   */
  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars).toLowerCase();
  }

  /**
   * Get response message from server
   *
   * @param in InputStream from HttpURLConnection
   * @return response message from server
   */
  private String slurp(InputStream in) throws IOException {
    final StringBuilder out = new StringBuilder();
    InputStreamReader reader = new InputStreamReader(in, "utf8");

    char[] readBuffer = new char[BUFFER_SIZE];
    int readCount = 0;
    do {
      readCount = reader.read(readBuffer);
      if (readCount > 0) {
        out.append(readBuffer, 0, readCount);
      }
    } while (readCount != -1);

    return out.toString();
  }

}