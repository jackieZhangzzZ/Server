package com.yc.http.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.yc.threadPool.Taskable;

public class TaskService implements Taskable{
	private Socket socket;
	private InputStream iis;
	private OutputStream oos;
	private boolean flag;
	
	
	public TaskService(Socket socket) {
		this.socket = socket;
		try {
			this.iis = this.socket.getInputStream();
			this.oos = this.socket.getOutputStream();
			flag = true;
		} catch (Exception e) {
			YcConstants.logger.error("failed to get stream",e);
			flag = false;
			throw new RuntimeException(e);
		}
	}



	@Override
	public Object doTask() {
		if (flag){
			YcHttpServletRequest request = new YcHttpServletRequest(this.iis);
			YcHttpServletResponse response = new YcHttpServletResponse(this.oos, request);
			Processor processor = null;
			if(request.getRequestURI().endsWith(".do")){
				processor= new DynamicProcessor();
			}else{
				processor = new StaticProcessor();
			}
			processor.process(request, response);
		}
		try {
			this.socket.close();
		} catch (Exception e) {
			YcConstants.logger.error("failed to close connection client",e);
		}
		return null;
	}
	
}
