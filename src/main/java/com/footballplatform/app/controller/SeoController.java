package com.footballplatform.app.controller;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.service.KeyPlayerService;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.SeoService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SeoController {

    private final MatchService matchService;
    private final KeyPlayerService keyPlayerService;
    private final SeoService seoService;

    public SeoController(MatchService matchService, KeyPlayerService keyPlayerService, SeoService seoService) {
        this.matchService = matchService;
        this.keyPlayerService = keyPlayerService;
        this.seoService = seoService;
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robots() {
        String sitemapUrl = seoService.absoluteUrl("/sitemap.xml");
        return """
                User-agent: *
                Allow: /
                Allow: /matches/
                Allow: /key-players/
                Disallow: /admin/
                Disallow: /login

                Sitemap: %s
                """.formatted(sitemapUrl);
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> sitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        appendUrl(xml, seoService.absoluteUrl("/"));

        List<MatchDTO> matches = matchService.findAll();
        for (MatchDTO match : matches) {
            appendUrl(xml, seoService.absoluteUrl("/matches/" + match.getId() + "/analysis"));
        }

        List<KeyPlayerDTO> keyPlayers = keyPlayerService.findAll();
        for (KeyPlayerDTO keyPlayer : keyPlayers) {
            appendUrl(xml, seoService.absoluteUrl("/key-players/" + keyPlayer.getId()));
        }

        xml.append("</urlset>");
        return ResponseEntity.ok(xml.toString());
    }

    private void appendUrl(StringBuilder xml, String url) {
        xml.append("<url><loc>")
                .append(escapeXml(url))
                .append("</loc></url>");
    }

    private String escapeXml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
