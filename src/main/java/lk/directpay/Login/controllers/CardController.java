package lk.directpay.Login.controllers;

import lk.directpay.Login.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
}
