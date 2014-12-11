package com.levelup.draw.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {  
    private static final String DB_NAME = "draw.db";  
    private static final String TBL_NAME = "drawAnswer";  
    private static final String CREATE_TBL = " create table "  
            + " drawAnswer(answer text,hint text) ";  
      
    private SQLiteDatabase db;
    
    public DBHelper(Context c) {  
        super(c, DB_NAME, null, 1);
        System.out.println("----------DBHelper-----------");
    }
    
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	
    	if(db == null )
    		System.out.println("onCreate  DB = null");
    	else
    		System.out.println("onCreate  DB != null");
    	this.db = db;
        db.execSQL(CREATE_TBL);
        
//        insert("������", "ˮ��");
//        insert("��Զ", "����");
//        insert("��˽䱳ϱ��", "Ъ����");
//        insert("����", "����");
//        insert("�ȿ�", "����");
//        insert("���ӽ�", "��ζ��");
//        insert("�޻���", "ʳ��");
//        insert("ë��", "�ճ���Ʒ");
//        insert("����", "��ף��Ʒ");
//        insert("����Ӱ", "����");
//        insert("��ŭ", "�о�");
//        insert("����̶", "�羰��ʤ");
//        insert("������ǹ", "����");
//        insert("�����", "�������");
//        insert("��Ŵ���", "������");
//        insert("����", "ְҵ");
//        insert("����", "����");
//        insert("����˹̹", "��ѧ��");
//        insert("��˵", "�������");
//        insert("����", "����");
//        insert("�궷��", "��Ϸ");
//        insert("������", "��������");
//        insert("��������������", "����");
//        insert("�Ǵ��Ź�", "����");
//        insert("������", "����");
//        insert("��ѻ", "����");
//        insert("����������", "����");
//        insert("����", "����");
//        insert("����", "��ͨ����");
        insert("���ѻ�", "Ӧ�ó���");
        insert("����", "�����豸");
        insert("�㽶", "ˮ��");
        insert("�ֻ�", "�����豸");
        insert("ˮ��", "������Ʒ");
        insert("�ڹ�", "����");
        insert("ϲ����", "��ͨ����");
        insert("������", "ֲ��");
        
    }
    
    public void insert(String answer ,String hint) {
        ContentValues values = new ContentValues();
        values.put("answer", answer);  
        values.put("hint", hint);  
        db.insert(TBL_NAME, null, values);
    }
    
    public void insertOnce(String answer ,String hint) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        
        values.put("answer", answer);  
        values.put("hint", hint);  
        db.insert(TBL_NAME, null, values);
        db.close();  
    }
    
    public Cursor query() {  
        SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);  
        return c;  
    }
    
    
    public void close() 
    {
        if (db != null)  
            db.close();  
    }
    
    public void show()
    {
    	System.out.println("-----------Show Start-----------");
    	Cursor c = this.query();
    	for(int i = 0; i < c.getCount(); i++)
    	{
    		c.moveToPosition(i);
    		System.out.println( c.getString(0)+"    "+c.getString(1)+c.getString(0).getBytes().length );
    		
    	}
    	System.out.println("-----------Show  Over-----------");
    }
    
    public String[] getRandomAnswerHint()
    {
    	Cursor c = this.query();
    	int pos = (int) (Math.random() * 1000);
    	pos = pos % c.getCount();
    	c.moveToPosition(pos);
    	
    	String[] s = new String[2];
    	s[0] = c.getString(0);
    	s[1] = c.getString(1);
    	return s;
    }
    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {  
    }  
} 