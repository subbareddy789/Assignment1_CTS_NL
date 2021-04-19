package nl.xyz.paymentvalidate.service;

import lombok.extern.slf4j.Slf4j;
import nl.xyz.paymentvalidate.common.Constants;
import nl.xyz.paymentvalidate.model.PaymentInitiationRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentValidationService{

    /**
     * Method to validate the client certificate
     * @param x509CertText
     * @return
     */
    public boolean validateCertificate(String x509CertText) {
        try {
            X509Certificate certificate = getCertificateFromText(x509CertText);
            return certificate.getSubjectDN().getName().contains("CN=Sandbox-TPP");
        } catch (CertificateException e) {
            log.error("Certificate validation failed... {}",e.getMessage());
        }
        return false;
    }

    /**
     * Method to validate the client signature
     * @param clientSign
     * @return
     */
    public boolean validateSignature(String certificateText, String clientSign) {
        try {
            X509Certificate certificate = getCertificateFromText(certificateText);
            Signature signature = Signature.getInstance(Constants.SHA256_RSA);
            signature.initVerify(certificate);
            return signature.verify(Base64.getDecoder().decode(clientSign.getBytes()));
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            log.error("Signature validation failed");
        }
        return false;
    }

    /**
     * Throw error if it met following condition 
     * Amount > 0 && Sum(DebtorAccountIBAN) mod Length(DebtorAccountIBAN) == 0
     * @param debtorIBAN
     * @return
     */
    public boolean checkAmountLimitExceeded(String debtorIBAN) {
        var ibanSum = debtorIBAN.chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isDigit)
                .map(Character::getNumericValue)
                .collect(Collectors.summarizingInt(Integer::intValue)).getSum();
        return ibanSum % debtorIBAN.length() == 0;
    }

    /**
     * Method to validate the request body
     * @param paymentInitReq
     * @return
     */
    public boolean validateRequestBody(PaymentInitiationRequest paymentInitReq){
        return paymentInitReq.getDebtorIBAN().matches("[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}")
                && paymentInitReq.getCreditorIBAN().matches("[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}")
                && paymentInitReq.getAmount().matches("-?[0-9]+(\\.[0-9]{1,3})?")
                && paymentInitReq.getCurrency().matches("[A-Z]{3}");
    }


    /**
     * Method to get the certificate from client header variable
     * @param certificateText
     * @return
     * @throws CertificateException
     */
    private X509Certificate getCertificateFromText(String certificateText) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance(Constants.X509);
        ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(certificateText.getBytes()));
        return (X509Certificate) cf.generateCertificate(bytes);
    }
}
