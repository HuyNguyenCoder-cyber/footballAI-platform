package com.footballplatform.app.config;

import com.footballplatform.app.entity.MatchStatus;
import java.util.Locale;

public final class CacheKeys {

    private CacheKeys() {
    }

    public static String home(Long competitionId, MatchStatus status, String timeFilter, String keyword) {
        String normalizedKeyword = normalizeBlank(keyword);
        return "competition=" + normalize(competitionId)
                + "|status=" + normalize(status == null ? null : status.name())
                + "|time=" + normalize(normalizeBlank(timeFilter))
                + "|keyword=" + normalize(normalizedKeyword == null ? null : normalizedKeyword.toLowerCase(Locale.ROOT));
    }

    public static String matchAnalysis(Long matchId) {
        return "match=" + normalize(matchId);
    }

    private static String normalize(Object value) {
        return value == null ? "all" : String.valueOf(value);
    }

    private static String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
