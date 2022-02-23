package test;

import java.sql.Connection;
import java.sql.DriverManager;

public class simpleconnect {
	public static void main(String[] args) {
		Connection con = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Picard\\Desktop\\test\\haushaltsbuch.db");
			System.out.println("db opened");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
