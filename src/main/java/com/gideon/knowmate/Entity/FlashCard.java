package com.gideon.knowmate.Entity;


import lombok.Builder;

@Builder
public class FlashCard {
    private String term;
    private String definition;
    private String imageUrl;
}
