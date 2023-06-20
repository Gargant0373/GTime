package me.gargant.classes;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderboardItem {
    private UUID uuid;
    private Time time;
}
