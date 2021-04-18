package nl.xyz.paymentvalidate.exceptions;

public class InvalidSignatureException extends Exception{

    public InvalidSignatureException() {
        super();
    }

    public InvalidSignatureException(String msg){
        super(msg);
    }
}
