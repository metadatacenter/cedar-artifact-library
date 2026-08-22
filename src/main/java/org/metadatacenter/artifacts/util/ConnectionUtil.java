package org.metadatacenter.artifacts.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class ConnectionUtil {

  //@formatter:off
  private static final TrustManager[] trustAllCerts = new TrustManager[1];
  static {
    trustAllCerts[0] = new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
      public void checkClientTrusted(X509Certificate[] certs, String authType) {}
      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    };
  }

  private static final ObjectMapper mapper = new ObjectMapper();

  //@formatter:on

  public static ObjectNode readJsonResponseMessage(InputStream is) {

    try {
      JsonNode jsonNode = mapper.readTree(readResponseMessage(is));

      if (!jsonNode.isObject())
        throw new RuntimeException("Expecting JSON object");

      return (ObjectNode)jsonNode;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error reading JSON object: " + e.getMessage());
    }
  }

  public static String readResponseMessage(InputStream is) {
    StringBuffer sb = new StringBuffer();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
      String messageLine;
      while ((messageLine = br.readLine()) != null) {
        sb.append(messageLine);
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    return sb.toString();
  }

  /**
   * Read a response body verbatim, preserving its line structure. Required for line-oriented
   * formats such as YAML, whose meaning depends on line breaks and indentation.
   */
  public static String readResponseBody(InputStream is) {
    try {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static HttpURLConnection createAndOpenConnection(String requestMethod, String endpoint, String apiKey) throws IOException {
    return createAndOpenConnection(requestMethod, endpoint, apiKey, null);
  }

  /**
   * @param acceptMediaType the media type to request through the Accept header, selecting a
   *                        representation from a server that negotiates its response format;
   *                        no Accept header is sent when it is null
   */
  public static HttpURLConnection createAndOpenConnection(String requestMethod, String endpoint, String apiKey,
                                                          String acceptMediaType) throws IOException {
    ignoreSSLCheckingByAcceptingAnyCertificates();
    try {
      URL url = new URL(endpoint);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod(requestMethod);
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "apiKey " + apiKey);
      if (acceptMediaType != null) {
        conn.setRequestProperty("Accept", acceptMediaType);
      }
      return conn;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static void ignoreSSLCheckingByAcceptingAnyCertificates() {
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> 1 == 1);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static void logErrorMessageAndThrowException(String message, final HttpURLConnection conn) {
    String response = ConnectionUtil.readResponseMessage(conn.getErrorStream());
    throw new RuntimeException(message + "\nError message: " + response);
  }
}
