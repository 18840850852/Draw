package com.levelup.draw.broadcast;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * ����wifi�ȵ�仯
 * 
 * @author smy
 * 
 */
public class WIFIBroadcast extends BroadcastReceiver{

	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//������wifi�ȵ����Ĺ㲥:  "android.net.wifi.SCAN_RESULTS"
		if(intent.getAction().endsWith(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			Log.i("WTScanResults---->ɨ�赽�˿�������---->", "android.net.wifi.SCAN_RESULTS");
			//����֪ͨ���������ӿ�
			((EventHandler)ehList.get(0)).scanResultsAvaiable();
			
		//wifi�򿪻�ر�״̬�仯   "android.net.wifi.WIFI_STATE_CHANGED"
		}else if(intent.getAction().endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->wifi״̬�仯--->", "android.net.wifi.WIFI_STATE_CHANGED");
			//���ﲻ��Ҫ����һ��SSID��wifi���ƣ�
			for(int j = 0; j < ehList.size(); j++) {
				((EventHandler)ehList.get(j)).wifiStatusNotification();
			}
			
		//������һ��SSID�󷢳��Ĺ㲥��(ע����android.net.wifi.WIFI_STATE_CHANGED������)  
		}else if(intent.getAction().endsWith(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->����״̬�仯---->", "android.net.wifi.STATE_CHANGE");
			System.out.println("����״̬�仯");
			  Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			  if(parcelableExtra!=null){
				  NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
	                State state = networkInfo.getState();  
	                if(state==State.CONNECTED){//��Ȼ����߿��Ը���ȷ��ȷ��״̬
	                	((EventHandler)ehList.get(0)).handleConnectChange();
	                }
	                	
	                }
			
		}
	}
	/**
	 * �¼������ӿ�
	 * @author ZHF
	 *
	 */
	public static abstract interface EventHandler {
		/**�������ӱ仯�¼�**/
		public abstract void handleConnectChange();
		/**ɨ��������Ч�¼�**/
		public abstract void scanResultsAvaiable();
		/**wifi״̬�仯�¼�**/
		public abstract void wifiStatusNotification();
	}
	
}