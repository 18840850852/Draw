package com.levelup.draw.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.graphics.Rect;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.tranfer.TranferController;

public class JsonManager {


	/**
	 * �����Լ���Ϣ���ַ���
	 * @param ip �Լ���ip
	 * @param type ��ʾ�Լ���Ϣ��������
	 * @param name �Լ�������
	 * @return һ��json��ʽ���ַ���
	 */
	public static String createMyInfo(int type, String ip, String name,int screenWidth,int screenHeight) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("ip", ip);
			jsonObject.put("type", type);
			jsonObject.put("name", name);
			jsonObject.put("screenwidth", screenWidth);
			jsonObject.put("screenheight", screenHeight);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * ����һ��������������Ϣ��json��ʽ���ַ���
	 * 
	 * @param ip
	 * @param type
	 *            �������Ͷ�Ӧ������
	 * @param ips
	 *            ip�ļ���
	 * @param names
	 *            name�ļ���
	 * @return json��ʽ���ַ���
	 */
	public static String createInfosJson(int type, List<MyInfo> infos) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			jsonObject.put("type", type);
			if (infos != null) {
				for (MyInfo info : infos) {
					JSONObject json = new JSONObject();
					json.put("ip", info.getIP());
					json.put("name", info.getName());
					json.put("screenwidth", info.getScreenWidth());
					json.put("screenheight", info.getScreenHeight());
					jsonArray.put(json);
				}
			}
			jsonObject.put("infos", jsonArray);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * �����غϵ���Ϣ�ַ���
	 * 
	 * @param type
	 *            ��������
	 * @param round
	 *            �غ���Ϣ
	 * @param nextip
	 *            ��һ�������˵�ip
	 * @return
	 */
	public static String createRoundJson(int type, int round, int roundTotal,
			String nextip) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("nextip", nextip);
			jsonObject.put("round", round);
			jsonObject.put("roundTotal", roundTotal);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * ����һ������json��
	 * 
	 * @param ip
	 *            ����˵�ip
	 * @param type
	 *            ��ʾ�����������Ͷ�Ӧ������
	 * @param wronganswer
	 *            ��Ĵ����
	 * @return һ������json��ʽ���ַ���
	 */
	public static String createWrongJson(int type, String ip, String wronganswer) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("wrongip", ip);
			jsonObject.put("wronganswer", wronganswer);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * ����һ�������Ϣ���ַ���
	 * 
	 * @param type
	 *            ��������
	 * @param ip
	 *            ��Ե��˵�ip
	 * @param score
	 *            ��Ե��˵õ��ķ���
	 * @return
	 */
	public static String createRightJson(int type, String ip, int score) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("rightip", ip);
			jsonObject.put("score", score);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * ����������Ϣ���ַ���
	 * 
	 * @param type
	 *            ��ʾ������Ϣ�������͵�����
	 * @param answer
	 *            ����Ĵ�
	 * @param hint1
	 *            ����ĵ�һ����ʾ
	 * @param hint2
	 *            ����ĵڶ�����ʾ
	 * @return ������Ϣ��json��ʽ���ַ���
	 */
	public static String createQuestionInfo(int type, String answer,
			String hint1, String hint2) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("answer", answer);
			jsonObject.put("hint1", hint1);
			jsonObject.put("hint2", hint2);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * ����·�����ַ���
	 * 
	 * @param type
	 *            ��������
	 * @param myPath
	 *            ·��
	 * @return
	 */
	public static String createPath(int type, MyPath myPath) {
		JSONObject jsonObject = new JSONObject();
		JSONObject pathObject = new JSONObject();
		JSONObject rect = new JSONObject();
		JSONArray pintsArray = new JSONArray();
		try {
			Rect rect_path = myPath.getRect();
			// ����
			rect.put("top", rect_path.top);
			rect.put("right", rect_path.right);
			rect.put("bottom", rect_path.bottom);
			rect.put("left", rect_path.left);

			// ��ļ���
			List<Point> points = myPath.getPointArray();
			for (Point p : points) {
				pintsArray.put(p.x);
				pintsArray.put(p.y);
			}

			// ��ɫ
			pathObject.put("color", myPath.getPaintColor());
			// ���
			pathObject.put("width", myPath.getPaintWidth());
			// d����
			pathObject.put("rect", rect);
			// ��
			pathObject.put("points", pintsArray);

			jsonObject.put("type", type);
			jsonObject.put("path", pathObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * ��װһ������Ϊmypath��json��
	 * @param type ����
	 * @param jsonObject һ���Ѿ��ܹ���װ�õ�mypath��jsonobeject
	 * @return
	 */
	public static String createPath(int type,JSONObject jsonObject){
		JSONObject json = new JSONObject();
		try{
			json.put("type", type);
			json.put("path", jsonObject);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return json.toString();
	}

	public static MyPath parseJsonToMyPath(JSONObject pathObject) {
		MyPath path = new MyPath();
		try {
			// ����ߵĿ�Ⱥ���ɫ
			path.setPaintColorWidth(pathObject.getInt("color"),
					pathObject.getInt("width"));
			// ��þ���
			JSONObject rectObject = pathObject.getJSONObject("rect");
			Rect rect = new Rect(rectObject.getInt("left"),
					rectObject.getInt("top"), rectObject.getInt("right"),
					rectObject.getInt("bottom"));
			path.setRect(rect);
			// ��õ�ļ���
			List<Point> points = path.getPointArray();
			JSONArray pointsArray = pathObject.getJSONArray("points");
			for (int i = 0; i < pointsArray.length(); i += 2) {
				points.add(new Point(pointsArray.getInt(i), pointsArray
						.getInt(i + 1)));
			}
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return path;
	}

	/**
	 * ���ͻغϽ���
	 * 
	 * @param type
	 * @return
	 */
	public static String createRoundOver(int type,int reason) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("reason", reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * ��Ϸ����
	 * @param type
	 * @return
	 */
	public static String createGameOver(int type) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * �غϽ�����Ķ���
	 * @param type ��������
	 * @param ip ���Ͷ������˵�ip
	 * @param animation_type ����������
	 * @return 
	 */
	public static String createAnimation(int type,String ip,int animation_type) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("ip", ip);
			jsonObject.put("animation", animation_type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * ����һ������˳���json��
	 * @param type
	 * @param ip
	 * @return
	 */
	public static String createExit(int type,String ip,boolean isServer,boolean isDrawer){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_EXIT);
			jsonObject.put("ip", ip);
			jsonObject.put("isserver", isServer);
			jsonObject.put("isdrawer", isDrawer);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * ����һ���˳������json��
	 * @param type ����
	 * @param ip �Լ���ip
	 * @param isServer �ǲ��Ƿ����ı��
	 * @return
	 */
	public static String createBack(int type,String ip,boolean isServer){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_BACK);
			jsonObject.put("ip", ip);
			jsonObject.put("isserver", isServer);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	
	/**
	 * ����һ���û�������json��
	 * @param type ����������
	 * @param ip �������˵�ip
	 * @return �û��������ַ���
	 */
	public static String createGiveup(int type){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_GIVEUP);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * ����һ��������json�ַ���
	 * @param type
	 * @return
	 */
	public static String createClear(int type){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_CLEAR);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
