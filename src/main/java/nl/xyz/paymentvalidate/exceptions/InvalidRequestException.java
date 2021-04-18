package nl.xyz.paymentvalidate.exceptions;

public class InvalidRequestException extends Exception{

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String msg){
        super(msg);
    }
}
