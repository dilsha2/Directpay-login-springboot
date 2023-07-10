package lk.directpay.Login.controllers;

import lk.directpay.Login.entities.User;
import lk.directpay.Login.model.DefaultResponse;
import lk.directpay.Login.model.MerchantUserPrinciple;
import lk.directpay.Login.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Value("${dp.merchant.secret}")
    private String DP_MERCHANT_SECRET;
    @Value("${ipg.stage}")
    private String IPG_STAGE;
    @Value("${site.url}")
    private String SITE_URL;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @PostMapping("/save")
    @ResponseBody
    public DefaultResponse saveCard(@RequestBody HashMap<String,Object> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated()) {
            MerchantUserPrinciple merchantUserPrinciple = (MerchantUserPrinciple) auth.getPrincipal();
            User merchant = merchantUserPrinciple.getMerchantUser().getMerchant();
            if(merchant==null) {
                LOGGER.log(Level.SEVERE, "Merchant not found");
            }

            Integer cardId = (Integer) payload.get("card_id");
            Integer walletId = (Integer) payload.get("wallet_id");

            if(cardId!=null&&walletId!=null) {
                merchant.setCardId(cardId);
                merchant.setWalletId(walletId);
                merchantService.save(merchant);
                return new DefaultResponse(200,"success", "Card saved successfully");
            }else {
                return new DefaultResponse(400, "error","Invalid data");
            }


        }else {
            return new DefaultResponse(400,"error","User not authenticated");
        }
    }
}
