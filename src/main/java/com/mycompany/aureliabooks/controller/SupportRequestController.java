/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.SupportRequestDAO;
import com.mycompany.aureliabooks.model.SupportRequest;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Customer Support Request Controller for both clients and admins.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "SupportRequestController", urlPatterns = {"/support", "/admin/support"})
public class SupportRequestController extends HttpServlet {

    // Enterprise Standard: Use standard Logger instead of System.out or e.printStackTrace()
    private static final Logger LOGGER = Logger.getLogger(SupportRequestController.class.getName());

    private static final String SUBJECT_REGEX = "^[\\p{L}\\p{N}\\s\\p{P}]{5,150}$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.contains("/admin/")) {
            // Admin actions (Authentication and Role are handled by SecurityFilter)
            try {
                String action = request.getParameter("action");
                SupportRequestDAO dao = new SupportRequestDAO();
                
                if ("reply".equals(action)) {
                    String idParam = request.getParameter("id");
                    if (idParam != null && !idParam.trim().isEmpty()) {
                        int ticketId = Integer.parseInt(idParam);
                        SupportRequest supportRequest = dao.getSupportRequestById(ticketId);
                        request.setAttribute("supportRequest", supportRequest);
                    }
                    request.getRequestDispatcher("/WEB-INF/support/reply.jsp").forward(request, response);
                } else {
                    List<SupportRequest> adminSupportRequests = dao.getAllSupportRequests();
                    request.setAttribute("adminSupportRequests", adminSupportRequests);
                    request.getRequestDispatcher("/WEB-INF/support/list.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid ticket ID format in GET", e);
                request.setAttribute("errorMessage", "Định dạng ID yêu cầu hỗ trợ không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected critical error in SupportRequestController Admin (GET)", e);
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống khi tải trang Admin. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        } else {
            // Customer actions
            try {
                // 1. Validate Authentication
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("user") == null) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=login");
                    return;
                }

                User user = (User) session.getAttribute("user");
                
                // 2. Fetch personal tickets
                SupportRequestDAO dao = new SupportRequestDAO();
                List<SupportRequest> supportRequests = dao.getSupportRequestsByUserId(user.getId());
                
                request.setAttribute("supportRequests", supportRequests);
                
                // 3. Render view based on action
                // create.jsp contains both the submit form and ticket history
                request.getRequestDispatcher("/WEB-INF/support/create.jsp").forward(request, response);
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected critical error in SupportRequestController (GET)", e);
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng liên hệ bộ phận hỗ trợ.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.contains("/admin/")) {
            // Admin replies to support request ticket
            try {
                // SecurityFilter has already guaranteed session and user existence for /admin/*
                HttpSession session = request.getSession(false);
                User adminUser = (User) session.getAttribute("user");
                
                // 1. Extract payload
                String idParam = request.getParameter("id");
                String replyMessage = request.getParameter("replyMessage");
                String status = request.getParameter("status");
                
                // 2. Validate Business Logic
                if (idParam == null || idParam.trim().isEmpty() || replyMessage == null || replyMessage.trim().isEmpty()) {
                    session.setAttribute("errorMsg", "Vui lòng nhập đầy đủ nội dung phản hồi.");
                    response.sendRedirect(request.getContextPath() + "/admin/support?action=reply&id=" + (idParam != null ? idParam : ""));
                    return;
                }
                
                if (replyMessage.trim().length() < 10) {
                    session.setAttribute("errorMsg", "Nội dung phản hồi phải có ít nhất 10 ký tự.");
                    response.sendRedirect(request.getContextPath() + "/admin/support?action=reply&id=" + idParam);
                    return;
                }
                
                if (replyMessage.trim().length() > 2000) {
                    session.setAttribute("errorMsg", "Nội dung phản hồi không được vượt quá 2000 ký tự.");
                    response.sendRedirect(request.getContextPath() + "/admin/support?action=reply&id=" + idParam);
                    return;
                }
                
                int ticketId = Integer.parseInt(idParam);
                
                // 3. Execute DB Update
                SupportRequestDAO dao = new SupportRequestDAO();
                boolean isUpdated = dao.replySupportRequest(ticketId, replyMessage.trim(), status, adminUser.getId());
                
                if (isUpdated) {
                    session.setAttribute("successMsg", "Đã gửi phản hồi và cập nhật trạng thái thành công.");
                    response.sendRedirect(request.getContextPath() + "/admin/support");
                } else {
                    session.setAttribute("errorMsg", "Lỗi máy chủ khi gửi phản hồi. Vui lòng thử lại sau.");
                    response.sendRedirect(request.getContextPath() + "/admin/support?action=reply&id=" + ticketId);
                }
                
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid ticket ID format in POST", e);
                request.setAttribute("errorMessage", "Định dạng ID yêu cầu hỗ trợ không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected critical error in SupportRequestController Admin (POST)", e);
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống khi xử lý phản hồi. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        } else {
            // Customer submits support request ticket
            try {
                // 1. Validate Authentication
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("user") == null) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=login");
                    return;
                }

                User user = (User) session.getAttribute("user");
                
                // 2. Extract payload
                String subject = request.getParameter("subject");
                String message = request.getParameter("message");

                // 3. Validate Business Logic
                String trimmedSubject = subject != null ? subject.trim() : "";
                String trimmedMessage = message != null ? message.trim() : "";

                if (trimmedSubject.isEmpty() || trimmedMessage.isEmpty()) {
                    session.setAttribute("errorMsg", "Vui lòng nhập đầy đủ Tiêu đề và Nội dung.");
                    response.sendRedirect(request.getContextPath() + "/support?action=create");
                    return;
                }

                if (trimmedSubject.length() < 5 || trimmedSubject.length() > 150) {
                    session.setAttribute("errorMsg", "Tiêu đề phải từ 5 đến 150 ký tự.");
                    response.sendRedirect(request.getContextPath() + "/support?action=create");
                    return;
                }

                if (!trimmedSubject.matches(SUBJECT_REGEX)) {
                    session.setAttribute("errorMsg", "Tiêu đề chứa ký tự không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/support?action=create");
                    return;
                }

                if (trimmedMessage.length() < 10 || trimmedMessage.length() > 2000) {
                    session.setAttribute("errorMsg", "Nội dung phải từ 10 đến 2000 ký tự.");
                    response.sendRedirect(request.getContextPath() + "/support?action=create");
                    return;
                }

                // 4. Populate Model object
                SupportRequest supportReq = new SupportRequest();
                supportReq.setUserId(user.getId());
                supportReq.setSubject(subject.trim());
                supportReq.setMessage(message.trim());

                // 5. Execute DB Update
                SupportRequestDAO dao = new SupportRequestDAO();
                boolean isInserted = dao.insertSupportRequest(supportReq);

                if (isInserted) {
                    session.setAttribute("successMsg", "Gửi yêu cầu hỗ trợ thành công. Đội ngũ admin sẽ phản hồi trong thời gian sớm nhất.");
                } else {
                    session.setAttribute("errorMsg", "Lỗi máy chủ khi gửi yêu cầu. Vui lòng thử lại sau.");
                }

                // 6. PRG Pattern Redirect
                response.sendRedirect(request.getContextPath() + "/support?action=create");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected critical error in SupportRequestController (POST)", e);
                request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống. Vui lòng liên hệ bộ phận hỗ trợ.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        }
    }
}
