package com.example.rpg.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_equipments",
        indices = {@Index("userId"),  @Index("equipmentId")}
)
public class UserEquipment {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public String equipmentId;

    public boolean isActivated;

    public UserEquipment(long userId, String equipmentId, boolean isActivated) {
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.isActivated = isActivated;
    }
}
