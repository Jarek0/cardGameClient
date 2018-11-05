package edu.pollub.pl.cardgameclient.game.create;

import android.os.Bundle;
import android.widget.Button;

import javax.inject.Inject;

import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import edu.pollub.pl.cardgameclient.game.GameOrganizationService;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_create_game)
public class GameOrganizationActivity extends SimpleNetworkActivity {

    @InjectView(R.id.cancelButton)
    private Button cancelButton;

    @Inject
    private GameOrganizationService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelButton.setOnClickListener(v -> simpleNetworkTask(new CloseGameTask()).execute());
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpleNetworkTask(new OrganizeGameTask()).execute();
    }

    private class OrganizeGameTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            service.organize();
        }

        @Override
        public void onSuccess() {
            showToast(R.string.gameCreated);
        }
    }

    private class CloseGameTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            service.close();
        }

        @Override
        public void onSuccess() {
            comeBack();
        }
    }
}
