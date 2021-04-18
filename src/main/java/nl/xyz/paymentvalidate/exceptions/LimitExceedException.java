package nl.xyz.paymentvalidate.exceptions;

public class LimitExceedException extends Exception{

    public LimitExceedException() {
        super();
    }

    public LimitExceedException(String msg){
        super(msg);
    }
}

