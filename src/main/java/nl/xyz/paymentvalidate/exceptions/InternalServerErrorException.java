package nl.xyz.paymentvalidate.exceptions;

public class InternalServerErrorException extends Exception{

    public InternalServerErrorException() {
        super();
    }

    public InternalServerErrorException(String msg){
        super(msg);
    }
}
