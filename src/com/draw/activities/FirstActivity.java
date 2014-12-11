package com.draw.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.draw.R;
import com.levelup.draw.broadcast.WIFIBroadcast;
import com.levelup.draw.broadcast.WIFIBroadcast.EventHandler;
import com.levelup.draw.data.CreateAPProcess;
import com.levelup.draw.data.RoomItemAdapter;
import com.levelup.draw.data.RoomItemAdapter.Connectable;
import com.levelup.draw.data.WFSearchProcess;
import com.levelup.draw.utils.DBHelper;
import com.levelup.draw.utils.SettingUtil;
import com.levelup.draw.utils.WifiAdmin;

public class FirstActivity extends Activity implements OnClickListener,
		EventHandler, Connectable {

	Button name;
	Button search;
	Button create;
	Button add;
	Button volume;
	Button quit;
	View mainBG;

	View contentApp;
	View contentAdd;
	View contentSearch;
	View contentInput;
	DBHelper dbHelper;

	public WifiAdmin m_wiFiAdmin; // Wifi������

	public static final String WIFI_AP_HEADER = "���ѻ�_";
	public static int SERVER_PORT = 4545;// �������Ķ˿ں�
	// ��Ϣ�¼�
	public static final int m_nWifiSearchTimeOut = 0;// ������ʱ
	public static final int m_nWTScanResult = 1;// ������wifi���ؽ��
	public static final int m_nWTConnectResult = 2;// ������wifi�ȵ�
	public static final int m_nCreateAPResult = 3;// �����ȵ���
	public static final int m_nUserResult = 4;// �û�����������������(����)
	public static final int m_nWTConnected = 5;// ������Ӻ�Ͽ�wifi��3.5���ˢ��adapter
	public static final String WIFI_AP_PASSWORD = "smy12345";

	public CreateAPProcess m_createAPProcess; // ����Wifi�ȵ��߳�
	public WFSearchProcess m_wtSearchProcess; // WiFi�����������߳�

	private RoomItemAdapter m_wTAdapter; // �����б�������
	ArrayList<ScanResult> m_listWifi = new ArrayList<ScanResult>();// ��⵽�ȵ���Ϣ�б�

	private BroadcastReceiver receiver = null;
	private final IntentFilter intentFilter = new IntentFilter();

	private boolean canConnect = true;
	private boolean cancreate = true;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case m_nWifiSearchTimeOut: // ������ʱ

				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				m_wtSearchProcess.stop();
				m_listWifi.clear(); // �����б�
				// ���ÿؼ�
//				Toast.makeText(getApplicationContext(), "������ʱ", 1000).show();
				break;

			case m_nWTScanResult: // ɨ�赽���
				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				m_listWifi.clear();
				if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
					for (int i = 0; i < m_wiFiAdmin.mWifiManager
							.getScanResults().size(); i++) {
						ScanResult scanResult = m_wiFiAdmin.mWifiManager
								.getScanResults().get(i);
						// ��ָ�������ȵ�Ƚϣ��������Ĺ��˵���
						if (scanResult.SSID.startsWith(WIFI_AP_HEADER)) {
							m_listWifi.add(scanResult);
						}
					}
					if (m_listWifi.size() > 0) {
						m_wtSearchProcess.stop();
						// �����б���ʾ�����������ȵ�
						m_wTAdapter.setData(m_listWifi);
						m_wTAdapter.notifyDataSetChanged();
					}
				}
				break;
			case m_nWTConnectResult: // ���ӽ��

				m_wTAdapter.notifyDataSetChanged(); // ˢ������������
				// *************************************************
				// �ж��ǲ����Լ���������ȵ�
				boolean isMySsid = false;
				WifiInfo localWifiInfo = m_wiFiAdmin.getWifiInfo();
				String conSsid = localWifiInfo.getSSID();
				if (conSsid.startsWith("\""))
					conSsid = conSsid.substring(1);
				if (conSsid.endsWith("\""))
					conSsid = conSsid.substring(0, conSsid.length() - 1);
				for (ScanResult sr : m_listWifi) {
					if (conSsid.equals(sr.SSID)) {
						isMySsid = true;
						break;
					}
				}
				if (isMySsid == false) {
					break;
				}

				Intent intent = new Intent(FirstActivity.this,
						GameActivity.class);
				intent.putExtra("isServer", false);
				startActivityForResult(intent, 0);
				System.out.println("start,false");
				break;
			case m_nCreateAPResult: // ����wifi�ȵ���
				m_createAPProcess.stop();
				// ��ת��������ʧ
				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				if ((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin
						.getWifiApState() == 13)
						&& (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))
						&& cancreate == true) {
					// ��ת������Ľ���
					if (cancreate == true) {
						cancreate = false;
						Intent intent2 = new Intent(FirstActivity.this,
								GameActivity.class);
						intent2.putExtra("isServer", true);
						startActivity(intent2);
						System.out.println("start,true");
						break;
					}
				} else {
					Toast.makeText(getApplicationContext(), "�ȵ㴴��ʧ��", 1000)
							.show();
					cancreate = true;
				}
				break;
			case m_nWTConnected: // ������Ӻ�Ͽ�wifi��3.5s��ˢ��
				m_wTAdapter.notifyDataSetChanged();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȡ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_first);

		boolean i = SettingUtil.get(getApplicationContext(), "volume", true);
		DrawApplication.getApplicationInstance().setVolume(i);
		
		boolean open = WifiAdmin.getInstance(getApplicationContext()).isWifiOpen();
		DrawApplication.getApplicationInstance().setWifiOpen(open);
		

		ListView m_listVWT = (ListView) findViewById(R.id.first_content_list);

		m_wiFiAdmin = WifiAdmin.getInstance(getApplicationContext());

		m_wTAdapter = new RoomItemAdapter(this, m_listWifi, this);
		m_listVWT.setAdapter(m_wTAdapter);

		// ����Wifi�ȵ�
		m_createAPProcess = new CreateAPProcess(this);
		m_wtSearchProcess = new WFSearchProcess(this);

		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

		dbHelper = new DBHelper(getApplicationContext());

		name = (Button) findViewById(R.id.first_name);
		create = (Button) findViewById(R.id.first_create);
		search = (Button) findViewById(R.id.first_search);
		add = (Button) findViewById(R.id.first_add);
		volume = (Button) findViewById(R.id.first_volume);
		quit = (Button) findViewById(R.id.first_quit);
		mainBG = findViewById(R.id.first_bg);

		mainBG.setOnClickListener(this);
		name.setOnClickListener(this);
		create.setOnClickListener(this);
		search.setOnClickListener(this);
		add.setOnClickListener(this);
		volume.setOnClickListener(this);
		quit.setOnClickListener(this);

		contentApp = findViewById(R.id.first_content_app);
		contentAdd = findViewById(R.id.first_content_add);
		contentInput = findViewById(R.id.first_content_input_name);
		contentSearch = findViewById(R.id.first_content_list);

		if (!DrawApplication.getApplicationInstance().getVolume()) {
			volume.setBackgroundResource(R.drawable.first_volume_off);
		}

		String userName = SettingUtil.get(getApplicationContext(), "username",
				"");

		System.out.println("2222" + userName);
		DrawApplication applcApplication = DrawApplication
				.getApplicationInstance();
		applcApplication.setUsername(userName);
		if (userName.equals("")) {
			name.setText("��������û���");
		} else {
			name.setText(userName);
		}
	}

	// ��������ĺ���
	private void createRoom() {

		DrawApplication applcApplication = DrawApplication
				.getApplicationInstance();
		String userName = applcApplication.getUsername();

		if (m_wiFiAdmin.getWifiApState() == 4) { // WIFI_STATE_UNKNOWN
			Toast.makeText(getApplicationContext(), "�����豸��֧���ȵ㴴��!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) { // Ŀǰ����wifi
			m_wiFiAdmin.closeWifi();
		}
		if (m_wtSearchProcess.running) {
			m_wtSearchProcess.stop(); // ֹͣ�߳�
		}

		m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER
				+ userName + "�ķ���", WIFI_AP_PASSWORD, 3, "ap"), true);
		m_createAPProcess.start(); // ���������ȵ��߳�

		// ��wifi��Ϣ�б����õ�listview��
		m_listWifi.clear();
		m_wTAdapter.setData(m_listWifi);
		m_wTAdapter.notifyDataSetChanged();
	}

	// ��������ĺ���
	private void searchRoom() {
		// �ر�wifi
		m_wiFiAdmin.closeWifi();
		if (!m_wtSearchProcess.running) { // �����߳�û�п���
			// 1.��ǰ�ȵ��wifi������ WIFI_STATE_ENABLED 3 //WIFI_AP_STATE_ENABLED 13
			if (m_wiFiAdmin.getWifiApState() == 3
					|| m_wiFiAdmin.getWifiApState() == 13) {
				Toast.makeText(getApplicationContext(), "�����ѽ���wifi", 1000)
						.show();
				return;
			}
			// 2.��ǰû���ȵ��wifi������
			if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) { // ���wifiû��
				m_wiFiAdmin.OpenWifi();
			}
			// ��ʼ����wifi
			m_wiFiAdmin.startScan();
			m_wtSearchProcess.start(); // ���������߳�
		} else {// �����߳̿����ţ��ٴε����ť ��������
			m_wtSearchProcess.stop();
			m_wiFiAdmin.startScan(); // ��ʼ����wifi
			m_wtSearchProcess.start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new WIFIBroadcast();
		registerReceiver(receiver, intentFilter);
		WIFIBroadcast.ehList.add(this);
		cancreate = true;
		initContent();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		WIFIBroadcast.ehList.remove(this);
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//����������Ϸǰ��wifi״̬�Ǵ򿪵Ļ�������Ϸ�ر�ʱҲҪ����ҵ�wifi
		if(DrawApplication.getApplicationInstance().isWifiOpen()){
			WifiAdmin.getInstance(getApplicationContext()).OpenWifi();
		}else{
			WifiAdmin.getInstance(getApplicationContext()).closeWifi();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.first_name:
			initContent();
			name.setTextColor(Color.parseColor("#1fb6c8"));
			showInputName();
			break;
		case R.id.first_create:
			String sname = SettingUtil.get(getApplicationContext(), "username",
					"");
			if (sname.equals("")) {// ˵�����ڻ�û������
				Toast.makeText(getApplicationContext(), "�������Լ��������", 500)
						.show();
				break;
			}
			findViewById(R.id.first_content_app).setVisibility(View.GONE);
			findViewById(R.id.first_content_progress).setVisibility(
					View.VISIBLE);
			createRoom();
			break;

		case R.id.first_search:
			String sname2 = SettingUtil.get(getApplicationContext(),
					"username", "");
			if (sname2.equals("")) {// ˵�����ڻ�û������
				Toast.makeText(getApplicationContext(), "�������Լ��������", 500)
						.show();
				break;
			}
			initContent();
			search.setBackgroundResource(R.drawable.first_search_u);
			findViewById(R.id.first_content_progress).setVisibility(
					View.VISIBLE);
			showSearch();
			searchRoom();
			break;
		case R.id.first_add:
			initContent();
			add.setBackgroundResource(R.drawable.first_add_u);
			showAdd();
			break;
		case R.id.first_volume:
			if (DrawApplication.getApplicationInstance().getVolume()) {
				DrawApplication.getApplicationInstance().setVolume(false);
				volume.setBackgroundResource(R.drawable.first_volume_off);
				SettingUtil.set(getApplicationContext(), "volume", false);
			} else {
				DrawApplication.getApplicationInstance().setVolume(true);
				volume.setBackgroundResource(R.drawable.first_volume_on);
				SettingUtil.set(getApplicationContext(), "volume", true);
			}
			break;
		case R.id.first_quit:
			this.finish();
			break;
		case R.id.first_bg:
			initContent();
			break;
		default:
			break;
		}

	}

	private void showSearch() {
		// TODO Auto-generated method stub

		contentApp.setVisibility(View.GONE);
		contentSearch.setVisibility(View.VISIBLE);

	}

	private void showAdd() {
		// TODO Auto-generated method stub
		contentApp.setVisibility(View.GONE);
		contentAdd.setVisibility(View.VISIBLE);
		final EditText et1 = (EditText) findViewById(R.id.first_add_citiao);
		final EditText et2 = (EditText) findViewById(R.id.first_add_leibie);

		findViewById(R.id.first_add_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String s1 = et1.getText().toString();
						String s2 = et2.getText().toString();

						if (s1.equals("") || s2.equals("")) {
							Toast.makeText(FirstActivity.this, "���ݲ���Ϊ��",
									Toast.LENGTH_SHORT).show();
						} else {
							dbHelper.insertOnce(s1, s2);
							Toast.makeText(FirstActivity.this, "��ӳɹ�",
									Toast.LENGTH_SHORT).show();
						}

						et1.setText("");
						et2.setText("");
						initContent();
					}
				});

		findViewById(R.id.first_add_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						et1.setText("");
						et2.setText("");
						initContent();

					}
				});
	}

	private void showInputName() {
		// TODO Auto-generated method stub
		contentApp.setVisibility(View.GONE);
		contentInput.setVisibility(View.VISIBLE);
		final EditText et = (EditText) findViewById(R.id.first_input_name);
		findViewById(R.id.first_input_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String sname = et.getText().toString();

						if (sname.equals("")) {
							Toast.makeText(getApplicationContext(),
									"�û�������Ϊ��,����������", Toast.LENGTH_SHORT).show();
						} else {
							name.setText(sname);
							SettingUtil.set(getApplicationContext(),
									"username", sname);
							DrawApplication.getApplicationInstance()
									.setUsername(sname);
						}
						et.setText("");
						initContent();
					}
				});

		findViewById(R.id.first_input_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						et.setText("");
						initContent();

					}
				});
	}

	private void initContent() {
		// TODO Auto-generated method stub
		name.setTextColor(Color.parseColor("#2e0000"));
		search.setBackgroundResource(R.drawable.first_search_n);
		add.setBackgroundResource(R.drawable.first_add_n);
		findViewById(R.id.first_content_progress).setVisibility(View.GONE);
		contentAdd.setVisibility(View.GONE);
		contentInput.setVisibility(View.GONE);
		contentApp.setVisibility(View.VISIBLE);
		contentSearch.setVisibility(View.GONE);
	}

	@Override
	public void handleConnectChange() {
		if (canConnect == false)
			return;
		Message msg = mHandler.obtainMessage(m_nWTConnectResult);
		mHandler.sendMessage(msg);
		canConnect = false;

	}

	@Override
	public void scanResultsAvaiable() {
		Message msg = mHandler.obtainMessage(m_nWTScanResult);
		mHandler.sendMessage(msg);
	}

	@Override
	public void wifiStatusNotification() {
		m_wiFiAdmin.mWifiManager.getWifiState(); // ��ȡ��ǰwifi״̬
	}

	@Override
	public void changeConnectable() {
		// TODO Auto-generated method stub
		this.canConnect = true;
	}
}
