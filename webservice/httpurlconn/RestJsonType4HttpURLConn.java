package webservice.httpurlconn;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;



/**
  * @author SaintedBean
*/
public class RestJsonType4HttpURLConn implements Type4HttpURLConn {

	public void setOption(HttpURLConnection conn) throws Exception{
		setOptionByMethod(conn);
	}
	
	public void setBody(HttpURLConnection conn, HashMap<String,Object> body) throws Exception{
		String method = conn.getRequestMethod();
		if("GET".equals(method.toUpperCase())){
			return;
		}
			
		String bodyStr = hashmap2json(body);
		String charset = conn.getRequestProperty("charset");
		PrintWriter pw = new PrintWriter( new OutputStreamWriter( conn.getOutputStream(), charset));
		pw.write(bodyStr);
		pw.flush();
	}
	
	public HashMap<String,Object> convert2Hashmap(String responseStr) throws Exception{
		return json2hashmap( responseStr);
	}
	

	private void setOptionByMethod(HttpURLConnection conn) throws Exception{
		String method = conn.getRequestMethod();
		
		HashMap<String,Integer> plan = new HashMap<String,Integer>();
		plan.put("POST", 1);
		plan.put("PUT", 1);
		plan.put("DELETE", 1);
		plan.put("GET", 4);

		switch( plan.get(method.toUpperCase()) ){
			case 1 :	
				conn.setDoOutput(true);
			default : 
				conn.setDoInput(true);
				conn.setRequestMethod(method);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
		}
		
	}
	
	private String hashmap2json(Map<String, Object> data) throws Exception{ 
        JSONObject json = new JSONObject(data);
        String jsonStr =  json.toString(2);
        return jsonStr;
	} 


	private HashMap<String, Object> json2hashmap(String jsonStr) throws Exception{
		HashMap<String, Object> outMap =  new HashMap<String, Object>();
		json2hashmapRecursion(jsonStr,outMap);
		return outMap;
	}

	private void json2hashmapRecursion(String jsonStr, HashMap<String, Object> outMap) throws Exception{
		String name = null;
		Object value = null;
		try {
			JSONObject jsonData = new JSONObject(jsonStr);
			
			Iterator<String> nameItr = jsonData.keys(); 
			while(nameItr.hasNext()) {
				name = nameItr.next();
				value = jsonData.get(name);
				outMap.put(name, value);
				
				if( value instanceof String){
					json2hashmapRecursion((String)value, outMap);
				}
			}
		} catch (JSONException e) {
			return;
		}
	}

	
}