package com.huiwan.base.util;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 检测截屏辅助类
 * @author w
 *
 */
public class ObserveSafeGuardUtil {
    private Context context;
    private int timeSecond;
    private ScreenShotCallback callback;
    private Thread thread;
	private boolean forever = false;
    private int screenshotCount = -1;
	private int screenRecoderCount = -1;
    private boolean needCheck = true;
    private int intervalTime = 200;
    private boolean checkVideo = false;
	private boolean checkVPN = false;
    
    /**
     * 
     * @param context 上下文
     * @param timeSecond 检测时长
     * @param callback 事件回调
     */
	public ObserveSafeGuardUtil(Context context, int timeSecond,
								ScreenShotCallback callback) {
		this.context = context;
		this.timeSecond = timeSecond;
		this.callback = callback;
	}

	public boolean isForever() {
		return forever;
	}

	public void setForever(boolean forever) {
		this.forever = forever;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public boolean isCheckVideo() {
		return checkVideo;
	}

	public void setCheckVideo(boolean checkVideo) {
		this.checkVideo = checkVideo;
	}

	public boolean isCheckVPN() {
		return checkVPN;
	}

	public void setCheckVPN(boolean checkVPN) {
		this.checkVPN = checkVPN;
	}

	/**
	 * 检测截屏事件
	 */
	public void observeScreenShot() {
		needCheck = true;
		if(thread != null) {
			return;
		}

		final long time = System.currentTimeMillis();
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						if(!forever && System.currentTimeMillis() - time > (timeSecond+1) * 1000L) {
							break;//立即结束线程
						}else if(needCheck){
							checkScreenShot();
	                        checkVideoRecorder();
							checkUseVpn();
						}
						Thread.sleep(intervalTime > 0 ? intervalTime : 200);
					} catch (Exception e) {
                        e.printStackTrace();
					}
				}	
			}
		});	
		thread.start();
		
	}

	private void checkVideoRecorder() {
		if (checkVideo) {
			if (!hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)){
				return;
			} else if (screenRecoderCount == -1){
				screenRecoderCount = getScreenRecorderCount(context);
			}
			int videoCount = getScreenRecorderCount(context);
			if(videoCount - screenRecoderCount >= 1 ) {//可能录了多次
				//检测到录屏
				if(callback != null)
					callback.onScreenRecorder(videoCount - screenRecoderCount);
			}
			screenRecoderCount = videoCount;
		}
	}

	private void checkUseVpn() {
		if (!hasPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)){
			return;
		}
		if (checkVPN) {
			if (isDeviceInVPN()){
				if(callback != null)
					callback.onUseVpn();
			}
		}
	}

	public void unObserveScreenShot(boolean check, int delay) {
		screenshotCount = getScreenShotCount(context);
		needCheck = false;
		timeSecond = 0;
		forever = false;
		if (check) {
			if (delay > 0) {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						checkScreenShot();
						checkVideoRecorder();
						checkUseVpn();
					}
				}, delay);
			} else {
				checkScreenShot();
				checkVideoRecorder();
				checkUseVpn();
			}
		}
	}

	private void checkScreenShot() {
		if (!hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)){
			return;
		} else if (screenshotCount == -1){
			screenshotCount = getScreenShotCount(context);
		}
		int count = getScreenShotCount(context);
		if(count - screenshotCount >= 1 ) {//可能按了多次
			//检测到截屏
			if(callback != null)
				callback.onScreenShotDown(count - screenshotCount);
		}
		screenshotCount = count;
	}

	private int getScreenShotCount(Context context) {
    	int screenshotCount = 0;
    	try {
    		String columns[] = new String[]{  
                    Media.DATA,Media._ID,Media.TITLE,Media.DISPLAY_NAME, Media.BUCKET_DISPLAY_NAME
            }; 
        	Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
    		int nameIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);
    		int bucketIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
    		while(cursor.moveToNext()) {
    			String name = cursor.getString(nameIndex);
    			String bucketname = cursor.getString(bucketIndex);
    			
    			if( (bucketname != null && bucketname.startsWith("Screenshots")) ||
    					(name!=null && name.startsWith("S")) ) {
    				screenshotCount++;
    			}
    		}
    		cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenshotCount;
    }

	private int getScreenRecorderCount(Context context) {
		int screenRecoders = 0;
		try {
			String columns[] = new String[]{
					Media.DATA,Media._ID,Media.TITLE,Media.DISPLAY_NAME, Media.BUCKET_DISPLAY_NAME
			};
			Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
			int nameIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int bucketIndex = cursor.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			while(cursor.moveToNext()) {
				String name = cursor.getString(nameIndex);
				String bucketname = cursor.getString(bucketIndex);

				if( (bucketname != null && bucketname.startsWith("ScreenRecorder")) ||
						(name!=null && name.startsWith("S")) ) {
					screenRecoders++;
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenRecoders;
	}

	public static boolean isDeviceInVPN() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (nif.getDisplayName().equals("tun0") || nif.getDisplayName().equals("ppp0")) {
					Log.i("TAG", "isDeviceInVPN  current device is in VPN.");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void networkCheck(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			Network network = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				network = connectivityManager.getActiveNetwork();
			} else {
				network = connectivityManager.getAllNetworks()[0];
			}

			NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
			Log.i("TAG", "networkCapabilities -> " + networkCapabilities.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasPermission(Context context, String permission) {
		return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
	}

	public interface ScreenShotCallback {
	   void onScreenShotDown(int num);
	   default void onScreenRecorder(int num){};
	   default void onUseVpn(){};
   }

}
