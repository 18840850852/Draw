package com.levelup.draw.data;

import android.os.Message;

import com.draw.activities.FirstActivity;

/**
 * Wifi��������
 * @author smy
 *
 */
public class WFSearchProcess implements Runnable {
	
	public FirstActivity context;
	public WFSearchProcess(FirstActivity context) {
		this.context = context;
	}

	public boolean running = false;
	private long startTime = 0L;
	private Thread thread  = null;
			
	@Override
	public void run() {
		while(true) {
			//�Ƿ�
			if(!running) return;
			if(System.currentTimeMillis() - startTime >= 5000L) {
				//���ͣ�������ʱ����Ϣ
				Message msg = context.mHandler.obtainMessage(context.m_nWifiSearchTimeOut);
				context.mHandler.sendMessage(msg);
				running=false;
			}
			try {
				Thread.sleep(10L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		try {
			thread = new Thread(this);
			running = true;
			startTime = System.currentTimeMillis();
			thread.start(); //�����߳�
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void stop() {
		try {
			running = false;
			thread = null;
			startTime = 0L;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
