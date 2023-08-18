package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.type.HistoryType;
import com.gg.server.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
    private final CoinHistoryRepository coinHistoryRepository;
    private final CoinPolicyRepository coinPolicyRepository;

    @Transactional
    public void addAttendanceCoinHistory(User user) {
        int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getAttendance();
        addCoinHistory(new CoinHistory(user, HistoryType.ATTENDANCECOIN.getHistory(), amount));
    }

    @Transactional
    public void addNormalCoin(User user){
        int  amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getNormal();
        addCoinHistory(new CoinHistory(user, HistoryType.NORMAL.getHistory(), amount));
    }

    @Transactional
    public void addRankWinCoin(User user){
        int  amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getRankWin();
        addCoinHistory(new CoinHistory(user, HistoryType.RANKWIN.getHistory(), amount));
    }

    @Transactional
    public void addRankLoseCoin(User user){
        int  amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getRankLose();
        addCoinHistory(new CoinHistory(user, HistoryType.RANKLOSE.getHistory(), amount));
    }

    private void addCoinHistory(CoinHistory coinHistory){
        coinHistoryRepository.save(coinHistory);
    }
}