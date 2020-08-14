package com.yc.http.server;

import com.yc.javax.servlet.ServletRequest;
import com.yc.javax.servlet.ServletResponse;

public interface Processor {
	
	
	public void process(ServletRequest request,ServletResponse response);
}
