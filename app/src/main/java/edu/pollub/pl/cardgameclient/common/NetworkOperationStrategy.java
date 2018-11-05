package edu.pollub.pl.cardgameclient.common;

public abstract class NetworkOperationStrategy {

    private Boolean successful = false;

    public abstract void execute() throws Exception;

    public Boolean isValid() {
        return true;
    }

    public void onSuccess() {

    }

    public Boolean isSuccessful() {
        return successful;
    }

    public void success() {
        this.successful = true;
    }

}
