package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.CompetitionService;
import com.footballplatform.app.service.MatchService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomePageServiceImplTest {

    @Mock
    private MatchService matchService;

    @Mock
    private CompetitionService competitionService;

    @InjectMocks
    private HomePageServiceImpl homePageService;

    @Test
    void getHomePageData_sortsMatchesByActualMatchTimeAscending() {
        CompetitionDTO competition = CompetitionDTO.builder()
                .id(10L)
                .name("Premier League")
                .build();

        MatchDTO laterMatch = MatchDTO.builder()
                .id(2L)
                .teamA("Team B")
                .teamB("Team C")
                .matchTime(LocalDateTime.of(2026, 6, 11, 18, 30))
                .status(MatchStatus.UPCOMING)
                .competitionId(10L)
                .competitionName("Premier League")
                .build();

        MatchDTO earlierMatch = MatchDTO.builder()
                .id(1L)
                .teamA("Team A")
                .teamB("Team B")
                .matchTime(LocalDateTime.of(2026, 6, 10, 15, 0))
                .status(MatchStatus.UPCOMING)
                .competitionId(10L)
                .competitionName("Premier League")
                .build();

        when(competitionService.findAll()).thenReturn(List.of(competition));
        when(matchService.findAll()).thenReturn(List.of(laterMatch, earlierMatch));

        var result = homePageService.getHomePageData(null, null, null, null);

        assertThat(result.getMatches())
                .extracting(MatchDTO::getId)
                .containsExactly(1L, 2L);
    }
}
