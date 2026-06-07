package com.footballplatform.app.controller;

import com.footballplatform.app.dto.HomePageDataDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.HomePageService;
import com.footballplatform.app.service.SeoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final HomePageService homePageService;
    private final SeoService seoService;

    public HomeController(HomePageService homePageService, SeoService seoService) {
        this.homePageService = homePageService;
        this.seoService = seoService;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(required = false) Long competitionId,
                       @RequestParam(required = false) MatchStatus status,
                       @RequestParam(required = false) String timeFilter,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        HomePageDataDTO pageData = homePageService.getHomePageData(competitionId, status, timeFilter, keyword);

        model.addAttribute("matches", pageData.getMatches());
        model.addAttribute("competitions", pageData.getCompetitions());
        model.addAttribute("statuses", pageData.getStatuses());
        model.addAttribute("selectedCompetitionId", pageData.getSelectedCompetitionId());
        model.addAttribute("selectedCompetitionName", pageData.getSelectedCompetitionName());
        model.addAttribute("selectedStatus", pageData.getSelectedStatus());
        model.addAttribute("selectedTimeFilter", pageData.getSelectedTimeFilter());
        model.addAttribute("selectedKeyword", pageData.getSelectedKeyword());
        model.addAttribute("seo", seoService.buildHomeSeo());
        return "home";
    }

    @GetMapping("/donate")
    public String donate(Model model) {
        model.addAttribute("seo", seoService.buildDonateSeo());
        return "donate";
    }
}
