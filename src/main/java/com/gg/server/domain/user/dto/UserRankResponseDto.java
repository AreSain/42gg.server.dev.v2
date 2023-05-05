package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRankResponseDto {
    private int rank;
    private int ppp;
    private int wins;
    private int losses;
    private double winRate;
}
