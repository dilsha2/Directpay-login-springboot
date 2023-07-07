package lk.directpay.Login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Card {

    private int cardId;
    private String mask;
    private String brand;
    private String type;
    private String issuer;
    private String expiry;
    private LocalDateTime createdAt;
    private boolean defaultCard;

}
