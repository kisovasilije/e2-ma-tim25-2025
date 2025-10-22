package com.example.rpg.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_equipments",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Equipment.class,
                        parentColumns = "id",
                        childColumns = "equipmentId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("userId"),  @Index("equipmentId")}
)
public class UserEquipment {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public long equipmentId;

    public boolean isActivated;

    public UserEquipment(long userId, long equipmentId, boolean isActivated) {
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.isActivated = isActivated;
    }
}
