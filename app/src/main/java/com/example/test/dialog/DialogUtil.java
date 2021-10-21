package com.example.test.dialog;

import android.content.Context;
import android.widget.Toast;

import com.example.test.R;
import com.rejig.base.ChooseLocationDialog;
import com.rejig.base.HWPosition;

public class DialogUtil {
    public static void showLocDialog(Context context){
        BaseFullScreenDialog dialog = new BaseFullScreenDialog(context, R.style.dialog_style_custom);
        ChooseLocationDialog chooseLocationDialog = new ChooseLocationDialog(context);
        chooseLocationDialog.setSelectPosition(new HWPosition(HWPosition.MY_ID));
        chooseLocationDialog.setCallback(new ChooseLocationDialog.Callback() {
            @Override
            public void onCancel() {
                Toast.makeText(context, "取消选择", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            @Override
            public void onSelectPoi(HWPosition position) {
                Toast.makeText(context, "选择了"+ position.toString(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.setContentView(chooseLocationDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.initFullScreenBottomDialog();
        dialog.show();
    }
}
