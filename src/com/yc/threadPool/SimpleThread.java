package com.yc.threadPool;

public class SimpleThread extends Thread {
	private boolean runningFlag;
	private Taskable task;   //任务
	private int threadNumber;
	private MyNotify mynotify;
	//标志runningFlag 用以激活线程
	public boolean isRunningFlag() {
		return this.runningFlag;
	}
	public synchronized void setRunning(boolean flag) {
		this.runningFlag = flag;
		if(flag){
			this.notifyAll();
		}
	}
	public Taskable getTask() {
		return task;
	}
	public void setTask(Taskable task) {
		this.task = task;
	}
	//提示是哪个线程工作
	public SimpleThread(int threadNumber,MyNotify notify){
		runningFlag=false;
		this.threadNumber=threadNumber;
		System.out.println("Thread"+threadNumber+" started...");
		this.mynotify=notify;
	}
	@Override
	public synchronized void run() {
		try {
			while(true){
				if(!runningFlag){
					this.wait();
				}else{
					System.out.println("********执行"+threadNumber+"...--->done");
					Object returnValue = this.task.doTask();
					if(mynotify !=null){
						mynotify.notifyResult(returnValue);
					}
					//sleep(5000);
					System.out.println("线程"+threadNumber+"已经重新准备........");
					setRunning(false);
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupt");
		}
	}
	
	
	
}
