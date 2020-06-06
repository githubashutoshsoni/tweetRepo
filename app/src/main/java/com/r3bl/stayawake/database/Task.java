package com.r3bl.stayawake.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    public Long id;

    @ColumnInfo
    public String content;

    public Task(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Task() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
