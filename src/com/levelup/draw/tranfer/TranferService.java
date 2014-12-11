package com.levelup.draw.tranfer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.draw.activities.DrawApplication;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.utils.WifiAdmin;

public class TranferService extends Service {
	private Handler handler = null;
	private TranferServerSocket serverSocket = null;

	public static List<MyInfo> infos = new LinkedList<MyInfo>();// ��������Ϣ�ļ���

	public static CientPlayer player = null; // ������

	public TranferServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(TranferServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	private TranferClientSocket clientSocket = null;

	public TranferClientSocket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(TranferClientSocket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		System.out.println("��service");
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("����service");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopThread();
		System.out.println("service����");
		super.onDestroy();
	}

	private void startSocket(Parcel parcel) {
		final int isServer = parcel.readInt();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// int isServer = parcel.readInt();
				if (isServer == ChatManager.SERVER_FLAG) {// ����һ��servet�߳�
					try {
						serverSocket = new TranferServerSocket(getHandler());
						serverSocket.start();

						// ����������������(������)
						player = new ServerPlayer(TranferService.this);

						// ���������Լ���ip�����ּ��뵽�����У�����Ӧ�����Լ������
						DrawApplication application = DrawApplication.getApplicationInstance();
						infos.add(new MyInfo(
								WifiAdmin.getInstance(getApplicationContext()).getHOstAddress(),
								application.getUsername(),//�Լ�������
								application.getScreenWidth(),
								application.getScreenHeight()));

						DrawApplication.getApplicationInstance().setMyIp(WifiAdmin.getInstance(getApplicationContext()).getHOstAddress());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// ���÷��������
					TranferController.isServer = true;
				} else {
					// ����һ��client�߳�
					String hostAdd = (WifiAdmin
							.getInstance(getApplicationContext())
							.getHOstAddress());
					clientSocket = new TranferClientSocket(getHandler(),
							hostAdd);
					clientSocket.start();

					// �����ͻ��˿��ƶ���
					player = new CientPlayer(TranferService.this);
					DrawApplication.getApplicationInstance().setMyIp( WifiAdmin.getInstance(getApplicationContext()).getIPAddress());


					// ���ÿͻ��˱��
					TranferController.isServer = false;
				}

				// Ϊtransfercontroller��service����ֵ
				TranferController.service = TranferService.this;

			}
		}).start();

	}

	// �Զ����binder
	public class MyBinder extends Binder {

		@Override
		protected boolean onTransact(int code, Parcel parcel, Parcel parcel2,
				int flags) throws RemoteException {
			startSocket(parcel); // ���������߳�
			return super.onTransact(code, parcel, parcel2, flags);
		}

		public void setMyHandler(Handler handler) {
			setHandler(handler);
		}

		public Handler getMyHandler() {
			return getHandler();
		}

	}

	// ֹͣ�߳�
	public void stopThread() {
		//�ͷŷ�����ռ�õ���Դ
		if (serverSocket != null) {
			serverSocket.setRun(false);
			if(serverSocket!=null){
				try {
					serverSocket.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					serverSocket.setSocket(null);
					serverSocket = null;
				}
			}
			System.out.println("server�߳�����");
			return ;
		}
		if (clientSocket != null) {
			clientSocket.setRun(false);
			try {
				clientSocket.getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				clientSocket.setSocket(null);
				clientSocket.setRun(false);
				clientSocket = null;
			}
			System.out.println("client�߳�����");
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

}
