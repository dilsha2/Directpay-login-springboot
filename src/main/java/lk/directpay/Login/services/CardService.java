package lk.directpay.Login.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.directpay.Login.entities.Currency;
import lk.directpay.Login.entities.User;
import lk.directpay.Login.util.AppConstants;
import lk.directpay.Login.util.IpgServiceHelper;
import lk.directpay.Login.util.ResponseDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CardService {

    private final RestTemplate restTemplate;
    private final ResponseDecoder responseDecoder;
    private final IpgServiceHelper ipgServiceHelper;
    private final Crypto crypto;

    @Value("${ipg.endpoint}")
    private String IPG_ENDPOINT;
    @Value("${ipg.stage}")
    private String IPG_STAGE;
    @Value("${dp.merchant.id}")
    private String DP_MERCHANT_ID;
    @Value("${dp.merchant.secret}")
    private String DP_MERCHANT_SECRET;
    @Value("${site.url}")
    private String SITE_URL;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public CardService(RestTemplate restTemplate, ResponseDecoder responseDecoder, IpgServiceHelper ipgServiceHelper, Crypto crypto) {
        this.restTemplate = restTemplate;
        this.responseDecoder = responseDecoder;
        this.ipgServiceHelper = ipgServiceHelper;
        this.crypto = crypto;
    }

    public String generateSignature(String data, String merchantSecret){
        try {
            return crypto.generateHmac(data, merchantSecret);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                InvalidKeyException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "generateSignature => " + e.getMessage(), e);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddingData(User user, String orderId){
        try {
            HashMap<String, Object> json = new HashMap<>();
            json.put("merchant_id", DP_MERCHANT_ID);
            json.put("amount", "10.00");
            json.put("type", AppConstants.MERCHANT_CARD_ADD);
            json.put("order_id", orderId);
            json.put("currency", Currency.LKR.name());
            json.put("response_url", "");
            json.put("return_url", "");
            json.put("first_name", user.getName());
            json.put("email", user.getEmail());
            json.put("page_type", AppConstants.REQUEST_PAGE_TYPE_IN_APP);
            return ipgServiceHelper.encodeReqBody(new ObjectMapper().writeValueAsString(json));
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
