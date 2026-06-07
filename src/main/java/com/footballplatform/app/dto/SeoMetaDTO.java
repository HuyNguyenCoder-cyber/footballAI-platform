package com.footballplatform.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoMetaDTO {

    private String title;
    private String description;
    private String canonicalUrl;
    private String ogTitle;
    private String ogDescription;
    private String ogType;
    private String ogUrl;
    private String ogImage;
    private String twitterCard;
    private String twitterTitle;
    private String twitterDescription;
    private String structuredDataJson;
}
