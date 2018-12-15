package edu.pollub.pl.cardgameclient.authorization.changePassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import command.ChangePasswordCommand;
import edu.pollub.pl.cardgameclient.R;
import edu.pollub.pl.cardgameclient.common.NetworkOperationStrategy;
import edu.pollub.pl.cardgameclient.common.activity.SimpleNetworkActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_change_password)
public class ChangePasswordActivity extends SimpleNetworkActivity {

    @InjectView(R.id.loginEditText)
    private EditText newPasswordEditText;

    @InjectView(R.id.confirmNewPasswordEditText)
    private EditText confirmNewPasswordEditText;

    @InjectView(R.id.oldPasswordEditText)
    private EditText oldPasswordEditText;

    @InjectView(R.id.cancelButton)
    private Button cancelButton;

    @InjectView(R.id.submitButton)
    private Button submitButton;

    @Inject
    private ChangePasswordService changePasswordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelButton.setOnClickListener((v) -> comeBack());
        submitButton.setOnClickListener((v) -> simpleNetworkTask(new ChangePasswordTask()).execute());
    }

    protected ChangePasswordCommand getCommand() {
        return new ChangePasswordCommand(oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString(), confirmNewPasswordEditText.getText().toString());
    }

    private void clearInputs() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
        confirmNewPasswordEditText.setText("");
    }

    private class ChangePasswordTask extends NetworkOperationStrategy {

        @Override
        public void execute() throws Exception {
            changePasswordService.changePassword(getCommand());
        }

        @Override
        public void onSuccess() {
            showToast(R.string.successfulPasswordChange);
            clearInputs();
            comeBack();
        }

        @Override
        public Boolean isValid() {
            String newPassword = newPasswordEditText.getText().toString();
            String newPasswordConfirmation = confirmNewPasswordEditText.getText().toString();
            String oldPassword = oldPasswordEditText.getText().toString();

            if(newPassword.isEmpty()) {
                showToast(R.string.newPasswordRequired);
                return false;
            }
            if(newPasswordConfirmation.isEmpty()) {
                showToast(R.string.newPasswordConfirmationRequired);
                return false;
            }

            if(oldPassword.isEmpty()) {
                showToast(R.string.oldPasswordRequired);
                return false;
            }
            if(!newPasswordConfirmation.equals(newPassword)) {
                showToast(R.string.passwordConfirmationFail);
                return false;
            }
            return true;
        }
    }
}
