package com.yc.http.server;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.yc.javax.servlet.Servlet;
import com.yc.javax.servlet.ServletRequest;
import com.yc.javax.servlet.ServletResponse;
import com.yc.javax.servlet.http.HttpServlet;
import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

public class DynamicProcessor implements Processor {

	@Override	//request的	requestURI=>/res/hello.do
	public void process(ServletRequest request, ServletResponse response) {
		//1.去除uri ，从uri中取出请求的servlet名字
		String uri = ((HttpServletRequest)request).getRequestURI();
		String servletName = uri.substring(uri.lastIndexOf("/")+1,uri.lastIndexOf("."));
		//动态字节码加载   到   res/找servlet.class文件
		//URLClassLoader
		URL[] urls = new URL[1];
		
		try {
			urls[0] = new URL("file",null,YcConstants.KITTYSERVER_BASEPATH);
			URLClassLoader ucl = new URLClassLoader(urls);
			Class c = ucl.loadClass(servletName);
			
			Object o = c.newInstance();
			
			if(o!=null  &&  o instanceof Servlet){
				Servlet servlet = (Servlet) o;
				servlet.init();
				((HttpServlet)servlet).service((HttpServletRequest)request, (HttpServletResponse)response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String bodayentity = e.toString();
			String protocal = gen500(bodayentity.getBytes().length);
			PrintWriter pw = response.getWriter();
			pw.println(bodayentity);
			pw.println(protocal);
			pw.flush();
		}
		
	}
	
	private String gen500(   long bodylength ){
		String protocal500="HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/html;charset=utf-8\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		return protocal500;
	}

}
