package edu.pollub.pl.cardgameclient.common;

import android.content.Context;
import android.content.res.Resources;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageSource {

    private final Context context;

    public String getMessage(String messageId) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(messageId, "string", context.getPackageName()));
    }

    public String getMessage(int messageId) {
        Resources res = context.getResources();
        return res.getString(messageId);
    }
}
