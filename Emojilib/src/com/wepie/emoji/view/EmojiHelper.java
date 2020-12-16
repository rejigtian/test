package com.wepie.emoji.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.lang.UCharacter;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.wepie.emoji.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 表情处理类。
 *
 * @author E
 */
public class EmojiHelper {
    public static final int NULL_POSITION = -1;//最后一页空位置
    public static boolean isEmojiParsing = false; //当前是否正在输入（或使用内置emoji键盘的删除按钮）内置emoji
    private static final int MAX_TRANSFORM_NUM = 100;
    private volatile static EmojiHelper instance;

    // 聊天表情全部图片对应的id
    private static final Integer[] emojiImages = {
            R.drawable.emoji_000,
            R.drawable.emoji_001, R.drawable.emoji_002, R.drawable.emoji_003, R.drawable.emoji_004,
            R.drawable.emoji_005, R.drawable.emoji_006, R.drawable.emoji_007,

            R.drawable.emoji_008, R.drawable.emoji_009, R.drawable.emoji_010, R.drawable.emoji_011,
            R.drawable.emoji_012, R.drawable.emoji_013, R.drawable.emoji_014,

            R.drawable.emoji_015, R.drawable.emoji_016, R.drawable.emoji_017, R.drawable.emoji_018,
            R.drawable.emoji_019, R.drawable.emoji_020, R.drawable.emoji_021,

            R.drawable.emoji_022, R.drawable.emoji_023, R.drawable.emoji_024, R.drawable.emoji_025,
            R.drawable.emoji_026, R.drawable.emoji_027, R.drawable.emoji_028,

            R.drawable.emoji_029, R.drawable.emoji_030, R.drawable.emoji_031, R.drawable.emoji_032,
            R.drawable.emoji_033, R.drawable.emoji_034, R.drawable.emoji_035,

            R.drawable.emoji_036, R.drawable.emoji_037, R.drawable.emoji_038, R.drawable.emoji_039,
            R.drawable.emoji_040, R.drawable.emoji_041, R.drawable.emoji_042,

            R.drawable.emoji_043, R.drawable.emoji_044, R.drawable.emoji_045, R.drawable.emoji_046,
            R.drawable.emoji_047, R.drawable.emoji_048, R.drawable.emoji_049,

            R.drawable.emoji_050, R.drawable.emoji_051, R.drawable.emoji_052, R.drawable.emoji_053,
            R.drawable.emoji_054, R.drawable.emoji_055, R.drawable.emoji_056,

            R.drawable.emoji_057, R.drawable.emoji_058, R.drawable.emoji_059, R.drawable.emoji_060,
            R.drawable.emoji_061, R.drawable.emoji_062, R.drawable.emoji_063,

            R.drawable.emoji_064, R.drawable.emoji_065, R.drawable.emoji_066, R.drawable.emoji_067,
            R.drawable.emoji_068, R.drawable.emoji_069, R.drawable.emoji_070,

            R.drawable.emoji_071, R.drawable.emoji_072, R.drawable.emoji_073, R.drawable.emoji_074,
            R.drawable.emoji_075, R.drawable.emoji_076, R.drawable.emoji_077,

            R.drawable.emoji_078, R.drawable.emoji_079, R.drawable.emoji_080, R.drawable.emoji_081,
            R.drawable.emoji_082, R.drawable.emoji_083, R.drawable.emoji_084, R.drawable.emoji_085, R.drawable.emoji_086,
            R.drawable.emoji_087, R.drawable.emoji_088, R.drawable.emoji_089, R.drawable.emoji_090,
            R.drawable.emoji_091, R.drawable.emoji_092, R.drawable.emoji_093, R.drawable.emoji_094,
            R.drawable.emoji_095, R.drawable.emoji_096, R.drawable.emoji_097, R.drawable.emoji_098,
            R.drawable.emoji_099, R.drawable.emoji_100, R.drawable.emoji_101, R.drawable.emoji_102,
            R.drawable.emoji_103, R.drawable.emoji_104, R.drawable.emoji_105, R.drawable.emoji_106,
            R.drawable.emoji_107, R.drawable.emoji_108, R.drawable.emoji_109, R.drawable.emoji_110,
            R.drawable.emoji_111, R.drawable.emoji_112, R.drawable.emoji_113, R.drawable.emoji_114, R.drawable.emoji_115
    };

//	private static int[] emojiUnicode = new int[]{
//		0x1F600, 0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606,
//		0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D,
//		0x1F60E, 0x1F60F, 0x1F610, 0x1F611, 0x1F612, 0x1F613, 0x1F614,
//		0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F619, 0x1F61A, 0x1F61B,
//		0x1F61C, 0x1F61D, 0x1F61E, 0x1F61F, 0x1F620, 0x1F621, 0x1F622,
//		0x1F623, 0x1F624, 0x1F625, 0x1F626, 0x1F627, 0x1F628, 0x1F629,
//		0x1F62A, 0x1F62B, 0x1F62C, 0x1F62D, 0x1F62E, 0x1F62F, 0x1F630,
//		0x1F631, 0x1F632, 0x1F633, 0x1F634, 0x1F635, 0x1F636, 0x1F637,
//		0x1F638, 0x1F639, 0x1F63A, 0x1F63B, 0x1F63C, 0x1F63D, 0x1F63E,
//		0x1F63F, 0x1F640, 0x1F645, 0x1F646, 0x1F647, 0x1F648, 0x1F649,
//		0x1F64A, 0x1F64B, 0x1F64C, 0x1F64D, 0x1F64E, 0x1F64F, 0x1F44B,
//			0x1F44D, 0x270c, 0x1F440, 0x1F644, 0x1F92A, 0x1F92B, 0x1F92C,
//			0x1F92D, 0x1F92E, 0x1F92F, 0x1F641, 0x1F642, 0x1F643, 0x1F910,
//			0x1F911, 0x1F912, 0x1F913, 0x1F914, 0x1F915, 0x1F922, 0x1F923,
//			0x1F924, 0x1F925, 0x1F917
//	};// 81:心碎： 0x1F494

    private static int[] emojiUnicode = new int[]{
            0x1F3B2,
            0x1F600, 0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605, 0x1F606,
            0x1F607, 0x1F608, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D,
            0x1F60E, 0x1F60F, 0x1F610, 0x1F611, 0x1F612, 0x1F613, 0x1F614,
            0x1F615, 0x1F616, 0x1F617, 0x1F618, 0x1F619, 0x1F61A, 0x1F61B,
            0x1F61C, 0x1F61D, 0x1F61E, 0x1F61F, 0x1F620, 0x1F621, 0x1F622,
            0x1F623, 0x1F624, 0x1F625, 0x1F626, 0x1F627, 0x1F628, 0x1F629,
            0x1F62A, 0x1F62B, 0x1F62C, 0x1F62D, 0x1F62E, 0x1F62F, 0x1F630,
            0x1F631, 0x1F632, 0x1F633, 0x1F634, 0x1F635, 0x1F636, 0x1F637,
            0x1F641, 0x1F642, 0x1F643, 0x1F644, 0x1F910, 0x1F911, 0x1F912,
            0x1F913, 0x1F914, 0x1F915, 0x1F917, 0x1F920, 0x1F921, 0x1F922,
            0x1F923, 0x1F924, 0x1F925, 0x1F927, 0x1F928, 0x1F929, 0x1F92A,
            0x1F92B, 0x1F92C, 0x1F92D, 0x1F92E, 0x1F92F, 0x1F440, 0x1F64A,
            0x1F648, 0x1F649, 0x1F638, 0x1F639, 0x1F63A, 0x1F63B, 0x1F63C,
            0x1F63D, 0x1F63E, 0x1F63F, 0x1F640, 0x1F64B, 0x1F645, 0x1F646,
            0x1F64D, 0x1F647, 0x1F926, 0x1F64F, 0x1F64C, 0x1F918, 0x1F919,
            0x1F91A, 0x1F91B, 0x1F91C, 0x1F91D, 0x1F91E, 0x1F91F, 0x1F44D,
            0x270c, 0x2764, 0x1F494
    };// 81:心碎： 0x1F494

    private static String[] emojiCodeStr = new String[116];

    private static HashMap<String, Integer> emap = new HashMap<String, Integer>();

    private static HashMap<String, Integer> singleCharEmap = new HashMap<String, Integer>();

    private static HashMap<String, SpannableStringBuilder> strLocalCache = new HashMap<>();

    private static List<String> cachedKey = new ArrayList<>();

    static {
        int len = emojiImages.length;
        for (int i = 0; i < len; i++) {
            String emojiCode = hex2UnicodeString(emojiUnicode[i]);
            emap.put(emojiCode, emojiImages[i]);
            emojiCodeStr[i] = emojiCode;
        }
    }

    private static String hex2UnicodeString(int x) {
        if (x > 0xffff) {
            int uf = ((((x - 0x10000) >> 10) | 0xD800));
            int ul = (((x - 0x10000) & 0x3FF) | 0xDC00);
            String result = (char) Integer.parseInt(Integer.toHexString(uf), 16)
                    + Character.toString((char) Integer.parseInt(Integer.toHexString(ul), 16));
            return result;
        } else {
            String uString = Character.toString((char) Integer.parseInt(Integer.toHexString(x), 16));
            singleCharEmap.put(uString, x);
            return uString;
        }
    }


    public static EmojiHelper getInstance() {
        if (instance == null) {
            synchronized (EmojiHelper.class) {
                if (instance == null) {
                    instance = new EmojiHelper();
                }
            }
        }
        return instance;
    }


    public static void parseEmojis(Context context, TextView textView, String text, int size) {
        parseEmojis(context, textView, text, size, true);
    }

    public static void parseEmojis(Context context, TextView textView, String text, int size, boolean clearText) {
        EmojiHelper.isEmojiParsing = true;
        if (strLocalCache.containsKey(text)){
            textView.setText(strLocalCache.get(text));
            EmojiHelper.isEmojiParsing = false;
            return;
        }
        if (clearText) textView.setText("");
        SpannableStringBuilder ss = new SpannableStringBuilder();
        parseEmoji2Ssb(context,ss,text,MAX_TRANSFORM_NUM,size);
        textView.setText(ss);
        setLocalCache(text, ss);
        EmojiHelper.isEmojiParsing = false;
    }

    private static void setLocalCache(String key, SpannableStringBuilder value) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(value);
        if (strLocalCache.size()<100){
            strLocalCache.put(key,spannableStringBuilder);
            cachedKey.add(key);
        }else {
            cachedKey.remove(0);
            strLocalCache.put(key,value);
            cachedKey.add(key);
        }
    }

    public static void parseEmoji2Ssb(Context context, SpannableStringBuilder ssb, String content, int maxParseNum, int size) {
        EmojiHelper.isEmojiParsing = true;
        if (strLocalCache.containsKey(content)){
            ssb.append(strLocalCache.get(content));
            EmojiHelper.isEmojiParsing = false;
            return;
        }
        int len = content.length();
        int i = 0;
        int parseNum = 0;
        long startTime = System.currentTimeMillis();
        boolean parse = true;
        while (i < len) {
            char c = content.charAt(i);
            String key = String.valueOf(c);
            if (parse && parseNum < maxParseNum && singleCharEmap.containsKey(key)) {
                //单字符表情
                SpannableString ss = new SpannableString(key);
                Drawable drawSmiley = context.getResources().getDrawable(emap.get(key));
                drawSmiley.setBounds(0, 0, dip2px(context, size), dip2px(context, size));
                ImageSpan span = new VerticalImageSpan(drawSmiley);
                ss.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(ss);
                drawSmiley.setCallback(null);
                parseNum++;
                i++;
            } else if (parse && parseNum < maxParseNum && i + 1 < len) {
                String newKay = key + content.charAt(i + 1);
                if (emap.containsKey(newKay)) {
                    //双字符表情
                    SpannableString ss = new SpannableString(newKay);
                    Drawable drawSmiley = context.getResources().getDrawable(emap.get(newKay));
                    drawSmiley.setBounds(0, 0, dip2px(context, size), dip2px(context, size));
                    ImageSpan span = new VerticalImageSpan(drawSmiley);
                    ss.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(ss);
                    drawSmiley.setCallback(null);
                    i += 2;
                    parseNum++;
                } else {
                    ssb.append(key);
                    i++;
                }
            } else {
                if (parse) {
                    ssb.append(key);
                    i++;
                } else {
                    ssb.append(content.substring(i));
                    break;
                }
            }
            if (System.currentTimeMillis() - startTime > 200) {
                parse = false;
            }
        }
        setLocalCache(content, ssb);

        EmojiHelper.isEmojiParsing = false;

    }

    /**
     * 仅解析新增的表情字符串，提高解析效率
     * @param context 上下文
     * @param editText 需要输入的文本框
     * @param size emoji大小
     * @param emojiSrcIndex emoji的角标
     * @param selIndex 输入框的光标位置
     */
    public static void parseEmojisFast(Context context, EditText editText, int size, int emojiSrcIndex, int selIndex) {
        if (EmojiHelper.isEmojiParsing) return;
        EmojiHelper.isEmojiParsing = true;
        String emojiStr = emojiCodeStr[emojiSrcIndex];
        String key = String.valueOf(emojiStr.charAt(0));
        SpannableString ss = new SpannableString(emojiStr);
        if (singleCharEmap.containsKey(key)){
            //单字符表情
            Drawable drawSmiley = context.getResources().getDrawable(emap.get(key));
            drawSmiley.setBounds(0, 0,
                    editText.getLayoutParams().height + dip2px(context, size),
                    editText.getLayoutParams().height + dip2px(context, size));
            ImageSpan span = new VerticalImageSpan(drawSmiley);
            ss.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.getText().insert(selIndex,ss);
        }else {
            Drawable drawSmiley = context.getResources().getDrawable(emap.get(emojiStr));
            drawSmiley.setBounds(0, 0,
                    editText.getLayoutParams().height + dip2px(context, size),
                    editText.getLayoutParams().height + dip2px(context, size));
            ImageSpan span = new VerticalImageSpan(drawSmiley);
            ss.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.getText().insert(selIndex,ss);
        }
        EmojiHelper.isEmojiParsing = false;
    }

    /**
     * 外部接口
     * 解析表情,将包含表情的字符串显示到TextView上
     */
    public static void parseEmojis(Context context, TextView textView, String text) {
        parseEmojis(context, textView, text, 18);
    }

    public static String parseEmojisToString(String text) {
        StringBuilder ret = new StringBuilder();
        String pasrse = text == null ? "" : text;
        int len = pasrse.length();
        int i = 0;
        while (i < len) {
            char c = pasrse.charAt(i);
            String key = String.valueOf(c);
            if (singleCharEmap.containsKey(key)) {
                //单字符表情
                ret.append("[表情]");
                i++;
            } else if (i + 1 < len) {
                String newKay = key + pasrse.charAt(i + 1);
                if (emap.containsKey(newKay)) {
                    //双字符表情
                    ret.append("[表情]");
                    i += 2;
                } else {
                    ret.append(key);
                    i++;
                }
            } else {
                ret.append(key);
                i++;
            }
        }
        return ret.toString();
    }

    public void clear(){
        strLocalCache.clear();
        cachedKey.clear();
    }


    /**
     * 删除表情
     *
     * @return 删除后剩余的内容
     */
    protected void deleteEmoji(Context context, EditText edit, int index) {
        String content = edit.getText().toString();
        if (index == 0) {
            return;
        }

        Editable editable = edit.getText();
        int endIndex = edit.getSelectionStart();
        String sufixChar = String.valueOf(content.charAt(index - 1));

        if (singleCharEmap.containsKey(sufixChar)) {
            //是单字符表情
            editable.delete(endIndex - 1, endIndex);
        } else if (index - 2 >= 0) {
            String emojiStr = String.valueOf(content.charAt(index - 2)) + sufixChar;

            if (emap.containsKey(emojiStr)) {
                editable.delete(endIndex - 2, endIndex);
            } else {
                //删除文字
                editable.delete(endIndex - 1, endIndex);
            }
        } else if (index - 1 >= 0) {
            //删除文字
            editable.delete(endIndex - 1, endIndex);
        }
    }

    /**
     * 检测指定位置资源是否存在
     */
    protected boolean checkResource(int position) {
        if (position < emojiImages.length) {
            return !(emojiImages[position] == NULL_POSITION);
        } else {
            return false;
        }
    }

    /**
     * 获取指定页的emoji资源id
     *
     * @return
     */
    protected Integer[] getEmojiResourceId(boolean needDice) {
        if (needDice) {
            return emojiImages;
        }else {
            return Arrays.copyOfRange(emojiImages,1,emojiImages.length);
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
