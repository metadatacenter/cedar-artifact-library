package org.metadatacenter.artifacts.util;

import org.junit.jupiter.api.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertSame;

class ConnectionUtilTlsTest {

  @Test
  void creatingConnectionPreservesJvmTlsVerificationDefaults() throws Exception {
    SSLSocketFactory defaultSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
    HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection(
        "GET", "https://invalid.example/resource", "test-api-key");
    try {
      HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
      assertSame(defaultSocketFactory, HttpsURLConnection.getDefaultSSLSocketFactory());
      assertSame(defaultHostnameVerifier, HttpsURLConnection.getDefaultHostnameVerifier());
      assertSame(defaultSocketFactory, httpsConnection.getSSLSocketFactory());
      assertSame(defaultHostnameVerifier, httpsConnection.getHostnameVerifier());
    } finally {
      connection.disconnect();
    }
  }
}
