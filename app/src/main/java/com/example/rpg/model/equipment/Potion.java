package com.example.rpg.model.equipment;

import androidx.room.Entity;
import androidx.room.Index;

import com.example.rpg.model.ActivityStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity(
        tableName = "potions",
        indices = @Index(value = "name", unique = true)
)
public class Potion extends Equipment {
    private final static Map<PotionType, List<ActivityStatus>> STATUS_LIFECYCLES = Map.of(
            PotionType.ONE_TIME_20, List.of(
                    ActivityStatus.PURCHASED,
                    ActivityStatus.ACTIVE,
                    ActivityStatus.USED
            ),
            PotionType.ONE_TIME_40, List.of(
                    ActivityStatus.PURCHASED,
                    ActivityStatus.ACTIVE,
                    ActivityStatus.USED
            ),
            PotionType.INFINITE_5, List.of(
                    ActivityStatus.PURCHASED,
                    ActivityStatus.ETERNAL
            ),
            PotionType.INFINITE_10, List.of(
                    ActivityStatus.PURCHASED,
                    ActivityStatus.ETERNAL
            )
    );

    private final static Map<PotionType, String> DESCRIPTIONS = Map.of(
            PotionType.ONE_TIME_20, "Increase Power Points by 20% one time",
            PotionType.ONE_TIME_40, "Increase Power Points by 40% one time",
            PotionType.INFINITE_5, "Increase Power Points by 5% forever",
            PotionType.INFINITE_10, "Increase Power Points by 10% forever"
    );

    /**
     * It is the potion type that determines behaviour of <u>bonus</u> field of Equipment base class
     */
    private PotionType pType;

    public PotionType getPType() {
        return pType;
    }

    public void setPType(PotionType type) {
        pType = type;
    }

    @Override
    public List<String> getAllStatuses() {
        return Arrays.stream(PotionType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityStatus getNextActivityStatus(ActivityStatus currentStatus) {
        var lifecycles = STATUS_LIFECYCLES.get(pType);
        if (lifecycles == null) {
            return null;
        }

        ActivityStatus next = null;
        for (int i = 0; i < lifecycles.size(); i++) {
            if (currentStatus == lifecycles.get(i) && lifecycles.get(i + 1) != null) {
                next = lifecycles.get(i + 1);
                break;
            }
        }

        return next;
    }

    /**
     * Gets equipment description based on its type
     *
     * @return {@link Equipment} description
     */
    @Override
    public String getDescription() {
        return DESCRIPTIONS.get(pType);
    }
}
