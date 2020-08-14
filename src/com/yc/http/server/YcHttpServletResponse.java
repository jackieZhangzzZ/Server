package com.yc.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.yc.javax.servlet.http.HttpServletResponse;

public class YcHttpServletResponse implements HttpServletResponse{
	private OutputStream oos;
	private YcHttpServletRequest request;
	private String contentType;
	
	public YcHttpServletResponse(OutputStream oos, YcHttpServletRequest request) {
		super();
		this.oos = oos;
		this.request = request;
	}
	
	public void sendRedirect(){
		String responseprotocal = null;
		byte[] fileContent = null;
		String uri = request.getRequestURI();
		File f = new File(request.getRealPath(),uri);
		if(!f.exists()){
			fileContent = readFile(new File(request.getRealPath(),"/404.html"));
			responseprotocal = gen404(fileContent.length);
		}else{
			fileContent = readFile(f);
			responseprotocal = gen200(fileContent.length);
		}
		try {
			oos.write(responseprotocal.getBytes());
			oos.flush();
			oos.write(fileContent);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(oos != null){
				try {
					oos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	private byte[] readFile(File f){
		FileInputStream fis = null;
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(f);
			byte[] bs = new byte[1024];
			int length = -1;
			while((length = fis.read(bs,0,bs.length))!=-1){
				boas.write(bs, 0, length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return boas.toByteArray();
	}
	
	/**
	 * 要考虑静态文件的类型
	 * @param bodylength
	 *            , 内容的长度
	 * @return
	 */
	private String gen200(long bodylength) {
		String uri = this.request.getRequestURI(); // 取出要访问的文件的地址
		int index = uri.lastIndexOf(".");
		if (index >= 0) {
			index = index + 1;
		}
		String fileExtension = uri.substring(index); // 文件的后缀名
		String protocal200 = "";
		if ("JPG".equalsIgnoreCase(fileExtension)
				|| "JPEG".equalsIgnoreCase(fileExtension)) {
			protocal200 = "HTTP/1.1 200 OK\r\nContent-Type: "+"\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		} else if ("PNG".equalsIgnoreCase(fileExtension)) {
			protocal200 = "HTTP/1.1 200 OK\r\nContent-Type: "+ "\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		} else if ("json".equalsIgnoreCase(fileExtension)) {
			protocal200 = "HTTP/1.1 200 OK\r\nContent-Type: "+"\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		} else if ("css".equalsIgnoreCase(fileExtension)) {
			protocal200 = "HTTP/1.1 200 OK\r\nContent-Type: text/css\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		} else if ("js".equalsIgnoreCase(fileExtension)) {
			protocal200 = "HTTP/1.1 200 OK\r\nContent-Type: text/javascript\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		}else {
			protocal200 = "HTTP/1.0 200 OK\r\nContent-Type: "+"\r\nContent-Length: "
					+ bodylength + "\r\n\r\n";
		}
		return protocal200;
	}

	/**
	 * 产生404响应
	 * 
	 * @return
	 */
	private String gen404(long bodylength) {
		String protocal404 = "HTTP/1.1 404 File Not Found\r\nContent-Type: text/html\r\nContent-Length: "
				+ bodylength + "\r\n\r\n";
		return protocal404;
	}

	@Override
	public PrintWriter getWriter() {
		PrintWriter pw = new PrintWriter(this.oos);
		return pw;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

}
