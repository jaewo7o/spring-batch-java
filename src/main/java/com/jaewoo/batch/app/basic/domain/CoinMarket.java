package com.jaewoo.batch.app.basic.domain;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoinMarket {
    private String market;
    private String korean_name;
    private String english_name;
}
