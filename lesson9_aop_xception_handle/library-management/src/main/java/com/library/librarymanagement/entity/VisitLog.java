package com.library.librarymanagement.entity;

import java.sql.Timestamp;

public class VisitLog {
    private Long visitId;
    private String action;
    private String ipAddress;
    private Timestamp timestamp;
    private String details;

    public VisitLog() {
    }

    public VisitLog(String action, String ipAddress) {
        this.action = action;
        this.ipAddress = ipAddress;
    }

    public VisitLog(String action, String ipAddress, String details) {
        this.action = action;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "VisitLog{" +
                "ipAddress='" + ipAddress + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", visitId=" + visitId +
                '}';
    }
}
