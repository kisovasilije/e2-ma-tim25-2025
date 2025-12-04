package com.example.rpg.model.equipment;

import androidx.room.Entity;
import androidx.room.Index;

import com.example.rpg.model.ActivityStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity(
        tableName = "armors",
        indices = @Index(value = "name", unique = true)
)
public class Armor extends Equipment {
    private final static List<ActivityStatus> STATUS_LIFECYCLES = List.of(
            ActivityStatus.PURCHASED,
            ActivityStatus.ACTIVE,
            ActivityStatus.ACTIVE_2,
            ActivityStatus.USED
    );

    private final static Map<ArmorType, String> DESCRIPTIONS = Map.of(
            ArmorType.GLOVES, "Increase Power Points by 10% two times",
            ArmorType.SHIELD, "Increase the chance of a successful attack by 10%",
            ArmorType.BOOTS, "Chance of increasing the number of attacks by 40%"
    );

    /**
     * It is the armor type that determines behaviour of the <u>bonus</u> field of the Equipment base class
     */
    private ArmorType armorType;

    public ArmorType getArmorType() {
        return armorType;
    }

    public void setArmorType(ArmorType type) {
        armorType = type;
    }

    @Override
    public List<String> getAllStatuses() {
        return Arrays.stream(ArmorType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityStatus getNextActivityStatus(ActivityStatus currentStatus) {
        ActivityStatus next = null;
        for (int i = 0; i < STATUS_LIFECYCLES.size(); i++) {
            if (currentStatus == STATUS_LIFECYCLES.get(i) && STATUS_LIFECYCLES.get(i + 1) != null) {
                next = STATUS_LIFECYCLES.get(i + 1);
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
        return DESCRIPTIONS.get(armorType);
    }
}
