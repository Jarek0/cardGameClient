package edu.pollub.pl.cardgameclient.common;

import android.content.Context;
import android.widget.Toast;

public class ToastViewer {

    private final Context context;
    private final MessageSource messageSource;

    public ToastViewer(Context context) {
        this.context = context;
        messageSource = new MessageSource(context);
    }

    public void showToast(String messageId) {
        Toast.makeText(context, messageSource.getMessage(messageId), Toast.LENGTH_LONG).show();
    }

    public void showToast(int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
    }

    public String getText(int messageId) {
        return messageSource.getMessage(messageId);
    }
}
