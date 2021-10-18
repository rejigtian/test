package com.example.test.dialog;

import android.content.Context;

import com.example.test.R;
import com.rejig.base.ChooseLocationDialog;

public class DialogUtil {
    public static void showLocDialog(Context context){
        BaseFullScreenDialog dialog = new BaseFullScreenDialog(context, R.style.dialog_style_custom);
        ChooseLocationDialog chooseLocationDialog = new ChooseLocationDialog(context);
        chooseLocationDialog.setCallback(dialog::dismiss);
        dialog.setContentView(chooseLocationDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.initBottomDialog();
        dialog.show();
    }
}
