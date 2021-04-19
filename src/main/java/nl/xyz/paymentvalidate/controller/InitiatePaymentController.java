package nl.xyz.paymentvalidate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.xyz.paymentvalidate.common.Constants;
import nl.xyz.paymentvalidate.exceptions.InvalidRequestException;
import nl.xyz.paymentvalidate.exceptions.InvalidSignatureException;
import nl.xyz.paymentvalidate.exceptions.LimitExceedException;
import nl.xyz.paymentvalidate.exceptions.UnknownCertificateException;
import nl.xyz.paymentvalidate.model.PaymentAcceptedResponse;
import nl.xyz.paymentvalidate.model.PaymentInitiationRequest;
import nl.xyz.paymentvalidate.model.TransactionStatus;
import nl.xyz.paymentvalidate.service.PaymentValidationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController()
@Slf4j
public class InitiatePaymentController {


    private final PaymentValidationService paymentValidationService;
    private X509Certificate applicationCertificate;

    public InitiatePaymentController(PaymentValidationService paymentValidationService,X509Certificate applicationCertificate) {
        this.paymentValidationService = paymentValidationService;
        this.applicationCertificate = applicationCertificate;
    }

    @PostMapping(path = "/v1.0.0/initiate-payment",
            consumes = "application/json", produces = "application/json")
    @SneakyThrows
    public ResponseEntity<Object> initiatePayment(@RequestHeader Map<String,String> headers,
                                                  @Valid @RequestBody PaymentInitiationRequest paymentInitReq) {

        if(!paymentValidationService.validateCertificate(headers.get(Constants.SIGNATURE_CERTIFICATE))){
            throw new UnknownCertificateException("Unknown certificate received");
        }
        BigDecimal amount = new BigDecimal(paymentInitReq.getAmount());
        if(amount.compareTo(BigDecimal.ZERO) > 0
                && paymentValidationService.checkAmountLimitExceeded(paymentInitReq.getDebtorIBAN())){
            log.error("Limit exceeded exception");
            throw new LimitExceedException("Amount Limit Exceeded");
        }
        if(!paymentValidationService.validateRequestBody(paymentInitReq)){
            log.error("Invalid request Exception");
            throw new InvalidRequestException("Invalid Request");
        }

        if(!paymentValidationService.validateSignature(headers.get(Constants.SIGNATURE_CERTIFICATE),headers.get(Constants.SIGNATURE))){
            log.error("Invalid Signature");
            throw new InvalidSignatureException("Invalid signature");
        }

        var response = new PaymentAcceptedResponse()
                .status(TransactionStatus.ACCEPTED)
                .paymentId(UUID.randomUUID());

        var responseHeaders = new HttpHeaders();
        responseHeaders.set(Constants.X_REQUEST_ID,
                headers.get(Constants.X_REQUEST_ID));
        responseHeaders.set(Constants.SIGNATURE_CERTIFICATE,
                headers.get(Constants.SIGNATURE_CERTIFICATE));
        responseHeaders.set(Constants.SIGNATURE,
                new String(Base64.getEncoder().encode(applicationCertificate.getSignature())));

        return new ResponseEntity<>(response,responseHeaders, HttpStatus.CREATED);
    }
}
