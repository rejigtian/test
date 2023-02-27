package com.woyou.hotfix;

import android.content.Context;
import android.util.Log;

import com.huiwan.signchecker.SignChecker;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.example.test.SampleApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
        try {
            Context context = SignChecker.getContext();
            Log.e("SignChecker", "doNormalSignCheck: " + SignChecker.doNormalSignCheck(context));
        } catch (Exception e) {
            Log.e("SignChecker", "doNormalSignCheck: " + e.getMessage());
        }
    }
}