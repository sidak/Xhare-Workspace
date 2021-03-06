package jsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONReader {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url, boolean useProxy) throws IOException, JSONException {
    
	if(useProxy) enableProxy();
    
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

private static void enableProxy() {
	System.setProperty("http.proxyHost", "proxyeast.idns.xerox.com");
    System.setProperty("http.proxyPort", "8000");
}

  public static void main(String[] args) throws IOException, JSONException {
    JSONObject json = readJsonFromUrl("http://13.218.151.99:8080/otp/routers/default/plan?fromPlace=12.750005%2C77.780467&toPlace=12.750005%2C77.780467&mode=CAR", false);
    System.out.println(json.toString());
  }
}