package lk.directpay.Login.util;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.directpay.Login.services.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class IpgServiceHelper {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Crypto crypto;

    @Autowired
    public IpgServiceHelper(Crypto crypto) {
        this.crypto = crypto;
    }

    public HttpEntity createPureHttpEntity(HashMap<String, Object> json, String merchantSecret) {
        try {
            String jsonString = new ObjectMapper().writeValueAsString(json);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity(jsonString, httpHeaders);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public HttpEntity createHttpEntity(HashMap<String, Object> json, String merchantSecret) {
//        String secret = crypto.decryptMerchantSecret(merchant.getSecret());
        try {
            String jsonString = new ObjectMapper().writeValueAsString(json);
            String encodedBody = encodeReqBody(jsonString);
            HttpHeaders headers = createHeaders(encodedBody, merchantSecret);
            return new HttpEntity(encodedBody, headers);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public HttpHeaders createHeaders(String data, String merchantSecret) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            String generatedHmac = crypto.generateHmac(data, merchantSecret);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set(HttpHeaders.AUTHORIZATION, "Hmac "+generatedHmac);
            return httpHeaders;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return new HttpHeaders();
    }

    public String encodeReqBody(String data){
        byte[] encodedBytes = Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}