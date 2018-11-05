package edu.pollub.pl.cardgameclient.common.activity;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.SimpleNetworkTask;
import edu.pollub.pl.cardgameclient.communication.http.RequestFail;


public abstract class SimpleNetworkActivity extends SharedPreferencesActivity {

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

}
