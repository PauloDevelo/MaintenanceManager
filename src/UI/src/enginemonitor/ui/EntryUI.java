package enginemonitor.ui;

import java.text.ParseException;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;
import enginemonitor.data.Engine;
import enginemonitor.data.Entry;
import enginemonitor.data.Historic;

public class EntryUI extends Dialog {
	private static Logger _logger = Logger.getLogger(EntryUI.class);

	protected Object result;
	protected Shell shlEntrePonctuelle;
	private Text textAge;
	private Text textRq;
	private Text textIntitule;
	private DateTime dateTime;
	private Historic _histo;
	private Engine _engine;
	
	private Entry _entry = null;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EntryUI(Engine engine, Historic histo, Shell parent, int style) {
		super(parent, style);
		setText("Ajout d'une entrée ponctuelle");
		
		_histo = histo;
		_engine = engine;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlEntrePonctuelle.open();
		shlEntrePonctuelle.layout();
		Display display = getParent().getDisplay();
		while (!shlEntrePonctuelle.isDisposed()) {
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
		shlEntrePonctuelle = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlEntrePonctuelle.setSize(450, 300);
		shlEntrePonctuelle.setText("Entr\u00E9e ponctuelle");
		shlEntrePonctuelle.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shlEntrePonctuelle, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setText("Intitul\u00E9 :");
		
		textIntitule = new Text(composite, SWT.BORDER);
		textIntitule.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textIntitule.selectAll();
			}
		});
		textIntitule.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		textIntitule.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textIntitule.selectAll();
			}
		});
		
		Label lblDate = new Label(composite, SWT.NONE);
		lblDate.setText("Date :");
		
		dateTime = new DateTime(composite, SWT.BORDER);
		
		Label lblAgeDuMoteur = new Label(composite, SWT.NONE);
		lblAgeDuMoteur.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAgeDuMoteur.setText("Age du moteur :");
		
		textAge = new Text(composite, SWT.BORDER);
		textAge.addListener(SWT.Verify, new Listener() {
		    @Override
			public void handleEvent(Event e) {
		    	String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
			}
		});
		textAge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textAge.selectAll();
			}
		});
		textAge.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textAge.selectAll();
			}
		});
		if(_engine != null && _entry == null){
			textAge.setText(Integer.toString((int)(_engine.get_age() + (float)0.5)));
		}
		else if(_entry != null){
			if(_entry.get_age() == -1){
				textAge.setText("");
			}
			else{
				textAge.setText(Integer.toString(_entry.get_age()));
			}
			if(!_entry.get_type().startsWith("PONCT")){
				textAge.setEnabled(false);
			}
		}
		textAge.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Group grpRemarques = new Group(shlEntrePonctuelle, SWT.NONE);
		grpRemarques.setText("Remarques");
		grpRemarques.setLayoutData(BorderLayout.CENTER);
		grpRemarques.setLayout(new BorderLayout(0, 0));
		
		textRq = new Text(grpRemarques, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textRq.setLayoutData(BorderLayout.CENTER);
		
		if(_entry != null){
			textAge.setText(Integer.toString(_entry.get_age()));
			textRq.setText(_entry.get_remarks());;
			textIntitule.setText(_entry.get_name());
			if(_entry.get_type().startsWith("Task_")){
				textIntitule.setEnabled(false);
			}
			
			try {
				dateTime.setYear(_entry.get_dateUTC().get(GregorianCalendar.YEAR));
				dateTime.setMonth(_entry.get_dateUTC().get(GregorianCalendar.MONTH));
				dateTime.setDay(_entry.get_dateUTC().get(GregorianCalendar.DAY_OF_MONTH));
			} catch (ParseException e) {
				_logger.error("Erreur lors du parsing d'une date");
			}
		}
		
		Composite composite_1 = new Composite(shlEntrePonctuelle, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new GridLayout(4, true));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		Button btnSauvegarder = new Button(composite_1, SWT.NONE);
		btnSauvegarder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(textIntitule.getText().isEmpty()){
					MessageDialog dialog = new MessageDialog(EntryUI.this.shlEntrePonctuelle, "Entrée ponctuelle non valide", null, "L'intitulé de l'entrée ponctuelle doit être renseigné.", MessageDialog.ERROR, 
							new String[] { "OK"}, 0);
					dialog.open();
				}
				else if(textAge.getText().isEmpty() && _entry != null && !_entry.get_type().startsWith("PONCT")){
					MessageDialog dialog = new MessageDialog(EntryUI.this.shlEntrePonctuelle, "Entrée non valide", null, "Les heures moteur doivent être renseignées pour cette entrée.", MessageDialog.ERROR, 
							new String[] { "OK"}, 0);
					dialog.open();
				}
				else{
					String name = textIntitule.getText();
					GregorianCalendar date = new GregorianCalendar(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
					int age = -1;
					if(!textAge.getText().isEmpty()){
						age = Integer.parseInt(textAge.getText());
					}
					String remark = textRq.getText();
					
					if(_entry == null){
						String type = "PONCT";
						
						Entry entry = new Entry(type, name, date, age, remark);
						_histo.addEntry(entry);
					}
					else{
						_entry.set_name(name);
						_entry.set_age(age);
						_entry.set_dateUTC(date);
						_entry.set_remarks(remark);
					}
					EntryUI.this.shlEntrePonctuelle.dispose();
				}
			}
		});
		btnSauvegarder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSauvegarder.setText("Sauvegarder");
		
		Button btnFermer = new Button(composite_1, SWT.NONE);
		btnFermer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EntryUI.this.shlEntrePonctuelle.dispose();
			}
		});
		btnFermer.setText("Fermer");

	}

	public void setEntry(Entry selectedEntry) {
		_entry  = selectedEntry;
	}

}
