package com.yc.threadPool;

import java.util.Vector;

public class ThreadPoolManager {
	private int initThreads; //初始线程数
	public Vector vector;
	
	private MyNotify notify;
	
	public void setInitThreads(int initThreads) {
		this.initThreads = initThreads;
	}
	
	public ThreadPoolManager(int threadCount,MyNotify notify){
		this.notify=notify;
		this.setInitThreads(threadCount);
		
		System.out.println("线程池开始......");
		
		vector = new Vector();
		for(int i=1;i<=initThreads;i++){
			SimpleThread thread = new SimpleThread(i,this.notify);
			vector.addElement(thread);
			thread.start();
		}
	}
	
	public void process(Taskable task){
		int i;
		for(i=0;i<vector.size();i++){
			SimpleThread currentThread = (SimpleThread)vector.elementAt(i);
			if(!currentThread.isRunningFlag()){
				System.out.println("Thread"+(i+1)+"开始执行任务了");
				currentThread.setTask(task);
				currentThread.setRunning(true);
				return;
			}
		}
		System.out.println("======================");
		System.out.println("线程池中没有空闲的线程");
		System.out.println("======================");
		
		if(i>=vector.size()){
			int temp = vector.size();
			for(int j=temp+1;j<=temp+10;j++){
				SimpleThread thread = new SimpleThread(j,this.notify);
				vector.addElement(thread);
				thread.start();
			}
			this.process(task);
		}
	}
}
