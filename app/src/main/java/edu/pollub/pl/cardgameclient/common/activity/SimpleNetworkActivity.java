package edu.pollub.pl.cardgameclient.common.activity;

import android.content.Intent;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.authorization.login.LoginActivity;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.SimpleNetworkTask;
import edu.pollub.pl.cardgameclient.communication.http.RequestFail;
import edu.pollub.pl.cardgameclient.communication.websocket.QueueHandler;
import edu.pollub.pl.cardgameclient.communication.websocket.StompMessageListener;
import edu.pollub.pl.cardgameclient.communication.websocket.WebSocketClient;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.LOGIN_KEY;
import static edu.pollub.pl.cardgameclient.config.ConfigConst.WS;
import static error.codes.ErrorCodes.UNAUTHORIZED_REQ;


public abstract class SimpleNetworkActivity extends SharedPreferencesActivity {

    @Inject
    private WebSocketClient webSocketClient;

    protected SimpleNetworkTask simpleNetworkTask(NetworkOperationStrategy strategy) {
        return new SimpleNetworkTask(() -> tryNetworkOperation(strategy), this::onResult);
    }

    protected NetworkOperationStrategy tryNetworkOperation(NetworkOperationStrategy strategy) {
        startLoading();
        if(!strategy.isValid()) return strategy;
        try {
            strategy.execute();
            strategy.success();
        }
        catch (RequestFail fail) {
            showToast(fail.getMessageId());
            if(fail.getMessageId().equals(UNAUTHORIZED_REQ)) {
                logout();
            }
        }
        catch (Exception e) {
            showToast(R.string.fatalError);
        }
        return strategy;
    }

    protected void onResult(NetworkOperationStrategy strategy) {
        if(strategy.isSuccessful()) {
            strategy.onSuccess();
        }
        stopLoading();
    }

    protected void connect() {
        webSocketClient.connect(WS, this::logout);
    }

    protected void disconnect() {
        webSocketClient.disconnect();
    }

    protected void subscribe(String queue, StompMessageListener listener) {
        QueueHandler handler = webSocketClient.isSubscribed(queue) ? webSocketClient.getQueueHandler(queue) : webSocketClient.subscribe(queue);
        handler.addListener(listener);
    }

    protected void unSubscribe(String queue) {
        webSocketClient.unSubscribe(queue);
    }

    protected void logout() {
        disconnect();
        showToast(R.string.logoutSuccess);
        removeString(LOGIN_KEY);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

}
