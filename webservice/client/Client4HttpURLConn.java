package webservice.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import webservice.httpurlconn.Type4HttpURLConn;

/**
  * @author SaintedBean
*/
public class Client4HttpURLConn {
	private String host;
	Type4HttpURLConn type4HttpURLConn;

	public Client4HttpURLConn(String host, Type4HttpURLConn type4HttpURLConn) {
		super();
		this.host = host;
		this.type4HttpURLConn = type4HttpURLConn;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Type4HttpURLConn getHttpURLConnType() {
		return type4HttpURLConn;
	}

	public void setHttpURLConnType(Type4HttpURLConn type4HttpURLConn) {
		this.type4HttpURLConn = type4HttpURLConn;
	}

	public HashMap<String,Object> connect(HashMap<String, String> header, HashMap<String, Object> body, String subHost, String method) { 
		 
        HttpURLConnection conn = null;
        BufferedReader br = null;
        StringBuilder sb  = null;
        HashMap<String,Object> responseMap = null;
	    try { 
	    	int timeout = 1000;
	    	
	        URL url = new URL(host + subHost); 
	        printLog("URL ### " + url );
	        conn = (HttpURLConnection) url.openConnection(); 
	        conn.setConnectTimeout(timeout);
	        conn.setReadTimeout(timeout);
	        
	        conn.setRequestMethod(method); 
	        
	        hashmap2header(conn,header);
	        type4HttpURLConn.setOption(conn);
	        printLog("REQUEST HEADER ### " + header );
	        
	        type4HttpURLConn.setBody(conn, body);
	        printLog("REQUEST BODY ### " + body );

	        int status = conn.getResponseCode();

	        printLog("RESPONSE Code ### " + status );
            sb = new StringBuilder();
	        switch (status) {
	            case 200:
	            case 201:
	                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	    	        printLog("RESPONSE BODY ### " + sb.toString() ); 
	    	        responseMap = type4HttpURLConn.convert2Hashmap(sb.toString());
	            default :
	            	responseMap.put("responseCode", status);
	    	        printLog("RESPONSE MAP ### " + responseMap ); 
	        }
	        
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } finally{
	    	conn.disconnect(); 
            try {
            	if( null != br ){
            		br.close();
            	}
			} catch (IOException e) {
		        throw new RuntimeException(e); 
			}
	    }
	    return responseMap;
	} 
	
	private void hashmap2header(HttpURLConnection conn, Map<String, String> data ) { 
	    try { 
	    	String key = null;
	    	Iterator<String> iterator = data.keySet().iterator();
	    	while(iterator.hasNext()){
	    		key = (String) iterator.next();
	    		conn.setRequestProperty(key, data.get(key));
	    	}
	    	
	    	// Default 
	    	if( conn.getRequestProperty("charset").isEmpty() ){
	    		conn.setRequestProperty("charset", "UTF-8");
	    	}
	    	
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 
	} 
	
	private void printLog(String logStr ) { 
        System.out.println(logStr); 
	}
	
}