package com.ray.androidprogecttools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ray.androidprogecttoolsm.model.NetworkInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Android开发工具
 * 
 * @author RayWang
 * 
 */
public class AndroidTools {
	/**
	 * 定义一个全局的Log日志输出，以便日后统一关闭
	 */
	public static void log(Context context, String message) {
		if (isDebuggable(context)) {
			log(context, context.getPackageName(), message);
		}
	}

	/**
	 * 定义一个全局的Log日志输出，以便日后统一关闭
	 */
	public static void log(Context context, String tag, String message) {
		if (isDebuggable(context)) {
			Log.e(tag, message);
		}
	}

	/**
	 * 获取字符串
	 * 
	 * @return
	 */
	public static String getString(Context context, int id) {
		return context.getString(id);
	}

	/**
	 * 获取string.xml中Boolean类型值
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static boolean getBoolean(Context context, int id) {
		return context.getResources().getBoolean(id);
	}

	/**
	 * 保存数据到SD卡
	 * 
	 * @param data
	 * @param path
	 */
	public static void saveDataToSDCard(byte[] data, String path) {
		DataOutputStream dataOutStream = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			dataOutStream = new DataOutputStream(new FileOutputStream(path));
			dataOutStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dataOutStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 得到图片的类型
	 * 
	 * @param data
	 * @return
	 */
	public static String getFileType(byte[] data) {
		if (data[0] == (byte) 0x42 && data[1] == (byte) 0x4d)
			return "bmp";
		if (data[0] == (byte) 0x47 && data[1] == (byte) 0x49
				&& data[2] == (byte) 0x46 && data[3] == (byte) 0x38)
			return "gif";
		if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8)
			return "jpg";
		if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50
				&& data[2] == (byte) 0x4e && data[3] == (byte) 0x47)
			return "png";
		return null;
	}

	/**
	 * 获取软件包名
	 * 
	 * @return
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	/**
	 * 获取versionCode（ANDROID版本号）
	 */
	public static int getVersionCode(Context context) {
		int versioncode = 0;
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					getPackageName(context), 0);
			versioncode = pinfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versioncode;
	}

	/**
	 * 是否为调试模式
	 * 
	 * @return
	 */
	public static boolean isDebuggable(Context context) {
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					getPackageName(context), 0);
			if (pinfo != null) {
				int flags = pinfo.applicationInfo.flags;
				return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取网络信息
	 * 
	 * @param activity
	 * @return
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		NetworkInfo myNetworkInfo = new NetworkInfo();
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo networkInfo = manager
					.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected()) {
				myNetworkInfo.setConnectToNetwork(true);
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					myNetworkInfo.setProxy(false);
					myNetworkInfo.setProxyName(networkInfo.getTypeName());
				} else {
					// 取得代理信息
					String proxyHost = android.net.Proxy.getDefaultHost();
					if (proxyHost != null) {
						myNetworkInfo.setProxy(true);
						myNetworkInfo.setProxyHost(proxyHost);
						myNetworkInfo.setProxyPort(android.net.Proxy
								.getDefaultPort());
					} else {
						myNetworkInfo.setProxy(false);
					}
					myNetworkInfo.setProxyName(networkInfo.getExtraInfo());
				}
			} else {
				myNetworkInfo.setConnectToNetwork(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myNetworkInfo;
	}

	/**
	 * 获取IP
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress instanceof Inet4Address)) {
						String ip = inetAddress.getHostAddress().toString();
						if (ip.startsWith("10.")) {
							return "";
						} else if (ip.startsWith("192.168.")) {
							return "";
						} else if (ip.startsWith("176")
								&& (Integer.valueOf(ip.split(".")[1]) >= 16)
								&& (Integer.valueOf(ip.split(".")[1]) <= 31)) {
							return "";
						} else {
							return ip;
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取wifi的mac地址
	 * 
	 * @return
	 */
	public static String getMacAddress(Context context) {
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String mac = info.getMacAddress();
			if (null != mac) {
				return mac;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得当前日期和时间 格式 yyyy-MM-dd HH:mm
	 */
	public static String getCurrentDateTimeNoSS() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 获得当前日期和时间 格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentDateTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 获得当前日期和时间 格式yyyy-MM-dd HH:mm:ss:SS
	 */
	public static String getCurrentDateTimeWithSS() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 获得当前时间
	 */
	public static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 获得当前时间
	 */
	public static String getCurrentTimeMM() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 返回当前时间，单位毫秒
	 * 
	 * @return
	 */
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 获得当前日期
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String current_time = sdf.format(date);
		return current_time;
	}

	/**
	 * 获得天数
	 */
	public static int getDayNum(long millisTime) {
		int day = (int) (millisTime / (1000 * 60 * 60 * 24));
		if (millisTime % (1000 * 60 * 60 * 24) != 0) {
			return day + 1;
		}
		return day;
	}

	/**
	 * 返回两次的时间差的显示方式
	 * 
	 * @param startTime
	 *            开始时间
	 * @param nowTime
	 *            结束时间
	 * @return
	 */
	public static String showRuleTime(long startTime, long nowTime) {
		String re = "";
		long difftime = nowTime - startTime;
		if (difftime < 0) {
			re = "0秒前";
		} else if (difftime < 60 * 1000) {
			// 小于60s
			re = difftime / 1000 + "秒前";
		} else if (difftime < 60 * 60 * 1000) {
			// 小于60min
			re = (difftime / 1000) / 60 + "分钟前";
		} else {
			Date date_start = new Date(startTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String nowDay = formatter.format(new Date(nowTime));
			String yesterDay = formatter.format(new Date(nowTime - 24 * 60 * 60
					* 1000));
			String startDay = formatter.format(date_start);
			if (startDay.equals(nowDay)) {
				SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
				re = "今天  " + myFormatter.format(date_start);
			} else if (startDay.equals(yesterDay)) {
				SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
				re = "昨天  " + myFormatter.format(date_start);
			} else {
				SimpleDateFormat myFormatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				re = myFormatter.format(date_start);
			}
		}
		return re;
	}

	/**
	 * 判断两个时间差
	 * 
	 * @param before
	 *            上一次的时间
	 * @param after
	 *            本次的时间
	 * @param defaultDiff
	 *            需要的差距
	 * @return
	 */
	public static boolean dateDiff(String beforeTime, String nowTime,
			long defaultDiff) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date_before = formatter.parse(beforeTime);
			Date date_after = formatter.parse(nowTime);
			long now_time = date_after.getTime();
			long before_time = date_before.getTime();
			long diff = now_time - before_time;
			if (diff - defaultDiff > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 取出粘贴版内容
	 */
	public static String getPasteText(Context context) {
		ClipboardManager clipMan = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		String text = clipMan.getText().toString();
		clipMan.setText("");
		return text;
	}

	/**
	 * 复制内容到粘贴版
	 */
	public static void setPasteText(Context context, String content) {
		ClipboardManager clipMan = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipMan.setText(content);
	}

	/**
	 * 安装APK
	 * 
	 * @param activity
	 * @param path
	 * @return 是否成功
	 */
	public static boolean installApk(Activity activity, String path) {
		File file = new File(path);
		if (file.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			activity.startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加桌面快捷方式
	 * 
	 * @param activity
	 * @param intent
	 *            点击图标启动intent
	 * @param icon
	 *            桌面icon
	 */
	public static void addShortcut(Activity activity, int icon, Activity start) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				activity.getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		// ComponentName comp = new ComponentName(activity.getPackageName(),
		// "."+activity.getLocalClassName());

		shortcut.putExtra(
				Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(activity, start.getClass())
						.setAction(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_LAUNCHER)
						.setFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				activity, icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		activity.sendBroadcast(shortcut);
	}

	/**
	 * 限制特殊字符 密码输入等处需要做判断
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static boolean limitSpecialCharacters(String str) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ 　]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return !m.replaceAll("").equalsIgnoreCase(str);
	}

	/**
	 * dip转成pixel
	 * 
	 * @param dip
	 *            dip尺寸
	 * @return
	 */
	public static int dipToPixel(Context context, float dip) {
		return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5);
	}

	/**
	 * 
	 * 处理scale
	 * */
	public static int executeScale(Context context) {
		// 屏幕缩放比
		float scale = 0;
		scale = context.getResources().getDisplayMetrics().density;
		int scaleScreen = (int) (45 * scale);
		return scaleScreen;
	}

	/**
	 * 图片的不等比缩放
	 * 
	 * @param src
	 *            源图片
	 * @param destWidth
	 *            缩放的宽度
	 * @param destHeigth
	 *            缩放的高度
	 * @return
	 */
	public static Bitmap lessenBitmap(Bitmap src, int destWidth, int destHeigth) {
		try {
			if (src == null)
				return null;

			int w = src.getWidth();// 源文件的大小
			int h = src.getHeight();
			float scaleWidth = ((float) destWidth) / w;// 宽度缩小比例
			float scaleHeight = ((float) destHeigth) / h;// 高度缩小比例
			Matrix m = new Matrix();// 矩阵
			m.postScale(scaleWidth, scaleHeight);// 设置矩阵比例
			return Bitmap.createBitmap(src, 0, 0, w, h, m, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从指定路径读取图片（原图读取 不会改变大小）
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap readBitmapFormPath(String imagePath) {
		if (TextUtils.isEmpty(imagePath)) {
			return null;
		}
		try {
			Bitmap bitmap = null;
			File file = new File(imagePath);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(imagePath);
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 从sdcard或data文件夹读取图片
	 * 
	 * @param context
	 * @param imagePath
	 * @return
	 */
	public static Bitmap createBitmapFormSdcardOrData(String imagePath) {
		if (null == imagePath) {
			return null;
		}
		InputStream stream = null;
		try {
			File file = new File(imagePath);
			if (!file.exists())
				return null;
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(imagePath), null, o);

			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
					imagePath), null, o2);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 图片圆角处理
	 * 
	 * @param bitmap
	 *            需要处理的图片
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = 4;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		} else {
			return null;
		}

	}

	/**
	 * 从assets文件夹读取图片
	 * 
	 * @param context
	 * @param imagePath
	 *            图片路径
	 * @return
	 */
	public static Bitmap createBitmapFormAssets(Context context,
			String imagePath) {
		InputStream stream = null;
		try {
			if (imagePath != null) {
				stream = context.getAssets().open(imagePath);
			}
			if (stream != null) {
				return Bitmap.createBitmap(BitmapFactory.decodeStream(stream));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获得手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {
		try {
			String phoneVersion = android.os.Build.MODEL;
			if (null != phoneVersion) {
				return phoneVersion;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用系统短信
	 * 
	 * @param context
	 *            Activity自身
	 * @param body
	 *            信息内容
	 */
	public static void sendSms(Activity activity, String phoneNumber,
			String body) {
		try {
			Uri smsToUri = Uri.parse("smsto:" + phoneNumber);// 联系人地址
			Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			intent.putExtra("address", phoneNumber);
			intent.putExtra("sms_body", body);// 短信的内容
			activity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用系统电话
	 * 
	 * @param context
	 * @param url
	 */
	public static void openSystemPhone(Activity activity, String phoneNum) {
		try {
			Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phoneNum));
			activity.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用系统浏览器
	 * 
	 * @param activity
	 * @param url
	 */
	public static void openSystemBrowser(Activity activity, String url) {
		try {
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			activity.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用系统网络设置
	 * 
	 * @param activity
	 */
	public static void openSystemNetworkSetting(Activity activity) {
		try {
			Intent i = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得屏幕的宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 获得屏幕的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 获取使用内存大小
	 */
	public static int getMemory(Context context) {
		int pss = 0;
		ActivityManager myAM = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo> appProcesses = myAM
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)) {
				int pids[] = { appProcess.pid };
				Debug.MemoryInfo self_mi[] = myAM.getProcessMemoryInfo(pids);
				pss = self_mi[0].getTotalPss();
			}
		}
		return pss;
	}

	/**
	 * 获得CPU使用率
	 */
	public static int getCpuInfo() {
		int cpu = 0;
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			cpu = (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return cpu;
	}

	/**
	 * 获得手机IMEI
	 * 
	 * @return
	 */
	public static String getIMEI(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (null != imei) {
				return imei;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得手机IMSI
	 * 
	 * @return
	 */
	public static String getIMSI(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			if (null != imsi) {
				return imsi;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 全屏切换
	 * 
	 * @param activity
	 * @param isNotFullScreen
	 *            true非全屏 false全屏
	 */
	public static void setFullScreen(Activity activity, boolean isNotFullScreen) {
		try {
			if (isNotFullScreen) {
				activity.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				activity.getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			activity.getMenuInflater();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到系统亮度
	 * 
	 * @return
	 */
	public static int getSystemBrightness(Context context) {
		int brightness = 5;
		try {
			brightness = Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			brightness = brightness * 100 / 255;
		} catch (SettingNotFoundException ex) {
			ex.printStackTrace();
		}
		return brightness >= 5 ? brightness : 5;
	}

	/**
	 * 调节屏幕亮度
	 * 
	 * @param value
	 */
	public static void setBackLight(Activity activity, int value) {
		try {
			WindowManager.LayoutParams lp = activity.getWindow()
					.getAttributes();
			lp.screenBrightness = (float) (value * (0.01));
			activity.getWindow().setAttributes(lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此方法只能设置当前activity 调节屏幕亮度及alpha值
	 * 
	 * @param value
	 */
	public static void setBackAppLight(Activity activity, int value) {
		try {
			WindowManager.LayoutParams lp = activity.getWindow()
					.getAttributes();
			lp.screenBrightness = (float) (value * (0.01));
			// if (value > 40) {
			// lp.alpha = (float) (value * (0.01));
			// }
			if (activity != null) {
				Activity parent = activity.getParent();
				if (parent != null) {
					Window window = parent.getWindow();
					if (window != null) {
						window.setAttributes(lp);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取sdcard或data的剩余空间
	 */
	public static long getSdcardFreeSize(String rootPath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(rootPath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// 剩余空间大小
		long freeSize = availaBlock * blocSize;
		return freeSize;
	}

	/**
	 * 获得系统版本号
	 * 
	 * @return
	 */
	public static String getSDK() {
		try {
			String release = android.os.Build.VERSION.RELEASE;
			if (null != release) {
				return release;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/** 全局统一吐司类 */
	private static Toast mToast;

	/**
	 * 小提示
	 * 
	 * @param context
	 * @param content
	 *            提示内容
	 * @param timer
	 *            是否长时间提醒
	 */
	public static void showToast(Context context, String content,
			boolean longTime) {
		if (content != null && context != null) {
			int timer = Toast.LENGTH_SHORT;
			if (longTime) {
				timer = Toast.LENGTH_LONG;
			}
			if (mToast == null) {
				Toast toast = new Toast(context);
				toast.setDuration(timer);
				mToast = toast;
			} else {
				mToast.setDuration(timer);
			}

			mToast.show();
		}
	}

	/**
	 * 判断当前是否符合桌面显示的对话框
	 * 
	 * @param context
	 * @return
	 */
	public static boolean pushDeskFlag(Context context) {
		boolean deskFlag = false;
		String taskNameTop = "";
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);
		if (tasksInfo.size() > 0) {
			taskNameTop = tasksInfo.get(0).topActivity.getPackageName();
		} else {
			return true;
		}
		for (int i = 0; i < tasksInfo.size(); i++) {
			if (context.getPackageName().equals(
					tasksInfo.get(i).topActivity.getPackageName())) {
				return false;
			}
		}
		List<String> names = getAllTheLauncher(context);
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(taskNameTop)) {
				deskFlag = true;
			}
		}
		return deskFlag;
	}

	/**
	 * 获取所有的launcher信息
	 * 
	 * @param context
	 * @return
	 */
	private static List<String> getAllTheLauncher(Context context) {
		List<String> names = null;
		PackageManager pkgMgt = context.getPackageManager();
		Intent it = new Intent(Intent.ACTION_MAIN);
		it.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> ra = pkgMgt.queryIntentActivities(it, 0);
		if (ra.size() != 0) {
			names = new ArrayList<String>();
		}
		for (int i = 0; i < ra.size(); i++) {
			String packageName = ra.get(i).activityInfo.packageName;
			names.add(packageName);
		}
		return names;
	}

	/**
	 * 判断手机是否有发送短信权限
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUseSendSMSPermission(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_PERMISSIONS);
			String[] permissions = pInfo.requestedPermissions;
			for (String s : permissions) {
				if (s.trim().equals(android.Manifest.permission.SEND_SMS))
					return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置Selector
	 */
	public static StateListDrawable newSelector(Context context, int[] state) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = state[0] == -1 ? null : context.getResources()
				.getDrawable(state[0]);
		Drawable pressed = state[1] == -1 ? null : context.getResources()
				.getDrawable(state[1]);
		Drawable focused = state[1] == -1 ? null : context.getResources()
				.getDrawable(state[1]);
		Drawable unable = state[0] == -1 ? null : context.getResources()
				.getDrawable(state[0]);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		// View.WINDOW_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}

	/**
	 * 切换软键盘
	 */
	public static void switchKeyBoardCancle(Activity activity) {
		try {

			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			// 得到InputMethodManager的实例
			if (imm.isActive()) {
				// 如果开启
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
						InputMethodManager.HIDE_NOT_ALWAYS);
				// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeKeyBoard(Activity activity) {

		try {
			InputMethodManager im = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * MD5加密
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.substring(0, md5StrBuff.length()).toString();
	}

	/** 上次点击的时间 */
	private static long lastClickTime;

	/**
	 * 按钮是不是连续被按下
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick(int timeDifference) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < timeDifference) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 截取并按规则组合字符串
	 * 
	 * @return
	 */
	public static String subAndCombinationString(String str, int subLength,
			boolean isReduction) {
		if (isReduction) {
			String str1 = str.substring(0, subLength);
			String str2 = str.replace(str1, "");
			String result = str2 + str1;
			return result;
		} else {
			String temp = str.substring(0, str.length() - subLength);
			String str1 = temp.substring(0, subLength);
			String str2 = temp.replace(str1, "");
			String str3 = str.replace(temp, "");
			String result = str3 + str1 + str2;
			return result;
		}
	}

	/**
	 * 获取屏幕亮度
	 */
	public static int getScreenBrightness(Activity activity) {

		int nowBrightnessValue = 0;
		ContentResolver resolver = activity.getContentResolver();
		try {
			nowBrightnessValue = android.provider.Settings.System.getInt(
					resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}

	/**
	 * 获取系统亮度模式
	 * 
	 * @param defaultValue
	 *            手动模式：0
	 * @return
	 */
	public static int getBrightnessMode(Activity activity, int defaultValue) {
		int brightnessMode = defaultValue;
		try {
			brightnessMode = Settings.System.getInt(
					activity.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException snfe) {
		}
		return brightnessMode;

	}

	/**
	 * 设置控件所在的坐标位置
	 * 
	 * @param view
	 *            要设置位置的view
	 * @param left
	 *            左边距离
	 * @param top
	 *            上边距离
	 * @param right
	 *            右边距离
	 * @param bottom底部距离
	 */
	public static void setRelativeLayoutPosition(View view, int left, int top,
			int right, int bottom) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		margin.setMargins(left, top, right, bottom);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	/**
	 * 等比缩放图片
	 * 
	 * @param bitmap
	 *            将要被缩放的图片
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @return
	 */
	public static Bitmap calculateScale(Bitmap bitmap, int width, int height) {
		try {
			if (bitmap == null) {
				return null;
			}
			int bitmapW = bitmap.getWidth();
			int bitmapH = bitmap.getHeight();

			if (bitmapW * height > width * bitmapH) {
				height = width * bitmapH / bitmapW;
			}
			if (bitmapW * height < width * bitmapH) {
				width = height * bitmapW / bitmapH;
			}

			float scaleWidth = (float) width / bitmapW;
			float scaleHeight = (float) height / bitmapH;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapW,
					bitmapH, matrix, true);

			return newBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取手机sdk版本
	 */
	public static int getSdkVersion() {
		int sdkVersion = 0;
		try {
			sdkVersion = Integer.parseInt(VERSION.SDK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdkVersion;
	}

	/**
	 * 读取图片资源
	 * 
	 * @param context
	 *            上下文
	 * @param resId
	 *            资源id
	 * @return
	 */
	public static Bitmap readBitmap(Context context, int resId) {
		InputStream stream = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			stream = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(stream, null, opt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取dimen文件中的值
	 * 
	 * @param id
	 * @return
	 */
	public static float getDimensValue(Context context, int id) {
		return context.getResources().getDimension(id);
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = context.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}
}
