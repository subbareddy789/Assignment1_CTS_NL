package nl.xyz.paymentvalidate.controller;

import lombok.SneakyThrows;
import nl.xyz.paymentvalidate.common.Constants;
import nl.xyz.paymentvalidate.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InitiatePaymentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final static String url = "/v1.0.0/initiate-payment";

    @Test
    @SneakyThrows
    @DisplayName("Validate Payment Happy Flow")
    public void validatePaymentHappyFlow(){
        //given
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL02RABO7134384551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00");

        HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(requestBody,getHttpHeaders());
        //when
        var response = restTemplate.postForEntity(url,request, PaymentAcceptedResponse.class);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentAcceptedResponse::getStatus)
                .isEqualTo(TransactionStatus.ACCEPTED);
    }

    @Test
    @SneakyThrows
    @DisplayName("Invalid Certificate Flow")
    public void invalidCertificate(){
        //given
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL02RABO7134384551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00");
        String invalidBase64Certificate = "ASDFLS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlEd2pDQ0Fxb0NDUUR4VmJDaklLeW5RakFOQmdrcWhraUc5dzBCQVFzRkFEQ0JvakVMTUFrR0ExVUVCaE1DDQpUa3d4RURBT0JnTlZCQWdNQjFWMGNtVmphSFF4RURBT0JnTlZCQWNNQjFWMGNtVmphSFF4RVRBUEJnTlZCQW9NDQpDRkpoWW05aVlXNXJNUk13RVFZRFZRUUxEQXBCYzNObGMzTnRaVzUwTVNJd0lBWURWUVFEREJsVFlXNWtZbTk0DQpMVlJRVURwbGVHTmxiR3hsYm5RZ1ZGQlFNU013SVFZSktvWklodmNOQVFrQkZoUnVieTF5WlhCc2VVQnlZV0p2DQpZbUZ1YXk1dWJEQWVGdzB5TURBeE16QXhNekl5TkRsYUZ3MHlNVEF4TWpreE16SXlORGxhTUlHaU1Rc3dDUVlEDQpWUVFHRXdKT1RERVFNQTRHQTFVRUNBd0hWWFJ5WldOb2RERVFNQTRHQTFVRUJ3d0hWWFJ5WldOb2RERVJNQThHDQpBMVVFQ2d3SVVtRmliMkpoYm1zeEV6QVJCZ05WQkFzTUNrRnpjMlZ6YzIxbGJuUXhJakFnQmdOVkJBTU1HVk5oDQpibVJpYjNndFZGQlFPbVY0WTJWc2JHVnVkQ0JVVUZBeEl6QWhCZ2txaGtpRzl3MEJDUUVXRkc1dkxYSmxjR3g1DQpRSEpoWW05aVlXNXJMbTVzTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFyeUx5DQpvdVRRcjFkdk1UNHF2ZWswZVpzaDhnMERRUUxsT2dCelp3eDdpSW54WUVBZ01OeENLWGlaQ2JtV0hCWXFoNmxwDQpQaCtCQm1ybkJRekIrcXJTTkl5ZDRiRmhmVWxRK2h0SzA4eXlMOWc0bnlMdDBMZUt1eG9hVldwSW5yQjVGUnpvDQpFWTVQUHBjRVhTT2JncitwTTcxQXZ5SnRRTHhaYnFUYW80UzdUUktlY1VtMzJXd2crRldZL1N0U0tsb3gzUW1FDQpheEVHVTdhUGthUWZRczRocnR1VWVQd0tyYmtRMmhRZE1wdkk1b1hSV3pUcWFmdkVRdk5EK0l5THZaUnFmMFRTDQp2SXdzZ3RKZDJ0Y2gya3FQb1V3bmczQW1VRmxlSmJNakZOenJXTTdUSDlMa0tQSXRZdFN1TVR6ZVNlOW8wU21YDQpaRmdjRUJoNURuRVRacUlWdVFJREFRQUJNQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUFTRk9rSmlLUXVMN2ZTDQpFckg2eTVVd2o5V21tUUxGbml0ODV0amJvMjBqc3FzZVR1WnFMZHB3Qk9iaUh4bkJ6N28zTTczUEpBWGRvWGt3DQppTVZ5a1pybFVTRXk3K0ZzTlo0aUZwcG9GYXBIRGJmQmdNMldNVjdWUzZOSzE3ZTB6WGNUR3lTU1J6WHN4dzB5DQpFUUdhT1U4UkozUnJ5MEhXbzlNL0ptWUZyZEJQUC8zc1dBdC8rTzR0aDVKeWs4UmFqTjNmSEZDQW9VejRyWHhoDQpVWmtmLzl1M1EwMzhyUkJ2cWFBKzZjMHVXNThYcUYvUXlVeHVURDRlcjl2ZUNuaVVod0lYNFhCc0ROeElXL3J3DQpCUkF4T1VrRzRWK1hxckJiNzVsQ3llYTFvLzlISWFxMWlJS0k0RGF5MHBpTU9nd1BFZzF3RjM4M3lkMHg4aFJXDQo0enh5SGNFUg0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ";
        var httpHeaders = getHttpHeaders();
        httpHeaders.set(Constants.SIGNATURE_CERTIFICATE, invalidBase64Certificate);
        HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(requestBody,httpHeaders);
        //when
        var response = restTemplate.postForEntity(url,request, PaymentRejectedResponse.class);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getStatus)
                .isEqualTo(TransactionStatus.REJECTED);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getReasonCode)
                .isEqualTo(ErrorReasonCode.UNKNOWN_CERTIFICATE);
    }

    @Test
    @SneakyThrows
    @DisplayName("Invalid Request Flow")
    public void invalidRequest(){
        //given
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL02RABO7134384551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00")
                .currency("ASDFASDF");

        HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(requestBody,getHttpHeaders());
        //when
        var response = restTemplate.postForEntity(url,request, PaymentRejectedResponse.class);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getStatus)
                .isEqualTo(TransactionStatus.REJECTED);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getReasonCode)
                .isEqualTo(ErrorReasonCode.INVALID_REQUEST);
    }

    @Test
    @SneakyThrows
    @DisplayName("General Error Flow")
    public void generalError(){
        //given
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL02RABO7134384551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00");
        var httpHeaders = getHttpHeaders();
        httpHeaders.set(Constants.SIGNATURE_CERTIFICATE, UUID.randomUUID().toString());

        HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(requestBody,httpHeaders);
        //when
        var response = restTemplate.postForEntity(url,request, PaymentRejectedResponse.class);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getStatus)
                .isEqualTo(TransactionStatus.REJECTED);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getReasonCode)
                .isEqualTo(ErrorReasonCode.GENERAL_ERROR);
    }

    @Test
    @SneakyThrows
    @DisplayName("Amount Limit Exceeded Flow")
    public void amountLimitExceed(){
        //given
        var requestBody = new PaymentInitiationRequest().
                debtorIBAN("NL01RABO1000311551").
                creditorIBAN("NL94ABNA1008270121").
                amount("1.00");
        HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(requestBody,getHttpHeaders());
        //when
        var response = restTemplate.postForEntity(url,request, PaymentRejectedResponse.class);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getStatus)
                .isEqualTo(TransactionStatus.REJECTED);

        then(response)
                .extracting(ResponseEntity::getBody)
                .extracting(PaymentRejectedResponse::getReasonCode)
                .isEqualTo(ErrorReasonCode.LIMIT_EXCEEDED);
    }


    private HttpHeaders getHttpHeaders(){
        var requestHeaders = new HttpHeaders();
        requestHeaders.set(Constants.X_REQUEST_ID,
                UUID.randomUUID().toString());
        requestHeaders.set(Constants.SIGNATURE_CERTIFICATE,
                "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlEd2pDQ0Fxb0NDUUR4VmJDaklLeW5RakFOQmdrcWhraUc5dzBCQVFzRkFEQ0JvakVMTUFrR0ExVUVCaE1DDQpUa3d4RURBT0JnTlZCQWdNQjFWMGNtVmphSFF4RURBT0JnTlZCQWNNQjFWMGNtVmphSFF4RVRBUEJnTlZCQW9NDQpDRkpoWW05aVlXNXJNUk13RVFZRFZRUUxEQXBCYzNObGMzTnRaVzUwTVNJd0lBWURWUVFEREJsVFlXNWtZbTk0DQpMVlJRVURwbGVHTmxiR3hsYm5RZ1ZGQlFNU013SVFZSktvWklodmNOQVFrQkZoUnVieTF5WlhCc2VVQnlZV0p2DQpZbUZ1YXk1dWJEQWVGdzB5TURBeE16QXhNekl5TkRsYUZ3MHlNVEF4TWpreE16SXlORGxhTUlHaU1Rc3dDUVlEDQpWUVFHRXdKT1RERVFNQTRHQTFVRUNBd0hWWFJ5WldOb2RERVFNQTRHQTFVRUJ3d0hWWFJ5WldOb2RERVJNQThHDQpBMVVFQ2d3SVVtRmliMkpoYm1zeEV6QVJCZ05WQkFzTUNrRnpjMlZ6YzIxbGJuUXhJakFnQmdOVkJBTU1HVk5oDQpibVJpYjNndFZGQlFPbVY0WTJWc2JHVnVkQ0JVVUZBeEl6QWhCZ2txaGtpRzl3MEJDUUVXRkc1dkxYSmxjR3g1DQpRSEpoWW05aVlXNXJMbTVzTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFyeUx5DQpvdVRRcjFkdk1UNHF2ZWswZVpzaDhnMERRUUxsT2dCelp3eDdpSW54WUVBZ01OeENLWGlaQ2JtV0hCWXFoNmxwDQpQaCtCQm1ybkJRekIrcXJTTkl5ZDRiRmhmVWxRK2h0SzA4eXlMOWc0bnlMdDBMZUt1eG9hVldwSW5yQjVGUnpvDQpFWTVQUHBjRVhTT2JncitwTTcxQXZ5SnRRTHhaYnFUYW80UzdUUktlY1VtMzJXd2crRldZL1N0U0tsb3gzUW1FDQpheEVHVTdhUGthUWZRczRocnR1VWVQd0tyYmtRMmhRZE1wdkk1b1hSV3pUcWFmdkVRdk5EK0l5THZaUnFmMFRTDQp2SXdzZ3RKZDJ0Y2gya3FQb1V3bmczQW1VRmxlSmJNakZOenJXTTdUSDlMa0tQSXRZdFN1TVR6ZVNlOW8wU21YDQpaRmdjRUJoNURuRVRacUlWdVFJREFRQUJNQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUFTRk9rSmlLUXVMN2ZTDQpFckg2eTVVd2o5V21tUUxGbml0ODV0amJvMjBqc3FzZVR1WnFMZHB3Qk9iaUh4bkJ6N28zTTczUEpBWGRvWGt3DQppTVZ5a1pybFVTRXk3K0ZzTlo0aUZwcG9GYXBIRGJmQmdNMldNVjdWUzZOSzE3ZTB6WGNUR3lTU1J6WHN4dzB5DQpFUUdhT1U4UkozUnJ5MEhXbzlNL0ptWUZyZEJQUC8zc1dBdC8rTzR0aDVKeWs4UmFqTjNmSEZDQW9VejRyWHhoDQpVWmtmLzl1M1EwMzhyUkJ2cWFBKzZjMHVXNThYcUYvUXlVeHVURDRlcjl2ZUNuaVVod0lYNFhCc0ROeElXL3J3DQpCUkF4T1VrRzRWK1hxckJiNzVsQ3llYTFvLzlISWFxMWlJS0k0RGF5MHBpTU9nd1BFZzF3RjM4M3lkMHg4aFJXDQo0enh5SGNFUg0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ==");
        requestHeaders.set(Constants.SIGNATURE,
                "AlFr/WbYiekHmbB6XdEO/7ghKd0n6q/bapENAYsL86KoYHqa4eP34xfH9icpQRmTpH0qOkt1vfUPWnaqu+vHBWx/gJXiuVlhayxLZD2w41q8ITkoj4oRLn2U1q8cLbjUtjzFWX9TgiQw1iY0ezpFqyDLPU7+ZzO01JI+yspn2gtto0XUm5KuxUPK24+xHD6R1UZSCSJKXY1QsKQfJ+gjzEjrtGvmASx1SUrpmyzVmf4qLwFB1ViRZmDZFtHIuuUVBBb835dCs2W+d7a+icGOCtGQbFcHvW0FODibnY5qq8v5w/P9i9PSarDaGgYb+1pMSnF3p8FsHAjk3Wccg2a1GQ==");

        return requestHeaders;
    }
}
