package com.huiwan.base.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

public class AlbumUtil {

    public static void updateSystemGallery(Context context, final String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(fileName)));
        context.sendBroadcast(intent);
    }

}
