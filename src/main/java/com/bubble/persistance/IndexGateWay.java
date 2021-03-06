package com.bubble.persistance;


import com.bubble.markets.entities.instrument.Index;

import java.util.UUID;

public interface IndexGateWay {
    void saveIndex(Index index);

    Index getIndexByUuid(UUID uuid);

    Iterable<Index> getIndicesByMarketUuid(UUID marketUuid);

}
