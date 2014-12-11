package com.levelup.draw.tranfer;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

public class TranferController {
	
	//������������type��һЩ����
	public static final int TYPE_ROUND_AND_IP = 10;
	public static final int TYPE_ANIMATION = 11;
	public static final int TYPE_RIGHT_IP = 12;
	public static final int TYPE_MYPATH = 13;
	public static final int TYPE_INIT = 14;
	public static final int TYPE_ROUND_OVER = 15;
	public static final int TYPE_GAME_OVER = 16;
	public static final int TYPE_INFOS = 17;
	public static final int TYPE_ANSWER = 18;
	public static final int TYPE_WRONG_IP = 19;
	public static final int TYPE_INFO = 20;
	public static final int ANIMATION_FLOWER = 0;
	public static final int ANIMATION_KISS = 1;
	public static final int ANIMATION_EGG = 2;
	public static final int ANIMATION_SHOES = 3;
	public static final int TYPE_EXIT = 30;
	public static final int TYPE_GIVEUP = 31;
	public static final int TYPE_BACK = 32;
	public static final int TYPE_CLEAR = 33;

	//�غϽ�����ԭ��
	public static final int ALL_RIGHT = 0;
	public static final int TIME_OVER = 1;
	public static final int DRAWER_EXIT = 2;
	public static final int GUESSER_EXIT = 3;
	
	//һ��TranferService�Ķ���
	public static TranferService service;
	public static boolean isServer;
	

	
	/**
	 * ���ݴӿͻ��˻�ȡ����Ϣ����Ҫ��ʲô
	 * @param ip �ͻ��˵�ip
	 * @param json �ͻ��˴�������socket����
	 */
	public static void handleReadMessage(byte[] buffer){
		//����ǿͻ��˵Ļ���������
		if(!isServer){
			System.out.println("�ͻ�������");
			return ;
		}
		
		int type=-1;
		String message="";
		String ip="";
		try {
			System.out.println("try�쵽");
			String jsonString = new String(buffer, "utf-8");
			JSONObject json = new JSONObject(jsonString);
			type = json.getInt("type");
			message = json.getString("data");
			ip = json.getString("ip");
			switch (type) {
			case 0:
				System.out.println("type");
				handleSendMessage(ip,message);
				break;

			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
	}
	
	public static void handleSendMessage(String ip,String msg){
		System.out.println(service.getServerSocket().chats.size());
		for(ChatManager cm:service.getServerSocket().chats){
			String clientip = cm.getSocket().getInetAddress().toString().substring(1);
			if(clientip.equals(ip)){
				cm.write("server's message");
			}
		}
	}

}
