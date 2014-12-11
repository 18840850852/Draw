package com.draw.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.draw.R;
import com.levelup.draw.customview.DrawCanvaView;
import com.levelup.draw.customview.MyColorWidth;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.gif.GifView;
import com.levelup.draw.tranfer.TranferController;
import com.levelup.draw.tranfer.TranferService;
import com.levelup.draw.utils.DisplayUtil;
import com.levelup.draw.utils.PlayMusicUtil;
import com.levelup.draw.utils.SettingUtil;

public class DrawFragment extends Fragment implements OnClickListener {

	private View view;

	// �����е�view
	LinearLayout liner;
	DrawCanvaView myView;
	Button eraser, move, paint;
	Button menu;
	ImageButton giveup;
	TextView round, point, title, time,message;
	RelativeLayout final_layout;
	int first_show_place;
	
	// ��ʾ������
	String answer = "", drawerIp; // answer��Ҫ�ڴ�����
	int iround, iroundTotal;// , myPoints;
	private boolean noBodyGetIt = true;
	
	// ��������
	String currentTool = new String("paint");
	
	//����ʱ
	Timer timer = new Timer();
	int time_count = 60; //ʱ������ż���
	Activity activity = null;

	Dialog d = null;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			/*
			 * answer = bundle.getString("rightAnswer"); iround =
			 * bundle.getInt("round"); iroundTotal =
			 * bundle.getInt("roundTotal"); myPoints = bundle.getInt("points");
			 */
			drawerIp = bundle.getString("nextip");
			iround = bundle.getInt("round");
			iroundTotal = bundle.getInt("roundTotal");
			answer = bundle.getString("answer");
		}
		//********************************************����õ���Ŀ����Ϣ
		//��ʼ��ʱ
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						activity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() { 
								// TODO Auto-generated method stub
								if(time!=null){
									time.setText(time_count--+"");
									if(time_count==55){
										Bundle bundle =DrawFragment.this.getArguments();
										TranferService.player.sendAnswer(
												answer, bundle.getString("hint1"), bundle.getString("hint2"));

									}
									if(time_count<=10){
										//ִ�ж���
										Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bigsmall);
										time.startAnimation(animation);
									}
								}
							}
						});
					}
				}, new Date(), 1000);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_draw, container, false);
		initViews();
		return view;
	}

	// ��ʼ��view��������ʾ������
	private void initViews() {

		myView = new DrawCanvaView(getActivity(), getActivity().getWindowManager());
		myView.setBackgroundColor(Color.TRANSPARENT);
		
		liner = (LinearLayout) view.findViewById(R.id.canva);
		liner.removeAllViews();
		liner.addView(myView);

		

		eraser = (Button) view.findViewById(R.id.eraser);
		move = (Button) view.findViewById(R.id.move);
		paint = (Button) view.findViewById(R.id.paint);
		menu = (Button) view.findViewById(R.id.menu);
		giveup = (ImageButton)view.findViewById(R.id.giveup);
		
		
		giveup.setOnClickListener(this);
		menu.setOnClickListener(this);

		round = (TextView) view.findViewById(R.id.round);
		point = (TextView) view.findViewById(R.id.point);
		title = (TextView) view.findViewById(R.id.title);
		time = (TextView) view.findViewById(R.id.time);
		message = (TextView) view.findViewById(R.id.message);
		
		round.setText("��" + iround + "/" + iroundTotal + "��");
		for(MyInfo info:TranferService.infos){
			if(info.getIP().equals(drawerIp)){
				point.setText( info.getPoint()+"��");
				break;
			}
		}
		title.setText("��ĿΪ��" + answer);
		time.setText("60");

		paint.setOnClickListener(this);
		move.setOnClickListener(this);
		eraser.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.paint:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CHANGE);
			setToPaint();
			break;
		case R.id.move:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CHANGE);
			setToMove();
			break;
		case R.id.eraser:// ��Ƥ
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CHANGE);
			setToEraser();
			break;
		case R.id.menu:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_OPEN);
			createMenu();
			break;
		case R.id.giveup:
			giveUp();
			break;
		default:
			break;
		}
	}

	public void playGIF(int id) {
		// TODO Auto-generated method stub
		GifView gif1 = (GifView)final_layout.findViewById(R.id.final_gif1);
		GifView gif2 = (GifView)final_layout.findViewById(R.id.final_gif2);
		GifView gif3 = (GifView)final_layout.findViewById(R.id.final_gif3);
		int gifID = 0;
		switch (id) {
			case TranferController.ANIMATION_FLOWER:
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_FLOWER);
				gifID = R.drawable.gif_flower;
				break;
			case TranferController.ANIMATION_KISS:
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_KISS);
				gifID = R.drawable.gif_kiss;
				break;
			case TranferController.ANIMATION_EGG:
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_EGG);
				gifID = R.drawable.gif_egg;
				break;
			case TranferController.ANIMATION_SHOES:
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_SHOE);
				gifID = R.drawable.gif_shoe;
				break;
			default :
				break;
		}
		if(gif1.getVisibility() == View.INVISIBLE )
		{
			gif1.setGifImage(gifID);
			gif1.setVisibility(View.VISIBLE);
		}
		else if(gif2.getVisibility() == View.INVISIBLE )
		{
			gif2.setGifImage(gifID);
			gif2.setVisibility(View.VISIBLE);
		}
		else if(gif3.getVisibility() == View.INVISIBLE )
		{
			gif3.setGifImage(gifID);
			gif3.setVisibility(View.VISIBLE);
		}
		else
		{
			switch (first_show_place) {
				case 1:
					
					gif1.setGifImage(gifID);
					first_show_place = 2;
					break;
				case 2:
					gif2.setGifImage(gifID);
					first_show_place = 3;
					break;
				case 3:
					gif3.setGifImage(gifID);
					first_show_place = 1;
					break;
				default:
					break;
			}
		}
	}
	
	private void createMenu() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.scores, null);
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		
		SimpleAdapter sa = new SimpleAdapter(getActivity().getApplicationContext(), list, R.layout.scores_item,new String[] { "name","point" },
				// �ֱ��Ӧview ��id
				new int[] { R.id.item_name,R.id.item_point });
		
		((ListView) layout.findViewById(R.id.scores_list)).setAdapter(sa);

		int size = TranferService.infos.size();
		int[] scores = new int[size];
		for(int i = 0; i<size; i++)
			scores[i] = i;
		for(int i = 0; i<size; i++)
		{
			for(int j = size-1; j>i; j--)
			{
				if( TranferService.infos.get(scores[j]).getPoint() > TranferService.infos.get(scores[j-1]).getPoint() )
				{
					int temp = scores[j-1];
					scores[j-1] = scores[j];
					scores[j] = temp;
				}
			}
		}
		
		
		for (int i = 0; i < size; i++) 
		{
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("name",TranferService.infos.get(scores[i]).getName());
			item.put("point",TranferService.infos.get(scores[i]).getPoint()+"��");
			list.add(item);
		}
		sa.notifyDataSetChanged();
		
		final Dialog d = new Dialog(getActivity(), R.style.MyDialog);
		d.setContentView(layout);
		d.show();
		
		if(!DrawApplication.getApplicationInstance().getVolume())
		{
			layout.findViewById(R.id.scores_volume).setBackgroundResource(R.drawable.volume_off);
		}
		
		
		layout.findViewById(R.id.scores_resume).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
				d.cancel();
			}
		});
		
		layout.findViewById(R.id.scores_volume).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
				if(DrawApplication.getApplicationInstance().getVolume())
				{
					v.setBackgroundResource(R.drawable.volume_off);
					SettingUtil.set(getActivity(), "volume", false);
					DrawApplication.getApplicationInstance().setVolume(false);
				}
				else
				{
					v.setBackgroundResource(R.drawable.volume_on);
					SettingUtil.set(getActivity(), "volume", true);
					DrawApplication.getApplicationInstance().setVolume(true);
				}
			}
		});

		layout.findViewById(R.id.scores_quit).setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
				d.cancel();
			}
		});
	}

	private void giveUp() {
		// TODO Auto-generated method stub
		TranferService.player.sendGiveUp();
	}

	private void setToPaint() {
		if (currentTool.equals("paint")) {
			createColorWidthPick();
		} else {
			currentTool = "paint";
			myView.setToTool(currentTool);
			eraser.setBackgroundResource(R.drawable.eraser_n);
			paint.setBackgroundResource(R.drawable.paint_u);
			move.setBackgroundResource(R.drawable.move_n);
		}
	}

	private void setToEraser() {
		if (currentTool.equals("eraser")) {
			createTouchButton(eraser);
		} else {
			currentTool = "eraser";
			myView.setToTool(currentTool);
			eraser.setBackgroundResource(R.drawable.eraser_u);
			paint.setBackgroundResource(R.drawable.paint_n);
			move.setBackgroundResource(R.drawable.move_n);
		}
	}

	private void setToMove() {
		// TODO Auto-generated method stub
		if (currentTool.equals("move")) {
			createTouchButton(move);
		} else {
			currentTool = "move";
			myView.setToTool(currentTool);
			eraser.setBackgroundResource(R.drawable.eraser_n);
			paint.setBackgroundResource(R.drawable.paint_n);
			move.setBackgroundResource(R.drawable.move_u);
		}
	}

	private void createTouchButton(Button temp) {
		
		RelativeLayout layout = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		ImageButton content = new ImageButton(layout.getContext());
		final Dialog d = new Dialog(getActivity(), R.style.MyDialog);
		if( temp.getId() == R.id.eraser )
		{
			content.setBackgroundResource(R.drawable.clean_all);
			content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
					liner.removeAllViews();
					myView = new DrawCanvaView(getActivity(), getActivity()
							.getWindowManager());
					liner.addView(myView);
					TranferService.player.sendClear();
					d.cancel();
				}
			});
		}
		else
		{
			content.setBackgroundResource(R.drawable.view_all);
			content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
					myView.setToTool("fullSee");
					d.cancel();
				}
			});
			
		}
		
		layout.addView(content, lp);
		d.setContentView(layout);
		d.show();
//		final String[] mItems = { "����" };
//		final String[] mItems2 = { "ȫ������ʾ" };
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//		if (temp.getId() == R.id.eraser) {
//			builder.setItems(mItems, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					liner.removeAllViews();
//					myView = new DrawCanvaView(getActivity(), getActivity()
//							.getWindowManager());
//					liner.addView(myView);
//				}
//			});
//		} else {
//			builder.setItems(mItems2, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					myView.setToTool("fullSee");
//				}
//			});
//		}
//
//		AlertDialog dialog = builder.create();
//		Window win = dialog.getWindow();
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//		win.setAttributes(params);
//		dialog.setCanceledOnTouchOutside(true);// ���õ��Dialog�ⲿ��������ر�Dialog
//		dialog.show();

	}

	private void createColorWidthPick() {

		System.out.println("createColorPick");
		LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.color_width, null);
		
		
		double z = 1;
		
		z = DisplayUtil.getScreenWidth(getActivity()) / 1280.0f;
		
		final MyColorWidth colorWidth = new MyColorWidth(layout.getContext(),myView.getPaintColor(),myView.getPaintWidth(),z);  
		  
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
		lp.topMargin = 24;
		lp.leftMargin = 50;
		layout.addView(colorWidth,lp);
		
		final Dialog d = new Dialog(getActivity(), R.style.MyDialog);
		d.setContentView(layout);
		d.show();
		
		layout.findViewById(R.id.select_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
				myView.setPaintColor( colorWidth.currentColor );
		    	myView.setPaintWidth( colorWidth.currentWidth );
		    	//System.out.println(colorWidth.currentWidth);
		    	myView.setToTool(currentTool);
		    	d.cancel();
			}
		});
		
		layout.findViewById(R.id.select_cancle).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
				d.cancel();
			}
		});
//
//		LinearLayout layout = new LinearLayout(getActivity()
//				.getApplicationContext());
//		layout.setOrientation(LinearLayout.VERTICAL);
//		final MyColorWidth colorWidth = new MyColorWidth(layout.getContext(),
//				myView.getPaintColor(), myView.getPaintWidth(), 1.0);
//
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		lp.gravity = Gravity.CENTER_HORIZONTAL;
//
//		layout.addView(colorWidth, lp);
//
//		AlertDialog mAlertDialog = new AlertDialog.Builder(getActivity())
//				.setTitle("Choose")
//				.setView(layout)
//				.setPositiveButton(getString(R.string.dialog_color_OK),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								myView.setPaintColor(colorWidth.currentColor);
//								myView.setPaintWidth(colorWidth.currentWidth);
//								myView.setToTool(currentTool);
//
//							}
//						})
//				.setNegativeButton(getString(R.string.dialog_color_cancle),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.cancel();
//							}
//						}).create();
//
//		mAlertDialog.show();
		System.out.println("create over");
	}

	protected void showGameOver() {
		// ��ʾ�ܵķ�������ʱ��ת������

	}

	protected void showRoundOver() {
		// ��ʾ�𰸣���ʱ��ʧ

	}

	private void showMessage(String string) {
		// ��ʾһ������Ϊstring��֪ͨ
		//Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
		message.setText(string);
	}

	// �������˴���˵���Ϣ
	public void handleRight(Bundle bundle) {
		
		String rightIP = bundle.getString("ip");
		String rightNameString = "";
		int padd = 0;
		int dadd = 0;
		if (noBodyGetIt) {
			padd = 2;
			dadd = 3;
			noBodyGetIt = false;
		} else {
			padd = 1;
			dadd = 1;
		}
		for (MyInfo info : TranferService.infos) {
			// ���¶Ե��˼ӷ�
			if (info.getIP().equals(rightIP)) {
				info.setPoint(padd + info.getPoint());
				rightNameString = info.getName();
			}
			// �������˼ӷ�
			if (info.getIP().equals(drawerIp)) {
				info.setPoint(dadd + info.getPoint());
				point.setText(info.getPoint() + "��");
			}
		}
		showMessage(rightNameString + "�¶���");
	}

	// �������˴�����Ϣ
	public void handleWrong(Bundle bundle) {
		String wrongIpString = bundle.getString("wrongip");
		String wrongANswerString = bundle.getString("wronganswer");
		for(MyInfo info:TranferService.infos)
		{
			if(info.getIP().equals(wrongIpString))
			{
				showMessage(info.getName() + "�µ���" + wrongANswerString);
				break;
			}
		}
		
	}

	// ����غϽ���
	public void handleEnd(int reason) {
		if(reason==TranferController.ALL_RIGHT){
			showMessage("�����˶�����ˣ���Ϸ����");
		}else if(reason==TranferController.TIME_OVER){
			showMessage("ʱ�䵽");
		}
		showFinal();
		//���غϽ�����ʱ��ֹͣ��ʱ
		timer.cancel();
	}

	private void showFinal() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final_layout = (RelativeLayout) inflater.inflate(R.layout.show_final, null);
		first_show_place = 1;
		( (ImageView)final_layout.findViewById(R.id.final_canva_bg)).setImageBitmap(myView.surfaceBitmap);
		d = new Dialog(getActivity(), R.style.MyDialog_NoBG);
		d.setContentView(final_layout);
		d.setCanceledOnTouchOutside(false);
		d.show();
		final_layout.findViewById(R.id.final_buttons).setVisibility(View.GONE);
		((TextView)final_layout.findViewById(R.id.final_answer)).setText("��:"+answer);
		for(MyInfo info:TranferService.infos){
			if(info.getIP().equals(drawerIp)){
				((TextView)final_layout.findViewById(R.id.final_name)).setText("����"+info.getName()+"����ͼ");
				break;
			}
		}
		
	}

	//�����������˳�����
	public void handleExit(String ip) {
		// TODO Auto-generated method stub
		for(MyInfo info:TranferService.infos){
			if(info.getIP().equals(ip)){
				showMessage(info.getName()+"�˳��˷���");
			}
		}
	}
	
	//��ʧ
	public void dismissDialog(){
		
		if(d!=null && d.isShowing()){
			d.cancel();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( myView.surfaceBitmap != null )
		{	
			myView.surfaceBitmap.recycle();
		}
		try{
			timer.cancel();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		super.onDestroy();
	}

}
