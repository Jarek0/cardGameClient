package edu.pollub.pl.cardgameclient.common;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleNetworkTask extends AsyncTask<Void, Void, NetworkOperationStrategy> {

    private final Supplier<NetworkOperationStrategy> action;
    private final Consumer<NetworkOperationStrategy> onResult;

    @Override
    protected NetworkOperationStrategy doInBackground(Void... voids) {
        return action.get();
    }

    @Override
    protected void onPostExecute(NetworkOperationStrategy operation) {
        onResult.accept(operation);
    }

}
