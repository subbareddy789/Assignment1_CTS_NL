package nl.xyz.paymentvalidate.exceptions;

import lombok.AllArgsConstructor;
import nl.xyz.paymentvalidate.common.Constants;
import nl.xyz.paymentvalidate.model.ErrorReasonCode;
import nl.xyz.paymentvalidate.model.PaymentRejectedResponse;
import nl.xyz.paymentvalidate.model.TransactionStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
@AllArgsConstructor
public class PaymentRequestValidationException extends ResponseEntityExceptionHandler {


    private HttpServletRequest requestHeaders;

    @ExceptionHandler(UnknownCertificateException.class)
    public  ResponseEntity<PaymentRejectedResponse> handleInvalidCertificateException(UnknownCertificateException ex){
        var response = new PaymentRejectedResponse().reasonCode(ErrorReasonCode.UNKNOWN_CERTIFICATE)
                .status(TransactionStatus.REJECTED).reason(ex.getMessage());
        return new ResponseEntity<>(response,getResponseHeaders(),HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(LimitExceedException.class)
    public ResponseEntity<PaymentRejectedResponse> handleLimitExceedException(LimitExceedException ex){
        var response = new  PaymentRejectedResponse().reasonCode(ErrorReasonCode.LIMIT_EXCEEDED)
                .status(TransactionStatus.REJECTED).reason(ex.getMessage());
        return new ResponseEntity<>(response,getResponseHeaders(),HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public  ResponseEntity<PaymentRejectedResponse> handleLimitExceedException(InvalidRequestException ex){
        var response = new PaymentRejectedResponse().reasonCode(ErrorReasonCode.INVALID_REQUEST)
                .status(TransactionStatus.REJECTED).reason(ex.getMessage());
        return new ResponseEntity<>(response,getResponseHeaders(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<PaymentRejectedResponse> handleLimitExceedException(Exception ex){
        var response = new PaymentRejectedResponse().reasonCode(ErrorReasonCode.GENERAL_ERROR)
                .status(TransactionStatus.REJECTED).reason(ex.getMessage());
        return new ResponseEntity<>(response,getResponseHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public  ResponseEntity<PaymentRejectedResponse> handleInvalidSignatureException(InvalidSignatureException ex){
        var response = new PaymentRejectedResponse().reasonCode(ErrorReasonCode.INVALID_SIGNATURE)
                .status(TransactionStatus.REJECTED).reason(ex.getMessage());
        return new ResponseEntity<>(response,getResponseHeaders(),HttpStatus.BAD_REQUEST);
    }

    public HttpHeaders getResponseHeaders(){
        var headers = new HttpHeaders();
        headers.set(Constants.X_REQUEST_ID,
                requestHeaders.getHeader(Constants.X_REQUEST_ID));
        headers.set(Constants.SIGNATURE_CERTIFICATE,
                requestHeaders.getHeader(Constants.SIGNATURE_CERTIFICATE));
        headers.set(Constants.SIGNATURE,
                requestHeaders.getHeader(Constants.SIGNATURE));
        return headers;
    }
}
