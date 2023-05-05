package com.gg.server.domain.user.dto;


import com.gg.server.domain.pchange.data.PChange;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserHistoryData {
    private int ppp;
    private LocalDateTime date;

    public UserHistoryData(PChange pChange){
        this.ppp = pChange.getPppResult();
        this.date = pChange.getCreatedAt();
    }
}
