package com.and.ibrahim.teleprompter.mvp.view;

public interface OnCameraPermissions {
    void onSetPermissions(boolean cameraPermission,boolean audioPermission,boolean storagePermission,boolean showPermission);
    void onShowMessage(boolean showMessage);

}
