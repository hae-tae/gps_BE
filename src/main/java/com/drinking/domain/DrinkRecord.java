package com.drinking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drink_records")
@Getter
@Setter
@NoArgsConstructor
public class DrinkRecord {

    @Id
    private Long id; // user id와 동일

    @Column(name = "soju_count")
    private Integer sojuCount = 0;

    @Column(name = "beer_count")
    private Integer beerCount = 0;

    @Column(name = "somaek_count")
    private Integer somaekCount = 0;

    @Column(name = "makgeolli_count")
    private Integer makgeolliCount = 0;

    @Column(name = "fruit_count")
    private Integer fruitCount = 0;
}