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
        tableName = "weapons",
        indices = @Index(value = "name", unique = true)
)
public class Weapon extends Equipment {
    private final static List<ActivityStatus> STATUS_LIFECYCLES = List.of(
            ActivityStatus.PURCHASED,
            ActivityStatus.ETERNAL
    );

    private final static Map<WeaponType, String> DESCRIPTIONS = Map.of(
            WeaponType.SWORD, "Increase Power Points by 5% forever",
            WeaponType.BOW_AND_ARROW, "Constantly increase the percentage of money received by 5%"
    );

    /**
     * It is the weapon type that determines behaviour of the <u>bonus</u> field of the Equipment base class
     */
    private WeaponType weaponType;

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType type) {
        weaponType = type;
    }

    @Override
    public List<String> getAllStatuses() {
        return Arrays.stream(WeaponType.values())
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
        return DESCRIPTIONS.get(weaponType);
    }
}
