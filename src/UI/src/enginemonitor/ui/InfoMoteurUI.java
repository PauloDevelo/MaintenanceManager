package enginemonitor.ui;

import java.util.GregorianCalendar;

import jssc.SerialPortList;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import enginemonitor.data.Engine;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DateTime;

public class InfoMoteurUI extends Dialog {

	private static Logger _logger = Logger.getLogger(InfoMoteurUI.class);
	
	protected Object result;
	protected Shell shlInfosDuMoteur;
	private Text txtMarque;
	private Text txtType;
	private Engine _engine;
	private Combo comboPortCOM;
	private DateTime _dateTime;
	private Text textAge;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InfoMoteurUI(Shell parent, int style) {
		super(parent, style);
		setText("Edition des informations moteur");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlInfosDuMoteur.open();
		shlInfosDuMoteur.layout();
		Display display = getParent().getDisplay();
		while (!shlInfosDuMoteur.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlInfosDuMoteur = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlInfosDuMoteur.setSize(315, 197);
		shlInfosDuMoteur.setText("Infos du moteur");
		shlInfosDuMoteur.setLayout(new GridLayout(2, true));
		
		Label lblMarque = new Label(shlInfosDuMoteur, SWT.NONE);
		lblMarque.setText("Marque :");
		
		txtMarque = new Text(shlInfosDuMoteur, SWT.BORDER);
		GridData gd_txtMarque = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtMarque.widthHint = 107;
		txtMarque.setLayoutData(gd_txtMarque);
		if(_engine != null) txtMarque.setText(_engine.get_brand());
		
		Label lblType = new Label(shlInfosDuMoteur, SWT.NONE);
		lblType.setText("Type :");
		
		txtType = new Text(shlInfosDuMoteur, SWT.BORDER);
		GridData gd_txtType = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtType.widthHint = 107;
		txtType.setLayoutData(gd_txtType);
		if(_engine != null) txtType.setText(_engine.get_type());
		
		Label lblPortCom = new Label(shlInfosDuMoteur, SWT.NONE);
		lblPortCom.setText("Port COM :");
		
		comboPortCOM = new Combo(shlInfosDuMoteur, SWT.NONE);
		comboPortCOM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onPortComChanged();
			}
		});
		GridData gd_comboPortCOM = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		gd_comboPortCOM.widthHint = 107;
		comboPortCOM.setLayoutData(gd_comboPortCOM);
		
		int selectedIndex = -1;
		int index = 0;
		comboPortCOM.add("");
		
		if(_engine != null && _engine.get_portName().isEmpty())selectedIndex = index;
		
		SerialPortList serialPortList = new SerialPortList();
		String[] portNames = serialPortList.getPortNames();
		for(String portName : portNames){
			index++;
			comboPortCOM.add(portName);
			if(_engine != null && _engine.get_portName().compareTo(portName) == 0)selectedIndex = index;
		}
		if(_engine != null){
			comboPortCOM.select(selectedIndex);
		}
		
		Label lblDateDinstallation = new Label(shlInfosDuMoteur, SWT.NONE);
		lblDateDinstallation.setText("Date d'installation :");
		
		_dateTime = new DateTime(shlInfosDuMoteur, SWT.BORDER);
		if(_engine != null){
			_dateTime.setDate(_engine.get_installation().get(GregorianCalendar.YEAR), 
					_engine.get_installation().get(GregorianCalendar.MONTH), 
					_engine.get_installation().get(GregorianCalendar.DAY_OF_MONTH));
		}
		
		Label lblAgeEnH = new Label(shlInfosDuMoteur, SWT.NONE);
		lblAgeEnH.setText("Age en H :");
		
		textAge = new Text(shlInfosDuMoteur, SWT.BORDER);
		GridData txtAgeGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		txtAgeGd.widthHint = 107;
		textAge.setLayoutData(txtAgeGd);
		if(_engine != null) textAge.setText(Integer.toString((int)(_engine.get_age() + 0.5)));
		
		
		Button btnSave = new Button(shlInfosDuMoteur, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sauver();
			}
		});
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSave.setText("Sauver");
		
		Button btnCancel = new Button(shlInfosDuMoteur, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fermer();
			}
		});
		btnCancel.setText("Annuler");

	}

	public void setModel(Engine engine) {
		_engine = engine;
	}
	
	private void onPortComChanged() {
		
	}
	
	private void sauver() {
		int selectedIndex = comboPortCOM.getSelectionIndex();
		
		if(selectedIndex == 0){
			_engine.set_portName("");
		}
		else{
			selectedIndex--;
			
			SerialPortList serialList = new SerialPortList();
			String[] portNames = serialList.getPortNames();
			if(portNames.length > selectedIndex && selectedIndex >= 0){
				_engine.set_portName(portNames[selectedIndex]);
			}
		}
		
		_engine.set_brand(txtMarque.getText());
		_engine.set_type(txtType.getText());
		GregorianCalendar date = new GregorianCalendar(_dateTime.getYear(), _dateTime.getMonth(), _dateTime.getDay());
		_engine.set_installation(date);
		
		try{
			Float age = new Float(textAge.getText());
			_engine.set_age(age);
		}
		catch(NumberFormatException ex){
			_logger.error("Erreur lors du parsing de l'age du moteur.", ex);
		}
		
		this.shlInfosDuMoteur.dispose();
	}
	
	private void fermer() {
		this.shlInfosDuMoteur.dispose();
	}
}
