package com.draw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.draw.R;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.tranfer.ChatManager;
import com.levelup.draw.tranfer.CientPlayer;
import com.levelup.draw.tranfer.ServerPlayer;
import com.levelup.draw.tranfer.TranferController;
import com.levelup.draw.tranfer.TranferService;
import com.levelup.draw.utils.DBHelper;
import com.levelup.draw.utils.DisplayUtil;
import com.levelup.draw.utils.PlayMusicUtil;
import com.levelup.draw.utils.WifiAdmin;

public class GameActivity extends Activity {

	private Binder binder;
	private boolean isServer = false;


	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case TranferController.TYPE_ANSWER: // �������Ĺ�����Ŀ����Ϣ
				handleAboutAnswer(msg.getData());
				break;
			case TranferController.TYPE_MYPATH: // ����·����Ϣ
				System.out.println("GameActiviity���յ���·��");
				handlePath(msg.getData());
				break;
			case TranferController.TYPE_RIGHT_IP: // �������˴���˵���Ϣ
				handleRight(msg.getData());
				break;
			case TranferController.TYPE_ROUND_AND_IP: // ����غϿ�ʼ����Ϣ
				handleRoundBegin(msg.getData());
				break;
			case TranferController.TYPE_WRONG_IP: // ��������˴���˵���Ϣ
				handleWrong(msg.getData());
				break;
			case TranferController.TYPE_ANIMATION: // ����������Ϣ
				handleAnimation(msg.getData());
				break;
			case TranferController.TYPE_ROUND_OVER: // ����غϽ�������Ϣ
				handleRoundEnd(msg.getData());
				break;
			case TranferController.TYPE_BACK: // �������˷�����һ�������Ϣ
				handleBack(msg.getData());
				break;
			case TranferController.TYPE_GIVEUP: // �������
				handleGiveup();
				break;
			case TranferController.TYPE_INFO: // һ���˵���Ϣ
				handleInfo();
				break;
			case TranferController.TYPE_INFOS: // �����˵���Ϣ
				handleInfo();
				break;
			case TranferController.TYPE_CLEAR: // ����
				handleClear();
				break;
			case TranferController.TYPE_GAME_OVER: // ��Ϸ����
				handleGameOver();
				break;
			case TranferController.TYPE_EXIT: // �����˳�
				handleExit(msg.getData());
				break;
			}
		}

	};

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (Binder) service;
			((TranferService.MyBinder) binder).setMyHandler(handler);
			// �����Ϊ�������˵Ļ�
			if (isServer) {
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(ChatManager.SERVER_FLAG);
				Parcel parcel2 = Parcel.obtain();
				try {
					if (binder != null) {
						binder.transact(0, parcel, parcel2, BIND_AUTO_CREATE);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} finally{
					try{
						parcel2.setDataPosition(0);// ���ǵ���仰������ô��
						parcel.recycle();
						parcel2.recycle();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			} else {// �������Ϊ�ͻ��˵Ļ�
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(ChatManager.CLIENT_FLAG);
				Parcel parcel2 = Parcel.obtain();
				try {
					binder.transact(0, parcel, parcel2, BIND_AUTO_CREATE);
				} catch (RemoteException e) {
					e.printStackTrace();
				}finally{
					try{
						parcel2.setDataPosition(0);
						parcel.recycle();
						parcel2.recycle();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// �����Ļ�Ŀ�Ⱥ͸߶�
		DrawApplication.getApplicationInstance().setScreenWidth(
				DisplayUtil.getScreenWidth(getApplicationContext()));
		DrawApplication.getApplicationInstance().setScreenHeight(
				DisplayUtil.getScreenHeight(getApplicationContext()));

		resolveData();

		InitFragment frag_init = new InitFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("showButton", isServer);
		frag_init.setArguments(bundle);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.game_content, frag_init, "frag_init");
		ft.commit();
	}

	// ���ݴ��ϸ�activity�����������ݴ�������
	private void resolveData() {
		Intent intent = getIntent();
		isServer = intent.getBooleanExtra("isServer", false);
		Intent intent2 = new Intent();
		intent2.setClass(GameActivity.this, TranferService.class);
		bindService(intent2, conn, BIND_AUTO_CREATE);// ��Service
	}

	// ��������addDraw
	private void addDraw(Bundle bundle) {
		Fragment frag_draw = new DrawFragment();
		frag_draw.setArguments(bundle);
		FragmentTransaction fTransaction = getFragmentManager()
				.beginTransaction();
		fTransaction.replace(R.id.game_content, frag_draw, "frag_draw");
		fTransaction.commit();
	}

	// ���µĽ���չʾ��activity��
	private void addGuess(Bundle bundle) {
		Fragment frag_guess = new GuessFragment();
		frag_guess.setArguments(bundle);
		FragmentTransaction fTransaction = getFragmentManager()
				.beginTransaction();
		fTransaction.replace(R.id.game_content, frag_guess, "frag_guess");
		fTransaction.commit();
	}

	// ���������Ŀ����Ϣ
	private void handleAboutAnswer(Bundle bundle) {
		// һ�����ڲµĽ�����
		System.out.println("�յ��˴�" + bundle.getString("answer"));
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		// �����ݽ����µĽ���ȥ����
		if (fragment != null) {
			((GuessFragment) fragment).handleAnswer(bundle);
		}
	}

	// ����·������Ϣ
	private void handlePath(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			System.out.println("GameActivit��GuessFragment���յ���·������");
			((GuessFragment) fragment).handlePath(bundle);
			return;
		}
	}

	// �������˴�Ե���Ϣ
	private void handleRight(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");

		PlayMusicUtil.playMusic(this, PlayMusicUtil.MUSIC_GET_RIGHT);
		if (fragment != null) {
			((GuessFragment) fragment).handleRight(bundle);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).handleRight(bundle);
		}
	}

	// �������˴���˵���Ϣ
	private void handleWrong(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).handleWrong(bundle);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).handleWrong(bundle);
		}
	}

	// ������Ϸ��ʼ����Ϣ
	private void handleRoundBegin(Bundle bundle) {
		String nextIp = bundle.getString("nextip");
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).dismissDialog();
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).dismissDialog();
		}

		// ��һ��ʼ����fragment��ʱ���Ҫ��ʼ��ʱ

		// ****************************����õ���Ŀ
		if (nextIp.equals(DrawApplication.getApplicationInstance().getMyIp())) { // �����һ�����������Լ��Ļ�

			DBHelper db = new DBHelper(getApplicationContext());
			String[] s = db.getRandomAnswerHint();

			int i = s[0].getBytes().length / 3;
			String hint1 = i + "����";

			// ����Ŀ��Ϣ���ͳ�ȥ
			
			System.out.println("��������Ŀ��Ϣ");
			bundle.putString("answer", s[0]);
			bundle.putString("hint1", hint1);
			bundle.putString("hint2", s[1]);
			addDraw(bundle);
		} else {
			addGuess(bundle);
		}
	}

	// ����һ���ظ�����
	private void handleRoundEnd(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		int reason = bundle.getInt("reason");
		if (fragment != null) {
			((GuessFragment) fragment).handleEnd(reason);
		} else {
			fragment = getFragmentManager().findFragmentByTag("frag_draw");
			if (fragment != null) {
				((DrawFragment) fragment).handleEnd(reason);
			}
		}
	}

	// �������˳��˷���
	private void handleBack(Bundle bundle) {
		Toast.makeText(this, "�����˳��˷��䣬���伴���ر�", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	private void handleGiveup() {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).handleEnd(TranferController.TYPE_GIVEUP);
		}
	}

	// ������***************************************��δ���
	public void handleAnimation(Bundle bundle) {
		int animation_type = bundle.getInt("animation");
		// ���Ŷ���
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).playGIF(animation_type);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).playGIF(animation_type);
		}
	}

	// ����һ���˵���Ϣ��Ҳ����֪ͨinitfragment�����е�listview����
	private void handleInfo() {
		Fragment fragment = getFragmentManager().findFragmentByTag("frag_init");
		if (fragment != null) {
			// ֪ͨ���½���
			((InitFragment) fragment).refresh();
		}
	}

	// ���������¼�
	private void handleClear() {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			// ֪ͨ���½���
			((GuessFragment) fragment).handleClear();
		}
	}

	private void handleGameOver() {

		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).dismissDialog();
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).dismissDialog();
		}

		Toast.makeText(getApplicationContext(), "��Ϸ����", 1000).show();
		if (TranferService.player instanceof ServerPlayer) {
			// ��serverplayer�еĶ������
			ServerPlayer serverPlayer = (ServerPlayer) TranferService.player;
			serverPlayer.clear();
		}
		for (MyInfo info : TranferService.infos) {
			info.setPoint(0);
		}

		InitFragment initFragment = new InitFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("showButton", isServer);
		initFragment.setArguments(bundle);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.game_content, initFragment, "frag_init");
		ft.commit();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Intent intent = new Intent(this, TranferService.class);
		unbindService(conn);
		stopService(intent);
		// �ر�wifi

		WifiAdmin wifiAdmin = WifiAdmin.getInstance(getApplicationContext());
		if (TranferService.player instanceof ServerPlayer) {
			// �ر��ȵ�
			WifiAdmin m_wiFiAdmin = WifiAdmin
					.getInstance(getApplicationContext());
			m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(
					m_wiFiAdmin.getApSSID(), FirstActivity.WIFI_AP_PASSWORD, 3,
					"ap"), false);
			// ��serverplayer�еĶ������
			ServerPlayer serverPlayer = (ServerPlayer) TranferService.player;
			serverPlayer.clear();
		} else if (TranferService.player instanceof CientPlayer
				&& wifiAdmin.getNetworkId() != 0) {
			wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
		}
		TranferService.infos.clear();
		TranferService.player = null;
		System.out.println("����finish");
		super.onDestroy();
	}

	// ���»��˼�
	// �Լ���Ҫ�˳�
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Fragment frag = getFragmentManager().findFragmentByTag("frag_init");
		if (frag != null && TranferService.player!=null) {
			TranferService.player.sendBack(DrawApplication
					.getApplicationInstance().getMyIp());
			finish();
			return ;
		}
		// ����Լ��ǲ��Ƿ���
		boolean isServer = false;
		if (TranferService.player!=null && TranferService.player instanceof ServerPlayer) {
			isServer = true;
		}
		boolean isDrawer = false;
		Fragment fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			isDrawer = true;
		}
		fragment = null;
		showExit(isServer, isDrawer);
	}

	private void showExit(final boolean isServer, final boolean isDrawer) {
		String hintString = "��ȷ��Ҫ�˳�������";
		// �����Ի���
		if (isServer) {
			hintString = "��ȷ��Ҫ�˳����⽫�رշ���";
		}
		AlertDialog exit = new AlertDialog.Builder(GameActivity.this,
				AlertDialog.THEME_HOLO_LIGHT)
				.setTitle("��ʾ")
				.setTitle(hintString)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						String myip = DrawApplication.getApplicationInstance()
								.getMyIp();
						TranferService.player
								.sendExit(myip, isServer, isDrawer);
						// �ȴ�2��

						GameActivity.this.finish();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
				}).create();
		exit.show();
	}

	// ���������˳�
	private void handleExit(Bundle bundle) {
		// TODO Auto-generated method stub
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		String ip = bundle.getString("ip");
		boolean isServer = bundle.getBoolean("isserver");

		if (isServer == true) {
			Toast.makeText(getApplicationContext(), "�����˳�������ر�`",
					Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		if (fragment != null) {
			((GuessFragment) fragment).handleExit(ip);
		} else {
			fragment = getFragmentManager().findFragmentByTag("frag_draw");
			if (fragment != null) {
				((DrawFragment) fragment).handleExit(ip);
			}
		}
		// ����Ϣ����ɾ���˳����˵���Ϣ
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(bundle.getString("ip"))) {
				TranferService.infos.remove(info);
				break;
			}
		}
	}
}
