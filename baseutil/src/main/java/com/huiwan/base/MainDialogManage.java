package com.huiwan.base;

import java.util.ArrayList;

public class MainDialogManage {

    private volatile static MainDialogManage instance;
    private final ArrayList<ShowDialogInfo> dialogList = new ArrayList<>();
    private boolean refuseShowDialog = false;
    private boolean isDialogShow = false;
    private final Callback callback = new Callback() {
        @Override
        public void showNext() {
            isDialogShow = false;
            showDialog();
        }

        @Override
        public void clearAll() {
            isDialogShow = false;
            dialogList.clear();
        }
    };

    public final static int REQUEST_PERMISSION = 0;//请求授权第一个弹
    public final static int PRIVACY_TYPE = 1;
    public final static int AUTH_TYPE = 2;
    public final static int GAME_TYPE = 3;
    public final static int XROOM_TYPE = 4;
    public final static int INVITE_TYPE = 5;
    public final static int TEENAGER_TYPE = 6;
    public final static int EXCLUSIVE_TYPE = 7;//专属道具弹窗
    public final static int SIGN_IN_TYPE = 8;
    public final static int PRAISE_IN_TYPE = 9;//好评引导弹窗
    public final static int ACTIVITY_TYPE = 10;

    //根据type的大小决定弹窗弹出的优先程度，type越小的下一个弹窗越先被弹出。
    public void addDialogTag(ShowDialogInfo showDialogInfo) {
        if (refuseShowDialog && showDialogInfo.tag != INVITE_TYPE) {
            return;
        }
        for (int i = 0; i < dialogList.size(); i++) {
            if (dialogList.get(i).tag > showDialogInfo.tag) {
                dialogList.add(i, showDialogInfo);
                showDialog();
                return;
            }
        }
        dialogList.add(showDialogInfo);
        showDialog();
    }

    private MainDialogManage() {
    }

    public static MainDialogManage getInstance() {
        if (instance == null) {
            synchronized (MainDialogManage.class) {
                if (instance == null) {
                    instance = new MainDialogManage();
                }
            }
        }
        return instance;
    }

    public void refuseShowDialog() {
        refuseShowDialog = true;
    }

    private void showDialog() {
        if (dialogList.isEmpty()) return;
        if (isDialogShow) return;
        ShowDialogInfo dialogInfo = dialogList.get(0);
        dialogList.remove(0);
        isDialogShow = true;
        dialogInfo.show();
    }

    public Callback getCallback() {
        return callback;
    }

    public void destroy() {
        dialogList.clear();
        isDialogShow = false;
        refuseShowDialog = false;
    }
    
    public static abstract class ShowDialogInfo {
        
        private final int tag;
        public ShowDialogInfo(int tag) {
            this.tag = tag;
        }
        
        public abstract void show();
    }

    public interface Callback {
        void showNext();

        void clearAll();
    }
}
