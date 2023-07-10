package lk.directpay.Login.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ResponseDecoder {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public JSONObject decodeResponse(ResponseEntity responseEntity) throws JSONException {
        JSONObject jsonObject;
        LOGGER.log(Level.INFO, responseEntity.getBody().toString());

        if (responseEntity.getStatusCodeValue() == 200 && responseEntity.hasBody()) {
            jsonObject = new JSONObject(responseEntity.getBody().toString());
        } else {
            jsonObject = null;
        }
        return jsonObject;

    }
}
