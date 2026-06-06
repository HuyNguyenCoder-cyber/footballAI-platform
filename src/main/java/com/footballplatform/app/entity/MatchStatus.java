package com.footballplatform.app.entity;

public enum MatchStatus {
    UPCOMING("Sắp diễn ra"),
    LIVE("Đang diễn ra"),
    FINISHED("Đã kết thúc");

    private final String displayName;

    MatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
