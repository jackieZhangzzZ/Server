版 本一:   http服务器，提供静态资源访问.        

     请求部分:   
          3)图片
          	GET /xxx/xxx.jpg HTTP/1.1
			Referer: http://localhost:8080/wowotuanStatic/index.html
			Sec-Fetch-Dest: image
			User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.16
    
    响应部分:
          3)图片:
           HTTP/1.1 200 OK
           Accept-Ranges: bytes
			Content-Length: 92174      **
			Content-Type: image/jpeg   **
			Date: Sat, 11 Apr 2020 02:22:41 GMT
			ETag: W/"92174-1586571146000"
			Last-Modified: Sat, 11 Apr 2020 02:12:26 GMT
			Server: Apache-Coyote/1.1
			
			响应实体
					
			
服务器功能:
1. 接收客户端的请求解析出它请求的文件名及相对路径. 
2. 查找这个文件是否存在，  不存在-> 404页面
          存在 ->  
                1）读取这个资源
                2) 构建响应协议     Content-Type:  浏览器根据响应中的  Content-Type来决定使用什么引擎来解析数据
                          text/html:  html  -> html渲染
                          css   :    css引擎
                          js:     js引擎
                          图片:   图片引擎. 
                          
                        Content-Length                      

用到的技术:
			1. ServerSocket  ->  Socket  
			2. 多线程
			3. log4j
			4. dom解析


KittyServer:
	xml的解析端口; 
   ServerSocket ss=new ServerSocket(  端口) ;

    Socket s=ss.accept();
    Thread t=new Thread(  new 任务(  s )  );
    t.start();
    
    
注意的问题:
  1. HttpServletRequest类中的  private String readFromInputStream()方法，要一次读取所有的请求头数据. 
  
  
  
  版 本二:   使用了线程池的http服务器，提供静态资源访问. 

参考了tomcat 的线程池配置:
    <Executor name="tomcatThreadPool" namePrefix="catalina-exec-" maxThreads="150"  minSpareThreads="4"/>
    
    
 问题:
 1. 线程池代码已经写好,但我们的任务接口为  Taskable,而不是 Runnable, 
   TaskService要改写接口为  Taskable,实现 doTask().
 2. 线程池初始化应在程序启动时， 读取  xml中是否有配置 Executor,如果有，则启动线程池，没有，则不初始化. 
     取到一个socket后，判断是否有线程池，有则使用  process(), 没有则使用 Thread. 
  
  
  版 本三:   servlet服务器，提供动态资源(  class字节码  ->  xxxxServlet.class   )  访问. 


要解决的问题: 
  1. 访问路径的映射: 
       实际访问的是一个   servlet的class:     如:  Hello.class
       但浏览器的地址栏却是一个url:       http://localhost:9090/wowotuan/Hello.do
       在这里，我们*简化处理了*，做了一个*约定*，只要请求资源的后缀名为  .do, 则表明是一个动态资源请求.  
       回顾一下真实的web项目，我们的请求应该是怎样处理的?  ....
             http://localhost:9090/wowotuan/Hello.do
             http://localhost:9090/wowotuan/hello.html
             http://localhost:9090/wowotuan/Hello
           
          注解:   @WebServlet
          web.xml:  
               <serlvet>
                   <servlet-class>
                   <serlvet-name>
               </servlet>
               <servlet-mapping>
               		<servlet-name>
               		<url-pattern>
               </servlet>
               
          请求过来 ->  tomcat解析出requestURI -> web.xml看是否有这个映射  ->有，则实例化对应的servlet  ........
                                  									  -> 没有，是否有这个对应的静态资源.....
                                  									  
                                  									  ->  都没有，则显示  404.html
    2. 动静资源处理的分离: 
         TaskService.java  ->   doTask() ->     处理完请求，解析出  requestURI(   hello.do/hello.html)  后,根据后缀名来判断是静态还是动态资源  -> 
            分发处理(  静态资源交给静态处理器处理，动态资源交给动态资源处理器处理.  ) 
             
       我们这里会做一个资源处理的接口Processor ->   process(   )，如果解析出来是动态资源，则使用 DynamicProcessor对象， 否则使用StaticProcessor对象
       
       DynamicProcessor对象的功能:  加载  HelloServlet.class字节码
       StaticProcessor对象的功能:   按前面版本的功能调用  response.sendRedirect()返回静态资源即可. 
       
       
       分发处理(  静态资源交给静态处理器处理，动态资源交给动态资源处理器处理.  ) 伪代码
            Processo p=null;
			if(){
				p=new 静态资源处理器(  request, response  );   -> response.sendRedirect();
			}else{
				p=new 动态资源处理器(  request, response);   -> hello.do  -> Hello.class  ->  load  ->  生命周期方法
			}
			p.process();
       
       
       
       
  2. 当我的  HttpServletRequest已经解析出来了  ->  Hello.do 知道对应的servlet为  Hello.class后，如何加载它呢?
     这就要考虑一下 统一一个标准的问题了: 
          Hello.class   以后是用户开发的,   而  KittyServer是服务器厂商开发的， 服务器厂商如何知道用户要开发的HelloServlet是什么样的，有哪些方法?如何调用呢?
          这就是j2ee规范的由来，   sun公司意识到了这一点，它开发了一套j2ee规范，规定了 web服务器的开发标准，这样无论是用户还是服务器厂商都按这个标准开发. 
          
          j2EE规范: 
              jdbc, xml, web service, email , rmi, web规范.......
              
          那么j2ee的针对web开发的规范是怎么样的呢?
              JSP, 
              Servlet
              Filter
              Listener
              
              
          Servlet规范: 
               javax.servlet.Servlet:接口
                            destroy() ,  init(ServletConfig config)       service(ServletRequest req, ServletResponse res)   
                子类abstract  javax.servlet.GenericServlet:子类:  init() 
                
              针对http协议的子类  javax.servlet.http.HttpServlet:    http协议的相关内容    method, protocal, .....header.
                            doGet(),  doPost(HttpServletRequest req, HttpServletResponse resp) ,  service(HttpServletRequest req, HttpServletResponse resp) 
                       
          
          
             ServletRequest->  HttpServletRequest
             ServletResponse -> HttpServletResponse
             
            动态资源处理器->   Hello.do找到Hello.class  调用Hello.class中的方法,哪些方法? 
                   init(),destroy(), service, doGet(), doPost()..... 
                   
                  Hello.class构造方法 -> 存到Map中-> init()->  service() 判断  method类型  -> doGet()/doPost()
                                     -> 判断Map中是否有这个 Hello的servlet,-》  取出这个servlet实例,调用  ->  service() 判断  method类型  -> doGet()/doPost()
                          
                  
  		
  3.  了解了这个规范之后，我们的开发将分成两个方面走,
      1)是客户(第三方公司)开发Servlet部分:  写一个类继承自HttpServlet，重写生命周期方法，再加上自己的业务实现即可. 
      2)是服务器厂商的服务器代码部分:   考虑:  1.  在获得客户端请求后，包装成  HttpServletRequest对象， HttpServletResponse对象, 
                       2. 判断是动态请求还是静态请求，分别实例化不同的处理类,再调用处理方法,对于动态请求， 加入生命周期的回调部分.  

用到的技术:
1. 设计模式
2. 动态字节码加载
  
  
  
  
  
  
  
  
  
  
  
  