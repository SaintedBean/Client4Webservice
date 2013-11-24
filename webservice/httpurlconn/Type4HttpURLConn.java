package webservice.httpurlconn;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;

/**
  * @author SaintedBean
*/
public interface Type4HttpURLConn {
	abstract public void setOption(HttpURLConnection conn)throws Exception;
	abstract public void setBody(HttpURLConnection conn, HashMap<String,Object> body)throws Exception;
	abstract public HashMap<String,Object> convert2Hashmap(String responseStr)throws Exception;
}