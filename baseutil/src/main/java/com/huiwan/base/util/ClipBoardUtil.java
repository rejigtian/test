package com.huiwan.base.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by three on 15/3/23.
 */
public class ClipBoardUtil {

    public static void copy(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void put(@NonNull Context context, @Nullable String content) {
        if (content == null) content = "";
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText(null, content));
    }

    @Nullable
    public static CharSequence get(@NonNull Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(context);
        }
        return null;
    }
}
