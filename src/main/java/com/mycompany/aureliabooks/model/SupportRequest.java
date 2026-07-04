/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Customer Support Request ticket model.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class SupportRequest implements Serializable {
    private int id;
    private int userId;
    private Integer handledByUserId;
    private String subject;
    private String message;
    private String replyMessage;
    private String status; // OPEN, PROCESSING, RESOLVED
    private Timestamp createdAt;

    public SupportRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getHandledByUserId() {
        return handledByUserId;
    }

    public void setHandledByUserId(Integer handledByUserId) {
        this.handledByUserId = handledByUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
