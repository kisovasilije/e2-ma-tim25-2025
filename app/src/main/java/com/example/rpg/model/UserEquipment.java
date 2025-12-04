package com.example.rpg.model;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.rpg.model.equipment.Equipment;

@Entity(
        tableName = "user_equipments",
        indices = {@Index("userId"),  @Index("equipmentId")}
)
public class UserEquipment {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;

    public String equipmentId;

    public ActivityStatus status;

    @Ignore
    public Equipment equipment;

    public UserEquipment(long userId, String equipmentId, ActivityStatus status) {
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.status = status;
    }

    public void updateStatus() {
        var nextStatus = equipment.getNextActivityStatus(status);
        if (nextStatus == null) {
            Log.w("[UserEquipment]", "Status not updated.");
            return;
        }

        status = nextStatus;
    }
}
