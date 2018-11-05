package edu.pollub.pl.cardgameclient.common.activity;

import android.view.View;
import android.widget.TextView;

import edu.pollub.pl.cardgameclient.R;
import roboguice.inject.InjectView;

public abstract class LoadingActivity extends ToastActivity {

    @InjectView(R.id.loadingView)
    private View loadingView;

    @InjectView(R.id.loadingText)
    private TextView loadingText;

    public void startLoading() {
        runOnUiThread(() -> loadingView.setVisibility(View.VISIBLE));
    }

    public void startLoading(String loadingText) {
        runOnUiThread(
                () -> {
                    this.loadingText.setText(loadingText);
                    loadingView.setVisibility(View.VISIBLE);
                }
        );
    }

    public void stopLoading() {
        runOnUiThread(() -> loadingView.setVisibility(View.GONE));
    }

}
