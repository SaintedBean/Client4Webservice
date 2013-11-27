package webservice.httpurlconn;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



/**
  * @author SaintedBean
*/
public class Soap4HttpURLConn implements Type4HttpURLConn {

	public void setOption(HttpURLConnection conn) throws Exception{
		setOptionByMethod(conn);
	}
	
	public void setBody(HttpURLConnection conn, HashMap<String,Object> body) throws Exception{
		String method = conn.getRequestMethod();
		if("GET".equals(method.toUpperCase())){
			return;
		}
			
		String bodyStr = hashmap2xml(body);
		String charset = conn.getRequestProperty("charset");
		PrintWriter pw = new PrintWriter( new OutputStreamWriter( conn.getOutputStream(), charset));
		pw.write(bodyStr);
		pw.flush();
	}
	
	public HashMap<String,Object> convert2Hashmap(String responseStr) throws Exception{
		return doc2HashMap(xmlStr2Dom( responseStr ));
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
	
	public String hashmap2xml(HashMap<String, Object> data) throws Exception{ 
		return document2xml( hashmap2document(data));
	} 


	public Document hashmap2document(HashMap<String, Object> inputData) throws Exception{ 
		Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        
        doc = dBuilder.newDocument();
        doc.setXmlStandalone(false);
        doc.setXmlVersion("1.0");
        
        HashMap<String, Object> data = duplicateHashMap(inputData);

        String nameSpaceURI = null;
        String rootName = null;
        if( null != data.get("nameSpaceURI") && null != data.get("rootName") ){
        	nameSpaceURI =  (String) data.get("nameSpaceURI");
            rootName = (String) data.get("rootName");
            data.remove("nameSpaceURI");
            data.remove("rootName");
        }else{
        	// TODO throw
        }
        
        //Create rootElement.
        Element rootElement =
            doc.createElementNS(nameSpaceURI, rootName);
        doc.appendChild(rootElement); //append root element to document
        
        //add elements to Document
        String key = null;
        Object obj = null;
        for( Entry<String, Object> entry : data.entrySet() ){
        	key = entry.getKey();
    		obj = entry.getValue();
    		rootElement.appendChild(makeLastNode(doc, key, (String)obj));
        }
        
		return doc;
	} 

    private HashMap<String, Object> duplicateHashMap(HashMap<String, Object> data) {
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	
        String key = null;
        Object obj = null;
        for( Entry<String, Object> entry : data.entrySet() ){
        	key = entry.getKey();
        	obj = entry.getValue();
        	result.put(key, obj);
        }
    
        return result;
    }

    private Node makeLastNode(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

	public String document2xml(Document doc) throws Exception{ 
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		return output;
	} 
	

	private HashMap<String, Object> doc2HashMap(Document doc) throws Exception{
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		
		if( !doc.hasChildNodes() ){
			// TODO throw
		}
		
		NodeList nodeList = doc.getChildNodes();
		Node rootNode = nodeList.item(0);
		System.out.println( "ROOT NODE ### " + rootNode.getNodeName() );
		doc2HashMapRecursion( rootNode, resultMap);

	    return (HashMap<String, Object>) resultMap.get(rootNode.getNodeName());
	}
	
	

	private void doc2HashMapRecursion(Node node, HashMap<String, Object> map ) throws Exception{
		if( !node.hasChildNodes() ){ // lastNode
			map.put(node.getNodeName(), node.getNodeValue());
			return;
		}else if( node.getChildNodes().getLength() == 1 &&  node.getChildNodes().item(0).getNodeName().equals("#text") ){ // lastNode
			map.put(node.getNodeName(), node.getChildNodes().item(0).getNodeValue() );
			return;
		}

		HashMap<String, String> checkOverlapMap = new HashMap<String, String>();
		
		NodeList nodeList = node.getChildNodes();
		int size = nodeList.getLength();
		Node childNode = null;
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> childMap = new HashMap<String, Object>();
		for(int i=0; i<size; i++){ 
			
			childNode = nodeList.item(i);
			if( checkOverlapMap.containsKey( childNode.getNodeName() ) ){ // add a list of childNodes
				HashMap<String, Object> doubleChildMap = new HashMap<String, Object>();
				if( childMap.containsKey(childNode.getNodeName()) ){ // save the first childNode of the chilNode list
					list.add( (HashMap<String, Object>) childMap.get(childNode.getNodeName()) );
				}
				doc2HashMapRecursion( childNode, doubleChildMap);
				list.add(doubleChildMap);
				
				childMap.put(childNode.getNodeName(), list );
			}else{ 	// add a childNode
				doc2HashMapRecursion( childNode, childMap);
			}
			checkOverlapMap.put(childNode.getNodeName(), "Y");
		}

		map.put(node.getNodeName(), childMap);	
	}
	
	
	
	private Document xmlStr2Dom(String xmlSource) throws Exception{
	    // Parse the given input
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(new InputSource(new StringReader(xmlSource)));

	    return doc;
	}
	
}