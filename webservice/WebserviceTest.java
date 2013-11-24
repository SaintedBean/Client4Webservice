package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import webservice.client.Client4HttpURLConn;
import webservice.httpurlconn.RestJsonType4HttpURLConn;
import webservice.httpurlconn.Type4HttpURLConn;

public class WebserviceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String host = "http://date.jsontest.com";
		String subHost = "";
		String method = "POST";
		
		HashMap<String,Object> body = new HashMap<String,Object>();
		body.put("tntId","t2004");
		body.put("charset","UTF-8");
		
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("tntId","t2004");
		header.put("charset","UTF-8");

		Client4HttpURLConn client = new Client4HttpURLConn(host, new RestJsonType4HttpURLConn());
		HashMap<String,Object> resultMap =  client.connect(header, body, subHost, method);
	}

}
