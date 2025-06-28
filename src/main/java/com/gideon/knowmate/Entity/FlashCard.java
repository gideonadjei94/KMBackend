package com.gideon.knowmate.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashCard {
    private String term;
    private String definition;
    private String imageUrl;
}
