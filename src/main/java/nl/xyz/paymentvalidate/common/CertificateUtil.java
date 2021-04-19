package nl.xyz.paymentvalidate.common;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration(proxyBeanMethods = false)
public class CertificateUtil {

    @Bean
    @SneakyThrows
    public X509Certificate applicationCertificate(){

        String text = FileUtils.readFileToString(ResourceUtils
                .getFile("classpath:certificate.txt"), UTF_8);
        CertificateFactory cf = CertificateFactory.getInstance(Constants.X509);
        ByteArrayInputStream bytes = new ByteArrayInputStream(text.getBytes());
        return (X509Certificate) cf.generateCertificate(bytes);
    }
}
