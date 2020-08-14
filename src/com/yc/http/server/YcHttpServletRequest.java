package com.yc.http.server;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.yc.javax.servlet.http.HttpServletRequest;

public class YcHttpServletRequest implements HttpServletRequest{
	private String method;
	private String protocal;
	private String serverName;
	private int serverPort;
	private String requestURI;
	private String requestURL;
	private String contextPath;
	private String realPath = System.getProperty("user.dir")+File.separatorChar+"webapps";
	
	private InputStream iis;

	public YcHttpServletRequest(InputStream iis) {
		this.iis = iis;
		parse();
	}
	@Override
	public void parse(){
		String requestInfoString = readFromInputStream();
		if(requestInfoString == null || "".equals(requestInfoString)){
			return;
		}
		parseRequestInfoString(requestInfoString);
	}
	
	private void parseRequestInfoString(String requestInfoString){
		StringTokenizer st = new StringTokenizer(requestInfoString);
		if(st.hasMoreTokens()){
			this.method = st.nextToken();
			this.requestURI = st.nextToken();
			this.protocal = st.nextToken();
			this.contextPath = "/"+this.requestURI.split("/")[1];
		}
	}
	
	private String readFromInputStream(){
		String protocal = null;
		StringBuffer sb = new StringBuffer(1024*10);
		int length = -1;
		byte[] bs = new byte[1024*10];
		try {
			length = this.iis.read(bs);
		} catch (Exception e) {
			e.printStackTrace();
			length = -1;
		}
		for(int j=0;j<length;j++){
			sb.append((char)bs[j]);
		}
		protocal = sb.toString();
		return protocal;
	}

	public String getMethod() {
		return method;
	}

	public String getProtocal() {
		return protocal;
	}

	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getRealPath() {
		return realPath;
	}

	//父接口中的参数     Object request.getAttribute("")
	private Map<String,Object> attributes = new HashMap<String,Object>();
	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	
	private Map<String,String> parameters = new HashMap<String,String>();
	@Override
	public String getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public Map<String, String> getParameterMap() {
		return this.parameters;
	}

	
	
	
}
