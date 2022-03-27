package com.example.demo1;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "hw", value = "/hw")
public class HW extends HttpServlet {

    private static final String URL = "jdbc:mysql://localhost:3306/shop";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static final String INSERT = "insert into users values (?, ?)";

    private final List<User> users = new ArrayList<>();
    private static Connection connection;

    {
        users.add(new User(1, "Ivan"));
        users.add(new User(2, "Oleg"));

        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT);
            for (User user : users) {
                statement.setLong(1, user.getId());
                statement.setString(2, user.getName());
                statement.execute();
            }
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Something wrong in init!");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        try {
            PrintWriter out = resp.getWriter();
            out.println("<html><body>");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                out.println("<h1>" +
                        "User id: " + resultSet.getLong(1) +
                        " name: " + resultSet.getString(2) +
                "</h1>");
            }
            out.println("</body></html>");

            resultSet.close();
            statement.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            users.clear();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}