package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.SeoMetaDTO;
import com.footballplatform.app.service.SeoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SeoServiceImpl implements SeoService {

    private final String siteBaseUrl;
    private final String defaultImagePath;

    public SeoServiceImpl(@Value("${app.site.base-url:http://localhost:8080}") String siteBaseUrl,
                          @Value("${app.site.default-image-path:/assets/images/og-default.svg}") String defaultImagePath) {
        this.siteBaseUrl = trimTrailingSlash(siteBaseUrl);
        this.defaultImagePath = defaultImagePath.startsWith("/") ? defaultImagePath : "/" + defaultImagePath;
    }

    @Override
    public SeoMetaDTO buildHomeSeo() {
        String title = "Football AI - Nh\u1eadn \u0111\u1ecbnh b\u00f3ng \u0111\u00e1 b\u1eb1ng AI, d\u1ef1 \u0111o\u00e1n t\u1ef7 s\u1ed1 v\u00e0 ph\u00e2n t\u00edch tr\u1eadn \u0111\u1ea5u";
        String description = "Football AI cung c\u1ea5p nh\u1eadn \u0111\u1ecbnh b\u00f3ng \u0111\u00e1 b\u1eb1ng AI, d\u1ef1 \u0111o\u00e1n t\u1ef7 s\u1ed1, ph\u00e2n t\u00edch phong \u0111\u1ed9, l\u1ecbch s\u1eed \u0111\u1ed1i \u0111\u1ea7u v\u00e0 d\u1eef li\u1ec7u tr\u1eadn \u0111\u1ea5u \u0111\u00e1ng ch\u00fa \u00fd.";
        String canonicalUrl = absoluteUrl("/");
        String structuredDataJson = """
                {
                  "@context": "https://schema.org",
                  "@type": "WebSite",
                  "name": "Football AI",
                  "url": %s
                }
                """.formatted(jsonString(canonicalUrl));
        return baseMeta(title, description, canonicalUrl, "website", defaultImageUrl(), structuredDataJson);
    }

    @Override
    public SeoMetaDTO buildDonateSeo() {
        String title = "\u1ee6ng h\u1ed9 Football AI | Donate";
        String description = "\u1ee6ng h\u1ed9 Football AI \u0111\u1ec3 duy tr\u00ec n\u1ec1n t\u1ea3ng nh\u1eadn \u0111\u1ecbnh b\u00f3ng \u0111\u00e1 b\u1eb1ng AI v\u00e0 ph\u00e1t tri\u1ec3n th\u00eam c\u00e1c n\u1ed9i dung ph\u00e2n t\u00edch ch\u1ea5t l\u01b0\u1ee3ng.";
        String canonicalUrl = absoluteUrl("/donate");
        return baseMeta(title, description, canonicalUrl, "website", defaultImageUrl(), null);
    }

    @Override
    public SeoMetaDTO buildMatchAnalysisSeo(MatchDTO match) {
        String matchName = match.getTeamA() + " vs " + match.getTeamB();
        String title = matchName + " - Nh\u1eadn \u0111\u1ecbnh AI, d\u1ef1 \u0111o\u00e1n t\u1ef7 s\u1ed1 v\u00e0 ph\u00e2n t\u00edch tr\u1eadn \u0111\u1ea5u";
        String description = "Nh\u1eadn \u0111\u1ecbnh " + matchName + " b\u1eb1ng AI. Ph\u00e2n t\u00edch phong \u0111\u1ed9, l\u1ecbch s\u1eed \u0111\u1ed1i \u0111\u1ea7u, \u0111\u1ed9i h\u00ecnh d\u1ef1 ki\u1ebfn, ch\u1ec9 s\u1ed1 t\u1ea5n c\u00f4ng, ph\u00f2ng ng\u1ef1 v\u00e0 d\u1ef1 \u0111o\u00e1n t\u1ef7 s\u1ed1.";
        String canonicalUrl = absoluteUrl("/matches/" + match.getId() + "/analysis");
        String structuredDataJson = """
                {
                  "@context": "https://schema.org",
                  "@type": "SportsEvent",
                  "name": %s,
                  "description": %s,
                  "sport": "Football",
                  "startDate": %s,
                  "url": %s,
                  "image": %s,
                  "competitor": [
                    {
                      "@type": "SportsTeam",
                      "name": %s
                    },
                    {
                      "@type": "SportsTeam",
                      "name": %s
                    }
                  ],
                  "superEvent": {
                    "@type": "SportsOrganization",
                    "name": %s
                  }
                }
                """.formatted(
                jsonString(matchName),
                jsonString(description),
                jsonString(match.getMatchTime() != null
                        ? match.getMatchTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                        : ""),
                jsonString(canonicalUrl),
                jsonString(resolveMatchImage(match)),
                jsonString(match.getTeamA()),
                jsonString(match.getTeamB()),
                jsonString(match.getCompetition() != null ? match.getCompetition().getName() : "")
        );
        return baseMeta(title, description, canonicalUrl, "article", resolveMatchImage(match), structuredDataJson);
    }

    @Override
    public SeoMetaDTO buildKeyPlayerSeo(KeyPlayerDTO keyPlayer, MatchDTO match) {
        String title = keyPlayer.getPlayerName() + " - C\u1ea7u th\u1ee7 ch\u1ee7 ch\u1ed1t | Football AI";
        String description = "Th\u00f4ng tin chi ti\u1ebft c\u1ea7u th\u1ee7 " + keyPlayer.getPlayerName()
                + ", vai tr\u00f2 chi\u1ebfn thu\u1eadt, \u0111\u1ed9i b\u00f3ng v\u00e0 \u1ea3nh h\u01b0\u1edfng t\u1edbi tr\u1eadn \u0111\u1ea5u "
                + match.getTeamA() + " vs " + match.getTeamB() + ".";
        String canonicalUrl = absoluteUrl("/key-players/" + keyPlayer.getId());
        String image = keyPlayer.getImageUrl() != null && !keyPlayer.getImageUrl().isBlank()
                ? absoluteUrl(keyPlayer.getImageUrl())
                : defaultImageUrl();
        String structuredDataJson = """
                {
                  "@context": "https://schema.org",
                  "@type": "Person",
                  "name": %s,
                  "description": %s,
                  "image": %s,
                  "memberOf": {
                    "@type": "SportsTeam",
                    "name": %s
                  },
                  "subjectOf": {
                    "@type": "SportsEvent",
                    "name": %s
                  },
                  "url": %s
                }
                """.formatted(
                jsonString(keyPlayer.getPlayerName()),
                jsonString(keyPlayer.getShortDescription()),
                jsonString(image),
                jsonString(keyPlayer.getTeamName()),
                jsonString(match.getTeamA() + " vs " + match.getTeamB()),
                jsonString(canonicalUrl)
        );
        return baseMeta(title, description, canonicalUrl, "profile", image, structuredDataJson);
    }

    @Override
    public String absoluteUrl(String path) {
        if (path == null || path.isBlank()) {
            return siteBaseUrl;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return siteBaseUrl + (path.startsWith("/") ? path : "/" + path);
    }

    @Override
    public String defaultImageUrl() {
        return absoluteUrl(defaultImagePath);
    }

    private SeoMetaDTO baseMeta(String title, String description, String canonicalUrl, String ogType,
                                String imageUrl, String structuredDataJson) {
        return SeoMetaDTO.builder()
                .title(title)
                .description(description)
                .canonicalUrl(canonicalUrl)
                .ogTitle(title)
                .ogDescription(description)
                .ogType(ogType)
                .ogUrl(canonicalUrl)
                .ogImage(imageUrl)
                .twitterCard("summary_large_image")
                .twitterTitle(title)
                .twitterDescription(description)
                .structuredDataJson(structuredDataJson)
                .build();
    }

    private String jsonString(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t") + "\"";
    }

    private String resolveMatchImage(MatchDTO match) {
        if (match.getTeamALogo() != null && !match.getTeamALogo().isBlank()) {
            return absoluteUrl(match.getTeamALogo());
        }
        if (match.getTeamBLogo() != null && !match.getTeamBLogo().isBlank()) {
            return absoluteUrl(match.getTeamBLogo());
        }
        return defaultImageUrl();
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8080";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
