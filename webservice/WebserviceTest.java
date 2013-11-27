package test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import webservice.client.Client4HttpURLConn;
import webservice.httpurlconn.RestJsonType4HttpURLConn;
import webservice.httpurlconn.Soap4HttpURLConn;

public class WebserviceTest {

	HashMap<String,Object> body = null;
	Soap4HttpURLConn typeHUC = null;
	
	@Before
	public void setUp() throws Exception {
		body = new HashMap();
		body.put("leaf1", "value1");
		body.put("leaf2", "value2");
		body.put("leaf3", "value3");
		body.put("leaf4", "value4");
		
		typeHUC = new Soap4HttpURLConn();
		
	}

	public void testBisicRestCall() {
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
		
		assertThat( resultMap.get("responseCode").toString(),is("200"));  
		assertThat( resultMap.size(),is(4)); 
	}


	@Test
	public void testSoapBasicProcess() throws Exception{
		body.put("nameSpaceURI", "http://www.journaldev.com/employee");
		body.put("rootName", "Employees");

		String xmlStr = typeHUC.hashmap2xml(body);
		HashMap<String,Object> resultMap = typeHUC.convert2Hashmap(xmlStr);
		System.out.println( "resultMap ### " + resultMap );
		checkResult(resultMap, body);
	}

    private void checkResult(HashMap<String, Object> data, HashMap<String,Object> body) {
        String key = null;
        Object obj = null;
        for( Entry<String, Object> entry : data.entrySet() ){
        	key = entry.getKey();
        	obj = entry.getValue();

        	if( (!"nameSpaceURI".equals(key)) && (!"rootName".equals(key)) ){
        		assertThat( body.get(key),is(obj)); 
            }
        }
    }

	

	
	
}
