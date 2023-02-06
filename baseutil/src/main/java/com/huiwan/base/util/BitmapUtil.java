package com.huiwan.base.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.platform.ThreadUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getName();
    private static int MAX_WIDTH = 2000;
    private static int MAX_HEIGHT = 2000;

    public static Bitmap decodeBitmap(String file) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        adjustOptions(options);
        return BitmapFactory.decodeFile(file, options);
    }

    private static void adjustOptions(Options options) {
        int width = options.outWidth;
        int height = options.outHeight;
        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            int scale = 1;
            int outWidth = width;
            int outHeight = height;
            while (outWidth > MAX_WIDTH || outHeight > MAX_HEIGHT) {
                scale *= 2;
                outWidth /= 2;
                outHeight /= 2;
            }
            options.inSampleSize = scale;
        }
        if (-1 == options.outWidth || -1 == options.outHeight) {
            options.inJustDecodeBounds = true;
        } else {
            options.inJustDecodeBounds = false;
        }
    }

    public static Bitmap decodeBitmap(String file, int targetWidth, int targetHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        options.inSampleSize = computeScale(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    /**
     * 根据目标的宽和高来计算Bitmap缩放比例。默认不缩放
     */
    public static int computeScale(BitmapFactory.Options options, int targetWidth, int targetHeight){
        int inSampleSize = 1;
        if(targetWidth == 0 || targetHeight == 0){
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于设定宽高，则计算缩放比例
        if(bitmapWidth > targetWidth || bitmapHeight > targetWidth){
            int widthScale = (int) Math.ceil((float) bitmapWidth / (float) targetWidth);
            int heightScale = (int) Math.ceil((float) bitmapHeight / (float) targetHeight);
            inSampleSize = Math.max(widthScale, heightScale);
        }
        return inSampleSize;
    }

    public static boolean compressBmpToFile(Bitmap bitmap,File file){
        if (bitmap == null || file == null) return false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(100 * 1024);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            if (baos.size()/1024 > 100) {
                quality /= Math.ceil(baos.size() / 1024 / 100);
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }


            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
            return true;

        } catch (Exception e) {
            FileUtil.safeDeleteFile(file.getAbsolutePath());
            ToastUtil.debugShow("save bitmap failed");
        }
        return false;
    }

    public static int getOrientation(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e){
            debugShow( e.toString());
        }
        return degree;
    }

    public static boolean rotateBitmapIfNeed(Bitmap temp, String path, int orientation){
        if(orientation == 0){
            return true;
        }

        Matrix m = new Matrix();
        m.postRotate(orientation);//rotate if need

        Bitmap bitmap = null;
        try{
            bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), m, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new BufferedOutputStream(new FileOutputStream(path)));
            return true;

        }catch(OutOfMemoryError err){
            debugShow("get rotated bitmap error, OOM ");
            return false;

        }catch(Exception e){
            debugShow(e.toString());
            return false;

        }finally {
            if(temp != null && !temp.isRecycled()) temp.recycle();
            if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
    }

    public static boolean rotateBitmapIfNeed(String path, int orientation){
        Log.d(TAG, "orientation : " + orientation);
        if(orientation == 0){
            return true;
        }

        Matrix m = new Matrix();
        m.postRotate(orientation);//rotate if need

        Bitmap temp = null;
        Bitmap bitmap = null;
        try{
            temp = BitmapFactory.decodeFile(path);
            bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), m, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(path));
            return true;

        }catch(OutOfMemoryError err){
            debugShow("get rotated bitmap error, OOM ");
            return false;

        }catch(Exception e){
            debugShow(e.toString());
            return false;

        }finally {
            if(temp != null && !temp.isRecycled()) temp.recycle();
            if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
    }
    /**
     * 保存一张图片（压缩策略： 图片质量从60开始上涨，保证图片在60K以上）
     * @param bitmap  需要保存的bitmap
     * @param file  保存的文件路径
     * @return  是否保存成功
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        try {
            FileUtil.safeDeleteFile(file);
            FileUtil.createFile(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 100);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            if(baos.size()/1024 < 60){
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                if(baos.size()/1024 < 60){
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.reset();
            baos.close();
            return true;

        } catch (Exception e) {
            FileUtil.safeDeleteFile(file);
        }
        return false;
    }

    /*
     * 将图片 bitmap 保存到图库
     */
    public static void saveBitmap(File file,Bitmap bitmap,SaveCallback callback) {
        ThreadUtil.runInOtherThread(() -> {
            try {
                //文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                //压缩图片，如果要保存png，就用Bitmap.CompressFormat.PNG，要保存jpg就用Bitmap.CompressFormat.JPEG,质量是100%，表示不压缩
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                //写入，这里会卡顿，因为图片较大
                fileOutputStream.flush();
                //记得要关闭写入流
                fileOutputStream.close();
                //成功的提示，写入成功后，请在对应目录中找保存的图片
                ThreadUtil.runOnUiThread(() -> {
                    if (callback != null) callback.onSuccess();

                });
            } catch (IOException e) {
                e.printStackTrace();
                ThreadUtil.runOnUiThread(() -> {
                    if (callback != null) callback.onFailed();
                });
            }
        });

    }

    public static boolean saveBitmapWithoutCompress(Bitmap bitmap, File file) {
        if (bitmap == null || file == null) return false;
        try {
            FileUtil.safeDeleteFile(file);
            FileUtil.createFile(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 100);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.reset();
            baos.close();
            return true;
        } catch (Exception e) {
            FileUtil.safeDeleteFile(file);
        }
        return false;
    }


    public static boolean saveOriginBitmap(Bitmap bitmap, Bitmap.CompressFormat format, File file) {
        try {
            FileUtil.safeDeleteFile(file);
            FileUtil.createFile(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 100);
            bitmap.compress(format, 100, baos);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.reset();
            baos.close();
            return true;

        } catch (Exception e) {
            FileUtil.safeDeleteFile(file);
        }
        return false;
    }

    /**
     * 图片镜像翻转
     * @param temp 原图
     * @param isHorizontal 是否是水平方向翻转
     * @return
     */
    public static Bitmap mirrorBitmap(Bitmap temp, boolean isHorizontal){
        int w = temp.getWidth();
        int h = temp.getHeight();
        Matrix m = new Matrix();
        if (isHorizontal){
            m.postScale(-1f, 1f);//horizontal mirror
        } else {
            m.postScale(1f, -1f);//
        }

        Bitmap bitmap = Bitmap.createBitmap(temp, 0, 0, w, h, m, true);
        return bitmap;
    }

    /**
     * 获取一个空的bitmap
     */
    public static Bitmap getEmptyBitmap(int width, int height){
        if (width <= 0) width = 1;
        if (height <= 0) height = 1;
        if (width > 2048) width = 2048;
        if (height > 2048) height = 2048;
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public static void debugShow(String s){
        if (LibBaseUtil.buildDebug()) {
            Log.e(TAG, "debugShow: "+ s );
        }
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_4444);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            LibBaseUtil.logInfo("BitmapUtil", "error get bitmap from drawable: {}", e);
            return null;
        }
    }


    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
    }

}
