package com.yc.javax.servlet;

public interface Servlet {
	public void init();
	
	public void destory();
	
	public void service(ServletRequest request,ServletResponse response);
}
