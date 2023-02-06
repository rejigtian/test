package com.huiwan.base.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

/**
 * 快速高斯模糊背景,以及相关工具
 * @author rejig
 * date 2021/01/18
 */
public class FastBlur {
    public static final int DEFAULT_PIXEL = 100;

    /**
     * 模糊图片，缩放图片后{@link #getBlurBitmap}使用性能还可以
     *
     * @param sentBitmap       需要模糊的图片
     * @param radius           模糊的程度
     * @param canReuseInBitmap 图片是否可以直接使用
     * @return 模糊后的图片
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


    /**
     * 调用此方法需要保证view已完成测量
     * 更多信息见方法{@link #blurViewWithBmp(Bitmap, View, int, int, int, float, float, int)}注释
     */
    public static void blurViewWithBmp(Bitmap bkg, View view, int conner, float scaleFactor, float radius) {
        blurViewWithBmp(bkg, view, conner, 0, 0, scaleFactor, radius, -1);
    }

    public static void blurViewWithBmp(Bitmap bkg, View view, int conner, int width, int height, float scaleFactor, float radius) {
        blurViewWithBmp(bkg, view, conner, width, height, scaleFactor, radius, -1);
    }

    /**
     * 模糊视图的背景
     *
     * @param bkg         需要模糊的背景图
     * @param view        需要模糊的视图
     * @param conner      若大于0，则为圆角
     * @param height      视图高度，若为0，则取view的宽高，但需要保证view已完成测量
     * @param width       视图宽度，若为0，则取view的宽高，但需要保证view已完成测量
     * @param scaleFactor 缩放程度，数值越大越模糊，耗时越短.需要不大于宽高
     * @param radius      模糊程度，数值越大越模糊
     * @param color       模糊混合的蒙层颜色值，-1则不混合。
     */
    public static void blurViewWithBmp(Bitmap bkg, View view, int conner, int width, int height, float scaleFactor, float radius, int color) {
        if (view == null || bkg == null) return;

        if (width == 0) {
            width = view.getMeasuredWidth();
        }
        if (height == 0) {
            height = view.getMeasuredHeight();
        }
        if (width <= 0 || height <= 0) {
            return;
        }
        if (width < scaleFactor || height < scaleFactor) {
            return;
        }
        Bitmap overlay = getBlurBitmap(bkg, width, height, scaleFactor, radius);
        if (conner == 0) {
            view.setBackground(new BitmapDrawable(view.getContext().getResources(), overlay));
            return;
        }
        if (color == -1) {
            blendConnerView(view, conner, overlay, width, height);
        } else {
            blendConnerViewWhitBg(view, conner, overlay, width, height, color);
        }
    }

    /**
     * 获取模糊后的Bitmap
     *
     * @param bkg         模糊前的图片
     * @param width       图片宽度
     * @param height      图片高度
     * @param scaleFactor 缩放程度
     * @param radius      模糊程度
     * @return 模糊后的图片
     */
    public static Bitmap getBlurBitmap(Bitmap bkg, int width, int height, float scaleFactor, float radius) {
        width = width > scaleFactor ? width : DEFAULT_PIXEL;
        height = height > scaleFactor ? height : DEFAULT_PIXEL;
        Bitmap overlay = Bitmap.createBitmap(
                (int) (width / scaleFactor),
                (int) (height / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        return overlay;
    }

    /**
     * 设置背景并裁剪为圆角
     *
     * @param view   需要设置背景的视图
     * @param conner 圆角角度
     * @param bmp    背景图
     * @param width  视图宽度
     * @param height 视图高度
     */
    public static void blendConnerView(View view, int conner, Bitmap bmp, int width, int height) {
        Bitmap newBmp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xffffffff);
        final Rect rect = new Rect(0, 0,
                width, height);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(new RectF(rect), conner, conner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (bmp != null) {
            canvas.drawBitmap(bmp, null, rect, paint);
        }

        view.setBackground(new BitmapDrawable(view.getContext().getResources(), newBmp));
    }

    /**
     * 设置背景并裁剪为圆角，并混合蒙层颜色
     *
     * @param view   需要设置背景的视图
     * @param conner 圆角角度
     * @param bmp    背景图
     * @param width  视图宽度
     * @param height 视图高度
     * @param color  需要混合的颜色
     */
    public static void blendConnerViewWhitBg(View view, int conner, Bitmap bmp, int width, int height, int color) {
        Bitmap newBmp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        final Rect rect = new Rect(0, 0,
                width, height);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(new RectF(rect), conner, conner, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        if (bmp != null) {
            canvas.drawBitmap(bmp, null, rect, paint);
        }

        view.setBackground(new BitmapDrawable(view.getContext().getResources(), newBmp));
    }
}