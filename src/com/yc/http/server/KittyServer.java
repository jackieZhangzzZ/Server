package com.yc.http.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yc.threadPool.ThreadPoolManager;

public class KittyServer {
	private ThreadPoolManager tmp;
	
	public static void main(String[] args) throws Exception {
		KittyServer ks = new KittyServer();
		ks.startServer();
	}
	
	boolean flag = false;
	private void startServer() throws Exception{
		ServerSocket ss = null;
		int port = parseServerXml();
		tmp = new ThreadPoolManager(10,null);
		try {
			ss = new ServerSocket(port);
			YcConstants.logger.debug("kitty server is starting,and listening to port"+ss.getLocalPort());
		} catch (Exception e) {
			YcConstants.logger.error("kitty server's port"+port+"is already in use...");
			return;
		}
		while(!flag){
			try {
				Socket s = ss.accept();
				YcConstants.logger.debug("a client "+s.getInetAddress()+"is connecting to kitty server....");
				TaskService ts = new TaskService(s);
				tmp.process(ts);
//				Thread t = new Thread(ts);
//				t.start();
			} catch (Exception e) {
				YcConstants.logger.error("client is down,cause:"+e.getMessage());
			}
		}
		
	}
	
	private int parseServerXml() throws Exception{
		List<Integer> list = new ArrayList<Integer>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(YcConstants.SERVERCONFIG);
			NodeList nl = doc.getElementsByTagName("Connector");
			for (int i = 0; i < nl.getLength(); i++) {
				Element node = (Element) nl.item(i);
				list.add(Integer.parseInt(node.getAttribute("port")));
			} 
		} catch (Exception e) {
			throw e;
		}
		return list.get(0);
	}
}
