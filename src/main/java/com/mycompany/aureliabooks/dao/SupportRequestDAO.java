/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.SupportRequest;
import java.util.List;

/**
 * Customer Support Request Ticket DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class SupportRequestDAO extends BaseDAO {

    public boolean insertSupportRequest(SupportRequest request) {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 5)
        return false;
    }

    public List<SupportRequest> getSupportRequestsByUserId(int userId) {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 5)
        return null;
    }

    public List<SupportRequest> getAllSupportRequests() {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 2)
        return null;
    }

    public SupportRequest getSupportRequestById(int id) {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 2/5)
        return null;
    }

    public boolean replySupportRequest(int id, String replyMessage, int handledByUserId) {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 2)
        return false;
    }
}
