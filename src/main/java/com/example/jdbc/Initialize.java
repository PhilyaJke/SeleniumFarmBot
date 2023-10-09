package com.example.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Initialize {

    public List<Accounts> getAllAccounts(){

        List<Accounts> list = new ArrayList<>();

        String url = "jdbc:postgresql://localhost:5432/seleniumbot";
        String user = "postgres";
        String password = "postgres";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement st = con.prepareStatement("SELECT * FROM Accounts");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Accounts accounts = new Accounts(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        rs.getDouble(9)
                );
                list.add(accounts);
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Initialize.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return list;
    }

    public void updateSocPublicBalance(Double socpublic_balance, String gmail){

        String url = "jdbc:postgresql://localhost:5432/seleniumbot";
        String user = "postgres";
        String password = "postgres";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement("UPDATE Accounts" +
                     " SET socpublic_balance = ?" +
                     " WHERE gmail = ?")){
            pstmt.setDouble(1, socpublic_balance);
            pstmt.setString(2, gmail);
            pstmt.executeUpdate();
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Initialize.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public class Accounts {

        private long id;
        private String gmail;
        private String password;
        private String proxy_ip;
        private String proxy_username;
        private String proxy_password;
        private Double socpublic_balance;
        private Double aviso_balance;
        private Double total_balance;

        public Accounts(long id, String gmail, String password, String proxy_ip, String proxy_username, String proxy_password, Double socpublic_balance, Double aviso_balance, Double total_balance) {
            this.id = id;
            this.gmail = gmail;
            this.password = password;
            this.proxy_ip = proxy_ip;
            this.proxy_username = proxy_username;
            this.proxy_password = proxy_password;
            this.socpublic_balance = socpublic_balance;
            this.aviso_balance = aviso_balance;
            this.total_balance = total_balance;
        }

        public String getGmail() {
            return gmail;
        }

        public void setGmail(String gmail) {
            this.gmail = gmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getProxy_ip() {
            return proxy_ip;
        }

        public void setProxy_ip(String proxy_ip) {
            this.proxy_ip = proxy_ip;
        }

        public String getProxy_username() {
            return proxy_username;
        }

        public void setProxy_username(String proxy_username) {
            this.proxy_username = proxy_username;
        }

        public String getProxy_password() {
            return proxy_password;
        }

        public void setProxy_password(String proxy_password) {
            this.proxy_password = proxy_password;
        }

        public Double getSocpublic_balance() {
            return socpublic_balance;
        }

        public void setSocpublic_balance(Double socpublic_balance) {
            this.socpublic_balance = socpublic_balance;
        }

        public Double getAviso_balance() {
            return aviso_balance;
        }

        public void setAviso_balance(Double aviso_balance) {
            this.aviso_balance = aviso_balance;
        }

        public Double getTotal_balance() {
            return total_balance;
        }

        public void setTotal_balance(Double total_balance) {
            this.total_balance = total_balance;
        }

    }
}
