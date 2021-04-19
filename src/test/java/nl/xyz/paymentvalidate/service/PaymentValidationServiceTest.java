package nl.xyz.paymentvalidate.service;

import lombok.SneakyThrows;
import nl.xyz.paymentvalidate.model.PaymentInitiationRequest;
import nl.xyz.paymentvalidate.service.PaymentValidationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class PaymentValidationServiceTest {

    static PaymentValidationService paymentValidationService;

    @BeforeAll
    static void init(){
        paymentValidationService = new PaymentValidationService();
    }

    @Test
    @SneakyThrows
    public void validateCertificate(){

        String certificateText = FileUtils.readFileToString(ResourceUtils
                .getFile("classpath:certificate.txt"), UTF_8);

        boolean result = paymentValidationService.validateCertificate(
                Base64.getEncoder().encodeToString(certificateText.getBytes()));
        assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    @SneakyThrows
    public void validateAmountExceed(){
        boolean result = paymentValidationService.checkAmountLimitExceeded("NL02RABO7134384551");
        assertThat(result).isEqualTo(Boolean.FALSE);
    }

    @Test
    @SneakyThrows
    public void validatePaymentRequest(){
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL02RABO7134384551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00");

        boolean result = paymentValidationService.validateRequestBody(requestBody);
        assertThat(result).isEqualTo(Boolean.TRUE);
    }
}
