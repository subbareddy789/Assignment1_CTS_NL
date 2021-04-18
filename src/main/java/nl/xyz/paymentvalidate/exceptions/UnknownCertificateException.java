package nl.xyz.paymentvalidate.exceptions;

public class UnknownCertificateException extends Exception{

    public UnknownCertificateException() {
        super();
    }

    public UnknownCertificateException(String msg){
        super(msg);
    }
}
