package com.XiaoFeng.controller;

import com.XiaoFeng.dao.OrderDao;
import com.XiaoFeng.model.Item;
import com.XiaoFeng.model.Order;
import com.XiaoFeng.model.Payment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@WebServlet(name = "OrderServlet", value = "/order")
public class OrderServlet extends HttpServlet {
    Connection con = null;

    @Override
    public void init() throws ServletException {
        super.init();
        con = (Connection) getServletContext().getAttribute("con");
        if (con == null) {
            try {
                con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=userdb", "sa", "123456");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get all payment type
        List<Payment> paymentTypeList = Payment.findAllPayment(con);
        request.setAttribute("paymentTypeList", paymentTypeList);
        String path = "/WEB-INF/views/order.jsp";
        request.getRequestDispatcher(path).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //ger all parameters
        int customerId = request.getParameter("customerId") != null ? Integer.parseInt(request.getParameter("customerId")) : 0;
        int paymentId = request.getParameter("paymentId") != null ? Integer.parseInt(request.getParameter("paymentId")) : 0;
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String address1 = request.getParameter("address1");
        String address2 = request.getParameter("address2");
        String postalCode = request.getParameter("postalCode");
        String state = request.getParameter("state");
        String city = request.getParameter("city");
        String country = request.getParameter("country");
        String notes = request.getParameter("notes");
        double orderTotal = request.getParameter("orderTotals") != null ? Double.parseDouble(request.getParameter("orderTotal")) : 0.0;
        //set into order model
        String message = null;
        if (customerId == 0 || paymentId == 0 || firstName == null || firstName.trim().length() == 0 || phone == null || phone.trim().length() == 0 || address1 == null || address1.trim().length() == 0
                || postalCode == null || postalCode.trim().length() == 0) {
            message = "错误！请填写必要信息(Required(*))";
            request.setAttribute("message", message);
            List<Payment> paymentTypeList = Payment.findAllPayment(con);
            request.setAttribute("paymentTypeList", paymentTypeList);
            String path = "/WEB-INF/views/order.jsp";
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }
        Order o = new Order();
        o.setCustomerId(customerId);
        o.setPaymentId(paymentId);
        o.setFirstName(firstName);
        o.setLastName(lastName);
        o.setPhone(phone);
        o.setAddress1(address1);
        o.setAddress2(address2);
        o.setPostalCode(postalCode);
        o.setState(state);
        o.setCity(city);
        o.setCountry(country);
        o.setNotes(notes);
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("cart") != null) {
            ArrayList<Item> cartItems = (ArrayList<Item>) session.getAttribute("cart");
            o.setOrderDetails(new HashSet<>(cartItems));
        }
        OrderDao dao = new OrderDao();
        int n = 0;
        try {
            n = dao.save(con, o);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (n > 0) {
            String path = "/WEB-INF/views/orderSuccess.jsp";
            request.getRequestDispatcher(path).forward(request, response);
        }
        //save order

        //forward

    }
}
