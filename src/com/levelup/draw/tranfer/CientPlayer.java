package com.levelup.draw.tranfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.utils.DisplayUtil;
import com.levelup.draw.utils.JsonManager;

public class CientPlayer {
	
	public CientPlayer(TranferService service) {
		this.service = service;
	}

	protected TranferService service;
	
	public TranferService getService() {
		return service;
	}


	/**
	 * �����յ�����Ϣ
	 * @param json
	 */
	public void onReceive(String json){
		JSONObject jsonObject = null;
		try{
			jsonObject = new JSONObject(json);
			int type = jsonObject.getInt("type");
			switch(type){
			case TranferController.TYPE_INFO: //�õ�����һ���˵���Ϣ
				handleInfo(jsonObject.getString("ip"),jsonObject.getString("name"),
						jsonObject.getInt("screenwidth"),jsonObject.getInt("screenheight"));
				break;
			
			case TranferController.TYPE_INFOS: //�õ����������˵ĸ�����Ϣ
				handleInfos(jsonObject.getJSONArray("infos"));
				break;
			
			case TranferController.TYPE_ROUND_AND_IP: //�õ�������Ϸ��ʼ����Ϣ
				handleRound(jsonObject.getInt("round"),jsonObject.getString("nextip"),jsonObject.getInt("roundTotal"));
				break;
				
			case TranferController.TYPE_ANSWER: //�õ����ǹ�����Ŀ����Ϣ
				System.out.println("�յ�����Ŀ��Ϣ");
				handleAnswer(jsonObject.getString("answer"),jsonObject.getString("hint1"),jsonObject.getString("hint2"));
				break;
				
			case TranferController.TYPE_MYPATH: //�õ����ǻ���·������Ϣ
//				System.out.println("·��length:"+);
				handlePath( jsonObject.getJSONObject("path"));
				break;
				
			case TranferController.TYPE_RIGHT_IP: //�õ��������˴�Ե���Ϣ
				handleRight(jsonObject.getString("rightip"));//,jsonObject.getInt("score"));
				break;
				
			case TranferController.TYPE_WRONG_IP: //�õ��������˴�����Ϣ
				handleWrong(jsonObject.getString("wrongip"),jsonObject.getString("wronganswer"));
				break;
			case TranferController.TYPE_GIVEUP: //�õ���ʱ��������Ϣ
				handleGiveup();
				break;
				
			case TranferController.TYPE_ROUND_OVER: //�õ����ǻغϽ�������Ϣ
				handleRoundOver(jsonObject.getInt("reason"));
				break;
				
			case TranferController.TYPE_ANIMATION: //�õ����Ƕ���������
				handleAnimation(jsonObject.getString("ip"),jsonObject.getInt("animation"));
				break;
				
			case TranferController.TYPE_CLEAR: //�õ�����������Ϣ
				handleClear();
				break;
			case TranferController.TYPE_GAME_OVER: //�õ�������Ϸ��������Ϣ
				handleGameOver();
				break;
				
			case TranferController.TYPE_EXIT: //�õ��������˳�����Ϣ
				handleExit(jsonObject.getString("ip"),jsonObject.getBoolean("isserver"),jsonObject.getBoolean("isdrawer")); 
				break;
				
			case TranferController.TYPE_BACK: //�õ���ʱ���˷�����һ�������Ϣ
				handleBack(jsonObject.getString("ip"),jsonObject.getBoolean("isserver"));
				break;
			}
		} catch(JSONException exception){
			exception.printStackTrace();
		}
	}

	/**
	 * ������˷����ĸ�����Ϣ
	 * @param ip һ���˵�ip
	 * @param name ��Ӧ������
	 */
	protected void  handleInfo(String ip,String name,int width,int height) {
		
	}
	
	/**
	 * ���������˳�����Ϣ
	 * @param ip
	 * @param isServer
	 */
	//�����������isserverΪtrue
	protected void  handleBack(String ip,boolean isServer) {
		Message msg = service.getHandler().obtainMessage(TranferController.TYPE_BACK);
		Bundle bundle = new Bundle();
		bundle.putInt("type", TranferController.TYPE_BACK);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);
	}
	/**
	 * �����յ��������˵ĸ�����Ϣ
	 * @param infos �����˵ĸ�����Ϣ��json������ַ���
	 */
	protected void handleInfos(JSONArray jsonArray){
		JSONObject jsonObject = null;
		
		//�Ȱ�֮ǰ����Ϣ�������Ȼ��������������˵���Ϣ
		TranferService.infos.clear();
		try{
			for(int i=0;i<jsonArray.length();++i){
				jsonObject = jsonArray.getJSONObject(i);
				//���õ����������ݴ浽List������
				TranferService.infos.add(new MyInfo(jsonObject.getString("ip"), jsonObject.getString("name"), 
						jsonObject.getInt("screenwidth"),jsonObject.getInt("screenheight")));
			}
			
		} catch(JSONException exception){
			exception.printStackTrace();
		}
		//����Handler����Ϣ���µ�activity��
		System.out.println("�յ���Ϣ"+TranferService.infos.size());
		service.getHandler().sendEmptyMessage(TranferController.TYPE_INFOS);
	}
	
	/**
	 * �����¸��غϿ�ʼ����Ϣ
	 * @param round �¸��غϻ����˵�ip
	 */
	protected void handleRound(int round,String nextip,int roundTotal){
		Message msg = service.getHandler().obtainMessage(TranferController.TYPE_ROUND_AND_IP);
		Bundle bundle = new Bundle();
		bundle.putInt("round",round);
		bundle.putString("nextip",nextip);
		bundle.putInt("roundTotal", roundTotal);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);
	}
	
	/**
	 * ������Ŀ��Ϣ
	 * @param answer ����
	 * @param hint1  ��ʾ1
	 * @param hint2 ��ʾ2
	 */
	protected void handleAnswer(String answer,String hint1,String hint2){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ANSWER);
		Bundle dataBundle = new Bundle();
		dataBundle.putString("answer", answer);
		dataBundle.putString("hint1", hint1);
		dataBundle.putString("hint2", hint2);
		msgMessage.setData(dataBundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * ����·��
	 * @param myPath ·��
	 */
	protected void handlePath(JSONObject jsonObject) {
		System.out.println("���յ���·��");
		Message message = service.getHandler().obtainMessage(TranferController.TYPE_MYPATH);
		Bundle bundle = new Bundle();
		bundle.putParcelable("path", JsonManager.parseJsonToMyPath(jsonObject));
		message.setData(bundle);
		service.getHandler().sendMessage(message);
	}
	
	/**
	 * �������˴��
	 * @param ip ��Ե��˵�ip
	 * @param score ��Ե��˵õ��ķ���
	 */
	protected void handleRight(String ip){ //,int score){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_RIGHT_IP);
		//�ڴ˴������ݣ�����ip���˵ķ�������
		
		//������˵��ǳƺ͵õ��ķ������͵�activity��
		Bundle bundle = new Bundle();
		bundle.putString("ip",ip);
//		bundle.putInt("score", score);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * �������˴��
	 * @param ip �����˵�ip
	 * @param wronganswer ����������Ĵ�
	 */
	protected void handleWrong(String ip , String wronganswer){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_WRONG_IP);
		Bundle bundle = new Bundle();
		bundle.putString("wrongip", ip);
		bundle.putString("wronganswer", wronganswer);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
		
	}
	/**
	 * �������˷���
	 */
	protected void handleGiveup(){
//		service.getHandler().sendEmptyMessage(TranferController.TYPE_GIVEUP);
	}
	
	/**
	 * ����غϽ���
	 */
	protected void handleRoundOver(int reason){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ROUND_OVER);
		Bundle bundle = new Bundle();
		bundle.putInt("reason", reason);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * �����Ӷ����Ķ���
	 * @param animation_type
	 */
	protected  void  handleAnimation(String ip,int animation_type) {
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ANIMATION);
		Bundle bundle = new Bundle();
		bundle.putInt("animation", animation_type);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * ����������Ϣ
	 */
	protected void handleClear() {
		service.getHandler().sendEmptyMessage(TranferController.TYPE_CLEAR);
	}
	/**
	 * ������Ϸ����
	 */
	protected void handleGameOver() {
		service.getHandler().sendEmptyMessage(TranferController.TYPE_GAME_OVER);
	}
	
	
	/**
	 * ���������˳�
	 * @param ip �˳��˵�ip
	 */
	protected void  handleExit(String ip,boolean isServer,boolean isDrawer) {
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_EXIT);
		Bundle bundle = new Bundle();
		bundle.putString("ip", ip);
		bundle.putBoolean("isserver", isServer);
		bundle.putBoolean("isdrawer", isDrawer);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	//�����Լ��˳�����Ϣ
	public void sendExit(String ip,boolean isServer,boolean isDrawer){
		service.getClientSocket().getChat().write(JsonManager.createExit(TranferController.TYPE_EXIT, ip, isServer,isDrawer));
	}
	
	/**
	 * �����Լ��ĸ�����Ϣ
	 * @param ip �Լ���ip
	 * @param name �Լ�������
	 */
	public void sendMyInfo(String ip,String name) {
		Context context = service.getApplicationContext();
		service.getClientSocket().getChat().write(JsonManager.createMyInfo
				(TranferController.TYPE_INFO, ip, name,DisplayUtil.getScreenWidth(context),DisplayUtil.getScreenHeight(context)));
	}
	
	/**
	 * ������Ŀ�������Ϣ
	 * @param answer ����
	 * @param hint1 ��ʾ1
	 * @param hint2 ��ʾ2
	 */
	public void sendAnswer(String answer,String hint1,String hint2){
		System.out.println("�ͻ��˷�������Ϣ");
		String jsonString = JsonManager.createQuestionInfo(TranferController.TYPE_ANSWER, answer, hint1, hint2);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * �����Լ���Ե���Ϣ
	 * @param ip �Լ���ip
	 * @param score �Լ���Եķ���
	 */
	public void sendRight(String ip,int score) {
		String jsonString = JsonManager.createRightJson(TranferController.TYPE_RIGHT_IP, ip, score);
		service.getClientSocket().getChat().write(jsonString);
	}

	/**
	 * �����Լ�������Ϣ
	 * @param ip �Լ���ip
	 * @param wrong �Լ���������
	 */
	public void sendWrong(String ip,String wrong){
		String jsonString = JsonManager.createWrongJson(TranferController.TYPE_WRONG_IP, ip, wrong);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * ���ͻ���·��
	 * @param myPath ·��
	 */
	public void sendPath(MyPath myPath){
		String jsonString = JsonManager.createPath(TranferController.TYPE_MYPATH, myPath);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * ���Ͷ���������
	 * @param animation_type  ����������
	 */
	public void sendAnimation(String ip ,int animation_type){
		String jsonString = JsonManager.createAnimation(TranferController.TYPE_ANIMATION,ip, animation_type);
		service.getClientSocket().getChat().write(jsonString);
	}

	/**
	 * �����Լ�������һ�������Ϣ
	 * @param ip
	 */
	public void sendBack(String ip){
		String jsonString = JsonManager.createBack(TranferController.TYPE_BACK, ip,false);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * ������������Ϣ
	 */
	public void sendClear(){
		String string = JsonManager.createClear(TranferController.TYPE_CLEAR);
		service.getClientSocket().getChat().write(string);
	}
	
	public void sendGiveUp(){
		String string = JsonManager.createGiveup(TranferController.TYPE_GIVEUP);
		service.getClientSocket().getChat().write(string);
	}
	
}
