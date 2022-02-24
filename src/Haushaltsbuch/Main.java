/**
 * Diese Klasse ist die Main:
 * 	- hier wird die komplette UI konstruiert
 *  - wenn der Konstruktor aufgerufen wird, erstellt Sie direkt eine Instanz
 *  	der Klasse DBAccess
 */

package Haushaltsbuch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
	private DBAccess dbAccess;

	private JPanel contentPane;

	private JButton btnKontoAendern;
	private JLabel lblKontoWert;
	private JTextField txtKonto;
	private JButton btnEinAus;
	private JTextField txtSumme;
	private JComboBox<String> comboBoxKategorie;
	private JTextField txtGrund;
	private JDateChooser dateChooser;
	private JButton btnBuchung;
	private JButton btnDelete;

	private JTable table;
	static DefaultTableModel dtm;
	private int selectedIdx = -1;
	private Object[] toDelete;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					Main frame = new Main();
					frame.getContentPane().setPreferredSize(new Dimension(700, 475));
					frame.pack();
					frame.setResizable(false);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() throws SQLException {
		dbAccess = new DBAccess();

		this.setTitle("Haushaltsbuch");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.contentPane.setLayout(null);
		
		contentPane.setBackground(new Color(255,220,220));
		
		final ImageIcon img = new ImageIcon("assets/fire.png");
		this.setIconImage(img.getImage());

		final JLabel lblKontostand = new JLabel("Kontostand");
		lblKontostand.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblKontostand.setBounds(230, 20, 87, 16);
		contentPane.add(lblKontostand);

		lblKontoWert = new JLabel(dbAccess.getLastKontoValue() + " €");
		lblKontoWert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblKontoWert.setBounds(320, 20, 133, 16);
		contentPane.add(lblKontoWert);

		btnKontoAendern = new JButton("Kontostand \u00E4ndern");
		btnKontoAendern.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnKontoAendern.setBounds(27, 12, 176, 32);
		btnKontoAendern.addActionListener(this);
		contentPane.add(btnKontoAendern);

		txtKonto = new JTextField();
		txtKonto.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtKonto.setBounds(27, 61, 176, 32);
		contentPane.add(txtKonto);
		txtKonto.setColumns(10);

		final JLabel lblSumme = new JLabel("Summe:");
		lblSumme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSumme.setBounds(27, 140, 151, 39);
		contentPane.add(lblSumme);

		txtSumme = new JTextField();
		txtSumme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtSumme.setColumns(10);
		txtSumme.setBounds(27, 170, 176, 32);
		contentPane.add(txtSumme);

		final JLabel lblkategorie = new JLabel("Kategorie:");
		lblkategorie.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblkategorie.setBounds(27, 203, 151, 39);
		contentPane.add(lblkategorie);

		final String[] kategorie = { "Gehalt", "Miete", "Lebensmittel", "Auto", "Computer", "Freizeit", "Haushalt" };
		comboBoxKategorie = new JComboBox<>(kategorie);
		comboBoxKategorie.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxKategorie.setBounds(27, 235, 176, 32);
		comboBoxKategorie.addActionListener(this);
		contentPane.add(comboBoxKategorie);

		final JLabel lbldatum = new JLabel("Datum");
		lbldatum.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbldatum.setBounds(27, 330, 151, 39);
		contentPane.add(lbldatum);

		btnBuchung = new JButton("Buchung");
		btnBuchung.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBuchung.addActionListener(this);
		btnBuchung.setBounds(26, 410, 177, 35);
		contentPane.add(btnBuchung);

		makeTable();

		final JLabel lblGrund = new JLabel("Grund:");
		lblGrund.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGrund.setBounds(27, 270, 151, 39);
		contentPane.add(lblGrund);

		txtGrund = new JTextField();
		txtGrund.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtGrund.setColumns(10);
		txtGrund.setBounds(27, 300, 176, 32);
		contentPane.add(txtGrund);

		dateChooser = new JDateChooser();
		dateChooser.setBounds(27, 361, 176, 34);
		dateChooser.setDate(new Date());
		contentPane.add(dateChooser);

		btnEinAus = new JButton("\u2193  Auszahlung  \u2191");
		btnEinAus.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnEinAus.setBounds(27, 110, 176, 32);
		btnEinAus.setForeground(Color.WHITE);
		btnEinAus.setBackground(Color.RED);
		btnEinAus.addActionListener(this);
		contentPane.add(btnEinAus);

		btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDelete.setBounds(498, 410, 177, 35);
		btnDelete.addActionListener(this);
		contentPane.add(btnDelete);
	}

	public void makeTable() {
		final String[] columNames = { "Kontostand", "Betrag", "Kategorie", "Grund", "Datum" };

		if (dbAccess.getData() != null) {
			dtm = new DefaultTableModel(dbAccess.getData(), columNames) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

		} else {
			Object[] tmpo = { 0 };
			Object[][] tmpo2 = { tmpo };
			dtm = new DefaultTableModel(tmpo2, columNames) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}

		table = new JTable(dtm);

		ListSelectionModel lsm = table.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedIdx = table.getSelectedRow();
			}
		});

		final JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(225, 53, 450, 343);
		scrollPane.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED));
		scrollPane.setAutoscrolls(true);
		contentPane.add(scrollPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnKontoAendern) {

			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String date = simpleDateFormat.format(dateChooser.getDate());

			try {
				Double kontowert = Double.parseDouble(txtKonto.getText().replace(",", "."));
				lblKontoWert.setText(kontowert + " €");
				dbAccess.insertKontostand(kontowert, date);
				txtSumme.requestFocus();
			} catch (NumberFormatException e1) {
				txtKonto.setText("Bitte nur Zahlen eingeben!");
				txtKonto.requestFocus();
				txtKonto.selectAll();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			txtKonto.setText(null);
			dbAccess.addOneToRowAmount();
		}

		if (e.getSource() == this.btnEinAus) {
			if (btnEinAus.getText().equals("\u2193  Auszahlung  \u2191")) {
				btnEinAus.setText("\u2193  Einzahlung  \u2191");
				btnEinAus.setBackground(Color.GREEN);
			} else {
				btnEinAus.setText("\u2193  Auszahlung  \u2191");
				btnEinAus.setBackground(Color.RED);
			}
		}

		if (e.getSource() == this.btnBuchung) {
			String kategorie = comboBoxKategorie.getSelectedItem().toString();
			String grund = txtGrund.getText();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String date = simpleDateFormat.format(dateChooser.getDate());
			try {
				Double betrag = Double.parseDouble(txtSumme.getText().replace(",", "."));

				if (betrag < 0) {
					betrag = betrag * (-1);
				}

				if (btnEinAus.getText().equals("\u2193  Auszahlung  \u2191")) {
					betrag = betrag * (-1);
				}

				dbAccess.insert(betrag, kategorie, grund, date);
				lblKontoWert.setText(dbAccess.getLastKontoValue() + " €");
			} catch (NumberFormatException nfe) {
				txtSumme.setText("Bitte Zahlen eingaben!");
				txtSumme.requestFocus();
				txtSumme.selectAll();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			txtSumme.setText(null);
			txtGrund.setText(null);
			txtSumme.requestFocus();
			dbAccess.addOneToRowAmount();
		}

		if (e.getSource() == this.btnDelete) {
			if (selectedIdx > -1) {
				int savedIdx = selectedIdx;

				fixKontostandInTable(savedIdx);

				rewriteDBFromTable();
			}
		}
	}

	private void rewriteDBFromTable() {
		try {
			dbAccess.deleteEverything();

			for (int i = 0; i < dbAccess.getRowAmount(); i++) {
				toDelete = new Object[5];
				toDelete[0] = table.getValueAt(i, 0);
				toDelete[1] = table.getValueAt(i, 1);
				toDelete[2] = table.getValueAt(i, 2);
				toDelete[3] = table.getValueAt(i, 3);
				toDelete[4] = table.getValueAt(i, 4);

				dbAccess.kevinDerSchlawiner(toDelete[0], toDelete[1], toDelete[2], toDelete[3], toDelete[4]);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void fixKontostandInTable(int savedIdx) {
		dtm.removeRow(savedIdx);
		dbAccess.removeOneFromRowAmount();
		
		if (dbAccess.getRowAmount() > 0) {
			lblKontoWert.setText(dtm.getValueAt(dbAccess.getRowAmount() - 1, 0).toString() + " €");
		} else {			
			lblKontoWert.setText("0 €");			
		}

		try {
			while (savedIdx < dbAccess.getRowAmount()) {
				if (savedIdx == 0 && dbAccess.getRowAmount() > 0) {
					Double tmpBetrag = Double.parseDouble(dtm.getValueAt(savedIdx, 1).toString());
					dtm.setValueAt(tmpBetrag, savedIdx, 0);
					lblKontoWert.setText(dtm.getValueAt(savedIdx, 0).toString() + " €");
					savedIdx++;
				}

				Double tmpBetrag = Double.parseDouble(dtm.getValueAt(savedIdx, 1).toString());
				Double tmpKontostand = Double.parseDouble(dtm.getValueAt(savedIdx - 1, 0).toString());
				dtm.setValueAt(tmpBetrag + tmpKontostand, savedIdx, 0);
				lblKontoWert.setText(dtm.getValueAt(savedIdx, 0).toString() + " €");
				savedIdx++;
			}
		} catch (ArrayIndexOutOfBoundsException xD) {
			System.err.println(xD);
		}
	}
}
