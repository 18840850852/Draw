package com.levelup.draw.tranfer;

import org.json.JSONObject;

import android.R.integer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.utils.JsonManager;

public class ServerPlayer extends CientPlayer {

	public ServerPlayer(TranferService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}

	private String drawer_ip;
	private static int round = 1; // Ȧ��
	private static int round_index = 0; // ���ڻ�����˳���
	private static int roundTotal = 2;// �ܵ�Ȧ��
	private static int rightNumber = 0; // ��Ե�����
	private Thread thread = null; // һ������ʱ���߳�
	private Thread thread_time = null;

	// ÿ�յ�һ���˷�������Ϣ���Ͱ����е��˵���Ϣ��������
	@Override
	protected void handleInfo(String ip, String name, int width, int height) {
		// TODO Auto-generated method stub
		// ��������Խ��õ���ip��name�洢����
		TranferService.infos.add(new MyInfo(ip, name, width, height));
		service.getHandler().sendEmptyMessage(TranferController.TYPE_INFO);
		sendAllInfos();
	}

	@Override
	protected void handleBack(String ip, boolean isServer) {
		// ���˳�������socket�б���ɾ��
		for (ChatManager cm : service.getServerSocket().chats) {
			if (cm.getLocalIp().equals(ip)) {
				service.getServerSocket().chats.remove(cm);
				break;
			}
		}

		// ����Ϣ����ɾ���˳����˵�ip
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(ip)) {
				TranferService.infos.remove(info);
				break;
			}
		}
		// ���������˵���Ϣ
		sendAllInfos();
	}

	@Override
	protected void handleAnswer(String answer, String hint1, String hint2) {
		System.out.println("�������յ�����Ŀ��Ϣ"); // int
		/*
		 * Message msgMessage = service.getHandler().obtainMessage(
		 * TranferController.TYPE_ANSWER); Bundle dataBundle = new Bundle();
		 * dataBundle.putString("answer", answer); dataBundle.putString("hint1",
		 * hint1); dataBundle.putString("hint2", hint2);
		 * msgMessage.setData(dataBundle);
		 * service.getHandler().sendMessage(msgMessage);
		 */
		super.handleAnswer(answer, hint1, hint2);

		// �ڸ����Լ�����Ļ����Ͻ���Ŀ��Ϣ�������ǻ�����������
		String clientip = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientip = cm.getSocket().getInetAddress().toString().substring(1);

			// ����Ŀ��Ϣ������Щ�Ȳ����Լ��ֲ��ǻ�������
			if (!clientip.equals(drawer_ip)) {
				cm.write(JsonManager.createQuestionInfo(
						TranferController.TYPE_ANSWER, answer, hint1, hint2));
			}
		}
	}

	@Override
	protected void handlePath(JSONObject myPathObject) {
		// TODO Auto-generated method stub
		super.handlePath(myPathObject);
		/*
		 * int length = TranferService.infos.size(); String draweripString =
		 * TranferService.infos.get(round_index % length) .getIP(); // �����˵�ip
		 */String clientip = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientip = cm.getSocket().getInetAddress().toString().substring(1);
			if (!clientip.equals(drawer_ip)) {
				cm.write(JsonManager.createPath(TranferController.TYPE_MYPATH,
						myPathObject));
			}
		}
	}

	@Override
	protected void handleRight(String ip) {
		// TODO Auto-generated method stub
		super.handleRight(ip);
		// �ڸ����Լ�����Ļ����Ͻ�����˵���Ϣ��������
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!clientIpString.equals(ip)) {
				cm.write(JsonManager.createRightJson(
						TranferController.TYPE_RIGHT_IP, ip, 0));// *************��ʱ�ѵ÷������0
			}
		}

		// �ж���������˶�����˵Ļ�����һ���غϽ�������Ϣ
		rightNumber++;
		if (rightNumber == TranferService.infos.size() - 1) {
			// ����ʱ��ֹͣ
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
			// �����˶��Ѿ������
			sendRoundOver(TranferController.ALL_RIGHT);
			// ���Լ���һ���غϽ�������Ϣ
			super.handleRoundOver(TranferController.ALL_RIGHT);
		}
	}

	@Override
	protected void handleWrong(String ip, String wronganswer) {
		// TODO Auto-generated method stub
		super.handleWrong(ip, wronganswer);
		// �ڸ����Լ�����Ļ����Ͻ�����˵���Ϣ��������
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!clientIpString.equals(ip)) {
				cm.write(JsonManager.createWrongJson(
						TranferController.TYPE_WRONG_IP, ip, wronganswer));
			}
		}
	}

	@Override
	protected void handleGiveup() {
		// TODO Auto-generated method stub
		super.handleRoundOver(TranferController.TYPE_GIVEUP);
		sendRoundOver(TranferController.TYPE_GIVEUP);
	}

	@Override
	public void handleExit(String ip, boolean isServer, boolean isDrawer) {
		// ����˳������ǻ����˵Ļ���
		// ���˳�������socket�б���ɾ��
		for (ChatManager cm : service.getServerSocket().chats) {
			if (cm.getLocalIp().equals(ip)) {
				service.getServerSocket().chats.remove(cm);
				break;
			}
		}
		/*
		 * //���˳���������Ϣ��ɾ�� for(MyInfo info:TranferService.infos){
		 * if(info.getIP().equals(ip)){ TranferService.infos.remove(info);
		 * break; } }
		 */
		if (isDrawer) {
			sendRoundOver(TranferController.DRAWER_EXIT); // ԭ���ǻ������˳���
		} else {
			sendExit(ip, false, false);
		}
		// ���ø���ķ���ȥ����activity����
		super.handleExit(ip, isServer, isDrawer);
	}

	/**
	 * ���Լ���Ե���Ϣ�������в����Լ�����
	 */
	@Override
	public void sendRight(String ip, int score) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRightJson(
					TranferController.TYPE_RIGHT_IP, ip, score));
		}
		// ������Լ���ԵĻ����ж��ǲ��������˶������
		rightNumber++; // �ֶ���һ���˴����
		// �����ʱ�����˶�����ˣ�������Ϸ�����ı��
		if (rightNumber == TranferService.infos.size() - 1) {
			sendRoundOver(TranferController.ALL_RIGHT);
			// ����ʱ��ֹͣ
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
			// **************�����Լ��غϽ�������Ϣ
			super.handleRoundOver(TranferController.ALL_RIGHT);
		}
	}

	/**
	 * ���Լ�������Ϣ�������в����Լ�����
	 */
	@Override
	public void sendWrong(String ip, String wrong) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createWrongJson(
					TranferController.TYPE_WRONG_IP, ip, wrong));
		}
	}

	public void sendRound(int number) {
		int length = TranferService.infos.size();
		int index = 0;
		/*
		 * if (length > 0) { r_index = (round++) / length + 1; index =
		 * (round_index++) % length; }
		 */
		if (length > 0) {
			index = round_index++;
			if (index >= length) {
				index = 0;
				round_index = 1;
				round++;
				if (round == roundTotal + 1) {
					// ��Ϸ����
					for (ChatManager cm : service.getServerSocket().chats) {
						cm.write(JsonManager
								.createGameOver(TranferController.TYPE_GAME_OVER));
					}
					super.handleGameOver();
					// ���ٿ����ȴ��߳�
					return;
				}
			}
		}

		roundTotal = number;
		rightNumber = 0;

		/*
		 * if (r_index > roundTotal) {// ��Ϸ����
		 * 
		 * }
		 */
		/*
		 * int index = round_index++; if(round_index>=length-1){ round_index=1;
		 * index=0; } if(index>=length-1){ round_index = 0; }
		 */
		drawer_ip = TranferService.infos.get(index).getIP();
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRoundJson(
					TranferController.TYPE_ROUND_AND_IP, round, roundTotal,
					drawer_ip));
		}

		// ���͸��Լ�����Ϊ�������Ļ��Լ����ղ��������Ϣ��
		Message msg = service.getHandler().obtainMessage(
				TranferController.TYPE_ROUND_AND_IP);
		Bundle bundle = new Bundle();
		bundle.putInt("round", round);
		bundle.putInt("roundTotal", roundTotal);
		bundle.putString("nextip", drawer_ip);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);

		// ��ʼ��ʱ
		if (thread == null) {// ˵����һ��ִ�������ǿ��ֹͣ��
			System.out.println("Ҫ����һ���µĻغϵĵ���ʱ�߳�");
			thread = new TimeCountThread();
			thread.start();
		}
	}

	public void sendRound() {
		sendRound(roundTotal);
	}

	/**
	 * �������˷���һ���˵ĻغϽ���
	 * 
	 * @param reason
	 *            �غϽ�����ԭ��
	 */
	public void sendRoundOver(int reason) {
		if (service == null) {
			return;
		}
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRoundOver(
					TranferController.TYPE_ROUND_OVER, reason));
		}

		// ����һ���ȴ��߳�
		if (thread_time != null && thread_time.isAlive()) {
			System.out.println("�غϼ�ȴ��̻߳�����");
			thread_time.interrupt();
		}
		thread_time = null;
		thread_time = new TimeThread();
		thread_time.start();
	}

	// ������˷������Ķ�������
	@Override
	protected void handleAnimation(String ip, int animation_type) {
		// TODO Auto-generated method stub
		// �����Լ�Ҫ�ڽ�����ȥ�����������
		super.handleAnimation(ip, animation_type);
		// Ȼ��Ҫ�����������������
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!ip.equals(clientIpString)) {
				cm.write(JsonManager.createAnimation(
						TranferController.TYPE_ANIMATION, ip, animation_type));
			}
		}
	}

	@Override
	protected void handleClear() {
		// TODO Auto-generated method stub
		super.handleClear();
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createClear(TranferController.TYPE_CLEAR));
		}
	}

	// �������Լ�����˷��Ͷ���
	@Override
	public void sendAnimation(String ip, int animation_type) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createAnimation(
					TranferController.TYPE_ANIMATION, ip, animation_type));
		}
	}

	/**
	 * �������˷��������˵���Ϣ
	 */
	public void sendAllInfos() {
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createInfosJson(TranferController.TYPE_INFOS,
					TranferService.infos));
		}
	}

	/**
	 * �������Լ��������˷�·��
	 */
	@Override
	public void sendPath(MyPath myPath) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createPath(TranferController.TYPE_MYPATH,
					myPath));
		}
	}

	/**
	 * �����������˷�������Ŀ����Ϣ
	 */
	@Override
	public void sendAnswer(String answer, String hint1, String hint2) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createQuestionInfo(
					TranferController.TYPE_ANSWER, answer, hint1, hint2));
		}
	}

	/*
	 * �������������˷���Ҫ�˳� ����Ϣ
	 */
	public void sendExit(String ip, boolean isServer, boolean isDrawer) {
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createExit(TranferController.TYPE_EXIT, ip,
					isServer, isDrawer));
		}
		if (thread_time != null) {
			thread_time.interrupt();
			thread_time = null;
		}
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * �������˷��ͷ����˳�����Ϣ
	 */
	@Override
	public void sendBack(String ip) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createBack(TranferController.TYPE_BACK, ip,
					true));
		}
	}

	@Override
	public void sendClear() {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createClear(TranferController.TYPE_CLEAR));
		}
	}

	@Override
	public void sendGiveUp() {
		// TODO Auto-generated method stub
		super.handleRoundOver(TranferController.TYPE_GIVEUP);
		sendRoundOver(TranferController.TYPE_GIVEUP);
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * �������Щ��̬����
	 */
	public void clear() {
		round = 0;
		round_index = 0;
		roundTotal = 0;
		rightNumber = 0;
	}

	// һ���غϵ���ʱ���߳�
	class TimeCountThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				Thread.sleep(1000 * 60);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("�����");
				thread = null;
				return;
			}
			// 1����ʱ�䵽��������Ϸ������allright��־Ϊfalse
			thread = null;
			System.out.println("������Ϸ�غϽ�����ʱ�䵽");
			sendRoundOver(TranferController.TIME_OVER);
			// *************************�����Լ��غϽ�������Ϣ
			Message msgMessage = service.getHandler().obtainMessage(
					TranferController.TYPE_ROUND_OVER);
			Bundle bundle = new Bundle();
			bundle.putBoolean("allright", false);
			msgMessage.setData(bundle);
			service.getHandler().sendMessage(msgMessage);

		}
	}

	// һ���ȴ����߳�
	class TimeThread extends Thread {
		public void run() {
			super.run();
			try {
				Thread.sleep(1000 * 5);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("�غϼ�ȴ��߳̽���������һ�غ���Ϣ");
			sendRound();
			thread_time = null;
		}
	}
}
