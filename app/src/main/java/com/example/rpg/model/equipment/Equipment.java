package com.example.rpg.model.equipment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.rpg.model.ActivityStatus;

import java.util.List;

/**
 * Equipment base class
 */
public abstract class Equipment {
    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    private EquipmentType type;

    /**
     * Expressed in percentages.
     * It is the type of equipment that determines which stat is being increased by given bonus.
     */
    private int bonus;

    /**
     * Expressed in percentages.
     * Relative to the amount of coins gained upon beating the boss at the end of the previous level.
     */
    @Nullable
    private Integer price;

    /**
     * Actual amount of coins to buy this equipment
     */
    @Ignore
    public Integer calculatedPrice;

    public abstract List<String> getAllStatuses();

    public abstract ActivityStatus getNextActivityStatus(ActivityStatus currentStatus);

    /**
     * Gets equipment description based on its type
     * @return {@link Equipment} description
     */
    public abstract String getDescription();

    @NonNull
    public String getId() { return id; }

    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public EquipmentType getType() { return type; }

    public void setType(EquipmentType type) { this.type = type; }

    public int getBonus() { return bonus; }

    public void setBonus(int bonus) { this.bonus = bonus; }

    @Nullable
    public Integer getPrice() { return price; }

    public void setPrice(@Nullable Integer price) { this.price = price; }
}
