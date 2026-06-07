package com.footballplatform.app.dto;

import com.footballplatform.app.entity.MatchStatus;
import java.util.List;
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
public class HomePageDataDTO {

    private List<MatchDTO> matches;
    private List<CompetitionDTO> competitions;
    private MatchStatus[] statuses;
    private Long selectedCompetitionId;
    private String selectedCompetitionName;
    private MatchStatus selectedStatus;
    private String selectedTimeFilter;
    private String selectedKeyword;
}
