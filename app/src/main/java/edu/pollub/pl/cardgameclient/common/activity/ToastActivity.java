package edu.pollub.pl.cardgameclient.common.activity;

import edu.pollub.pl.cardgameclient.common.ToastViewer;

public abstract class ToastActivity extends NavigationActivity {

    private final ToastViewer toastViewer = new ToastViewer(this);

    public void showToast(String messageId) {
        runOnUiThread(() ->toastViewer.showToast(messageId));
    }

    public void showToast(int messageId) {
        runOnUiThread(() ->toastViewer.showToast(messageId));
    }

    public String getMessage(int messageId) {
        return toastViewer.getText(messageId);
    }
}
