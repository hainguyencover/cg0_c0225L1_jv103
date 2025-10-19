package com.library.librarymanagement.entity;

import java.sql.Timestamp;

public class LogBookAction {
    private Long logId;
    private Integer bookId;
    private String action;
    private Integer changeAmount;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private Timestamp timestamp;
    private String actor;

    public LogBookAction() {
    }

    public LogBookAction(Integer bookId, String action, Integer changeAmount, Integer beforeQuantity, Integer afterQuantity, String actor) {
        this.bookId = bookId;
        this.action = action;
        this.changeAmount = changeAmount;
        this.beforeQuantity = beforeQuantity;
        this.afterQuantity = afterQuantity;
        this.actor = actor;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Integer getBeforeQuantity() {
        return beforeQuantity;
    }

    public void setBeforeQuantity(Integer beforeQuantity) {
        this.beforeQuantity = beforeQuantity;
    }

    public Integer getAfterQuantity() {
        return afterQuantity;
    }

    public void setAfterQuantity(Integer afterQuantity) {
        this.afterQuantity = afterQuantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return "LogBookAction{" +
                "logId=" + logId +
                ", bookId=" + bookId +
                ", action='" + action + '\'' +
                ", changeAmount=" + changeAmount +
                ", actor='" + actor + '\'' +
                '}';
    }
}
