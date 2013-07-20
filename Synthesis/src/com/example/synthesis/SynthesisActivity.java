package com.example.synthesis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SynthesisActivity extends Activity{
	
	Bitmap img1; //Before画像　一時保存領域
	Bitmap img2; //After画像　一時保存領域
	int flg = 0;
	int imgflg = 0;
	ImageView image;
	int height = 0;
	int width = 0;

	 /** Called when the activity is first created. */
	private static final int REQUEST_GALLERY = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysnthesis);
      
        final Button bt2 =(Button) findViewById(R.id.Button02);
        bt2.setText("Back");
        
        final Button bt1 =(Button) findViewById(R.id.button3);
        bt1.setText("Decision");
        
        Intent intent1 = new Intent(); //Before画像
    	
        // ギャラリー呼び出し（Before）
        intent1.setType("image/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);    
        startActivityForResult(intent1, REQUEST_GALLERY);
       
        nextGallery();// After画像呼び出し処理
               
        //Backボタンの押下時の処理        
    	bt2.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
          startBackMainActivity();
        }
      });
    	
    	//Decisionボタンの押下時の処理        
    	bt1.setOnClickListener(new OnClickListener(){
        public void onClick(View v){
        	
        	bt1.setVisibility(View.INVISIBLE);
        	adddialog(); // 文字の配置処理呼び出し
        }
      });
        
    }   
    
    //After画像取得
    void nextGallery(){
    	Intent intent2 = new Intent(); //After画像
    	intent2.setType("image/*");
        intent2.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent2, REQUEST_GALLERY);
       
    }
    
    
    
    //画像結合の処理
   
   void synthesis(){
	  
       width= img1.getWidth();
       height = img1.getHeight();
	   
	
	  	
	  	//合成の向きを選択
	  	AlertDialog.Builder selectdlg = new AlertDialog.Builder(this);
	  	selectdlg.setTitle("Directions");	  	
	  	selectdlg.setMessage("Directions to synthesize it?");
	  	selectdlg.setPositiveButton("Right → Left", new DialogInterface.OnClickListener(){ 
	  		//貼り付けが右→左の場合
	  		public void onClick(DialogInterface dialog, int whichButton) {
	  			 //貼り付け元を作成
	  			final Bitmap newBitmap = Bitmap.createBitmap(width*2, height, Bitmap.Config.ARGB_8888);
	  			Canvas offScreen = new Canvas(newBitmap); 
	  			offScreen.drawBitmap(img1, 0, 0, (Paint)null); 
	  	        offScreen.drawBitmap(img2, height, 0, (Paint)null); 
	  	        image = (ImageView)findViewById(R.id.ImageView01);
	  	        image.setImageBitmap(newBitmap);
	  	        if(flg == 0){
	  	        
  	        	Paint paint = new Paint();
  	        	paint.setColor(Color.WHITE);
  	        	paint.setTextSize(20f);
	  	        offScreen.drawText("Before", 0, 20,paint ); // テキスト文字列合成
	  	        offScreen.drawText("After", width+10, 20,paint); // テキスト文字列合成
	  	        }
	  	        
	  			save(newBitmap);
	  		}
	  	});
	  	selectdlg.setNeutralButton("Top → Under", new DialogInterface.OnClickListener() {
	  		//貼り付けが上から下の場合 
	  		public void onClick(DialogInterface dialog, int whichButton) {
	  			//貼り付け元を作成
	  			final Bitmap newBitmap = Bitmap.createBitmap(width, height*2, Bitmap.Config.ARGB_8888);
	  			Canvas offScreen = new Canvas(newBitmap); 
	  			offScreen.drawBitmap(img1, 0, height, (Paint)null); 
	  	        offScreen.drawBitmap(img2, 0, 0, (Paint)null); 
	  	        
	  	        if(flg == 0){
	  
	  	        	Paint paint = new Paint();
	  	        	paint.setColor(Color.WHITE);
	  	        	paint.setTextSize(20f);
		  	        offScreen.drawText("Before", 0, 20,paint ); // テキスト文字列合成
		  	        offScreen.drawText("After", 0, height+20,paint); // テキスト文字列合成
		  	    }
	  	        
	  	        
	  	        image = (ImageView)findViewById(R.id.ImageView01);
	  	        image.setImageBitmap(newBitmap);
	  	        save(newBitmap);
	  	    
	  		}
	  	});
	  	selectdlg.show();

	  	
   }
    
   void adddialog( ){
	   
	 //文字を表示するかどうか
	  	AlertDialog.Builder adddlg = new AlertDialog.Builder(this);
	  	adddlg.setTitle("Add Text");
	  	adddlg.setMessage("Do you put the [Before]and[After] in the image?");
	  	adddlg.setPositiveButton("Yes", new DialogInterface.OnClickListener(){ 
	  		public void onClick(DialogInterface dialog, int whichButton) {
	  			flg = 0;
	  			synthesis();
	  		}
	  		
	
	  	});
	  	adddlg.setNeutralButton("No", new DialogInterface.OnClickListener() {
	  		public void onClick(DialogInterface dialog, int whichButton) {
	  			dialog.cancel(); 
	  			flg = 1;
	  			synthesis();
	  		}
	  	});
	  	adddlg.show();
  
	   
   }
   
   
    void save(Bitmap newBitmap){

        final String SAVE_DIR = "/MyPhoto/";
        //保存場所を取得
        File file = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);
        try{
            if(!file.exists()){
                file.mkdir();
            }
        }catch(SecurityException e){
            e.printStackTrace();
            throw e;
        }

        Date mDate = new Date();
        SimpleDateFormat fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = fileNameDate.format(mDate) + ".jpg";
        String AttachName = file.getAbsolutePath() + "/" + fileName;

        try {
            FileOutputStream out = new FileOutputStream(AttachName);
            newBitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
            try {
				throw e;
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
        }
            // save index
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = getContentResolver();
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.TITLE, fileName); 
        values.put("_data", AttachName);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    	
    }
    
    
    	//画像一時保存の処理
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          // TODO Auto-generated method stub
        	 
          if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
            	
            		if(imgflg == 0){
            	    //Before画像のコピー                   	
                   	 InputStream in = getContentResolver().openInputStream(data.getData());
	            	img1 = BitmapFactory.decodeStream(in);
	                imgflg = 1;
	            	in.close();
            		}else{
            			//Before画像のコピー
            			InputStream in = getContentResolver().openInputStream(data.getData());
    	            	img2 = BitmapFactory.decodeStream(in);
    	            	imgflg = 0;
    	            	
    	            	in.close();
    	            	
            		}
            } catch (Exception e) {            	 
            	//画像情報の取得に失敗時
            	AlertDialog.Builder dlg;
            	dlg = new AlertDialog.Builder(this);
            	dlg.setTitle("error");
            	dlg.setMessage("Failed to get the image information!");
            	dlg.setPositiveButton("OK", null);
            	dlg.show();
            }
          }
          
      } 
   
        
        //メイン画面に戻る。
    public void startBackMainActivity(){
      Intent intent=new Intent(this,MainActivity.class);
      startActivityForResult(intent,0);
      finish();
    }
    

    
    
    
}
