package com.levelup.draw.data;


import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.draw.activities.FirstActivity;
import com.example.draw.R;
import com.levelup.draw.utils.WifiAdmin;



public class RoomItemAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ScanResult> mList; // ɨ�赽���������б�
	private FirstActivity mContext;
	private Connectable connectable;

	public RoomItemAdapter(FirstActivity context, List<ScanResult> list,
			Connectable connectable) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = list;
		this.mInflater = LayoutInflater.from(context);
		this.connectable = connectable;
	}

	// �¼ӵ�һ��������������������
	public void setData(List<ScanResult> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// ��ȡ������ɨ����
		final ScanResult localScanResult = mList.get(position);
		// ��ȡwifi��
		final WifiAdmin wifiAdmin = mContext.m_wiFiAdmin;
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			//***************����Ҫ�����Ǹ�item�Ĳ��ֵ�id
			convertView = mInflater.inflate(R.layout.first_room_item, null);
			//*********************************************
			
			// ���ز���ģ��ؼ�
			viewHolder.textVName = ((TextView) convertView
					.findViewById(R.id.first_room_name));
			viewHolder.btConnect = ((Button)convertView.findViewById(R.id.first_room_enter));
			viewHolder.pb = ((ProgressBar)convertView.findViewById(R.id.first_room_wait));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ������Ӵ����¼�
		viewHolder.btConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				v.setVisibility(View.GONE);
				viewHolder.pb.setVisibility(View.VISIBLE);
				// ����wifi����
				WifiConfiguration localWifiConfiguration = wifiAdmin
						.createWifiInfo(localScanResult.SSID,
								FirstActivity.WIFI_AP_PASSWORD, 3, "wt");
				// ��ӵ�����
				wifiAdmin.addNetwork(localWifiConfiguration);
				// �����3.5s������Ϣ
				mContext.mHandler.sendEmptyMessage(FirstActivity.m_nWTConnected);
				connectable.changeConnectable();
			}
		});
		/*// ����Ͽ������¼�
		viewHolder.linearLConnectOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �Ͽ�ָ��wifi�ȵ�
				System.out.println("�Ͽ�wifi");
				wifiAdmin
						.disconnectWifi(wifiAdmin.getWifiInfo().getNetworkId());
				// "�Ͽ�����"��ʧ����������ʾ
				viewHolder.textConnect.setVisibility(View.GONE);
				viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
				viewHolder.linearLConnectOk.setVisibility(View.GONE);
				// �����3.5s������Ϣ
				mContext.mHandler.sendEmptyMessage(mContext.m_nWTConnected);

				connectable.changeConnectable();
			}
		});*/

		viewHolder.textVName.setText(localScanResult.SSID); // ��ʾ�ȵ�����

		// �����ӵ�wifi��Ϣ
		WifiInfo localWifiInfo = wifiAdmin.getWifiInfo();
		if (localWifiInfo != null) {
			try {// ��������
				if ((localWifiInfo.getSSID() != null)
						&& (localWifiInfo.getSSID()
								.equals(localScanResult.SSID))) {
					viewHolder.btConnect.setClickable(false);
					return convertView;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				return convertView;
			}
			viewHolder.btConnect.setClickable(true); //��������ť����ʹ��
		}
		return convertView;
	}

	public final class ViewHolder {
		public Button btConnect;
		public TextView textVName;
		public ProgressBar pb;
	}

	public interface Connectable {
		void changeConnectable();
	}
}
