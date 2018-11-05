package edu.pollub.pl.cardgameclient.authorization.changeLogin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import command.ChangeLoginCommand;
import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static edu.pollub.pl.cardgameclient.config.ConfigConst.LOGIN_KEY;

@ContentView(R.layout.activity_change_nick)
public class ChangeNickActivity extends SimpleNetworkActivity {

    @InjectView(R.id.newPasswordEditText)
    private EditText loginEditText;

    @InjectView(R.id.submitButton)
    private Button cancelButton;

    @InjectView(R.id.cancelButton)
    private Button submitButton;

    @Inject
    private ChangeNickService changeNickService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelButton.setOnClickListener((v) -> comeBack());
        submitButton.setOnClickListener((v) -> simpleNetworkTask(new ChangeNickTask()).execute());
    }

    protected ChangeLoginCommand getCommand() {
        return new ChangeLoginCommand(loginEditText.getText().toString());
    }

    private void clearInputs() {
        loginEditText.setText("");
    }

    private class ChangeNickTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            ChangeLoginCommand command = getCommand();
            changeNickService.changeNick(command);
            saveString(LOGIN_KEY, command.getLogin());
        }


        @Override
        public Boolean isValid() {
            if(loginEditText.getText().toString().isEmpty()) {
                showToast(R.string.loginRequired);
                return false;
            }
            return true;
        }

        @Override
        public void onSuccess() {
            showToast(R.string.successfulNickChange);
            clearInputs();
            comeBack();
        }
    }
}
