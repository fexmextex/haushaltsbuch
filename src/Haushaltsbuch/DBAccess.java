package Haushaltsbuch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;

public class DBAccess {

	Connection connection;
	private Object[][] data;

	Double ktoStand = 0.0;
	double kontoraus;
	
	Double lastKontoValue = 0.0;
	
	public DBAccess() throws SQLException {
		this.connection = establishConnection();
		selectAll();
	}
	
	public double getLastKontoValue() {
		return this.lastKontoValue;
	}

	public Object[][] getData() {
		return this.data;
	}
	
	public double getKontoraus() {
		return this.kontoraus;
	}

	public Connection establishConnection() throws SQLException {
		String tmp = DBAccess.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString()
				.replace("file:/", "").replace("sqlDemo.jar", "");
		String dbPath = "jdbc:sqlite:" + tmp + "haushaltsbuch.db";		

//		String dbPath = "jdbc:sqlite:haushaltsbuch.db"; // für jarDatei

		return DriverManager.getConnection(dbPath);
	}

	public void selectAll() throws SQLException {
		data = new String[rowCounter()][5];
		String sql = "SELECT * FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);

		int z = 0;
		while (result.next()) {
			ktoStand = result.getDouble("kontostand");
			data[z][0] = ktoStand+"";
			
			Double betrag = result.getDouble("betrag");
			data[z][1] = betrag + "";
			
			data[z][2] = result.getString("kategorie");
			
			data[z][3] = result.getString("grund");
			
			data[z][4] = result.getString("datum");

			System.out.println(
					(data[z][0] + " | " + data[z][1] + " | " + data[z][2] + " | " + data[z][3] + " | " + data[z][4])); // test
			z++;
		}
		kontoraus = ktoStand;
		System.out.println(kontoraus);
		
		this.lastKontoValue = ktoStand;
		
		// test 
		result.close();
		statement.close();
	}
	
	private int rowCounter() throws SQLException {
		String sql = "SELECT count(*) FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		result.next();
		return result.getInt(1);
	}

	public void closeConnection() throws SQLException {
		this.connection.close();
	}

	public void insert(final Double betrag, final String kategorie, final String grund, final Date date) throws SQLException {
		Double kontostand = this.lastKontoValue + betrag;		
		
		String sql = "INSERT INTO buch(kontostand,betrag,kategorie,grund,datum) VALUES ('" + kontostand + "','" + betrag + "','"
				+ kategorie + "','" + grund + "','" + date.toString() + "')";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);

		Object[] tmp = {kontostand, betrag, kategorie, grund, date};
		Main.dtm.addRow(tmp);
		
		this.lastKontoValue = kontostand;
	}
	
	public void insert(final double kontostand) throws SQLException {
		String date = LocalDate.now().toString();
		String sql = "INSERT INTO buch(kontostand,betrag,kategorie,grund,datum) VALUES ('" + kontostand + "','','-','-','" + date + "')";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);
		
		this.lastKontoValue = kontostand;

		Object[] tmp = {kontostand, 0, "-", "-", date};
		Main.dtm.addRow(tmp);
	}
	
	public void delete(final Object kontostand, final Object betrag, final Object kategorie, final Object grund, final Object date) throws SQLException {
		String sql = "DELETE FROM buch WHERE kontostand = " + kontostand + //
							" AND betrag = " + betrag + //
							" AND kategorie = '" + kategorie + //
							"' AND grund = '" + grund + //
							"' AND datum = '" + date + "'";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);
	}

}
