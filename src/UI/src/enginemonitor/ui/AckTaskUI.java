package enginemonitor.ui;

import java.text.ParseException;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import enginemonitor.data.Entry;
import enginemonitor.data.Task;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class AckTaskUI extends Dialog {
	private static Logger _logger = Logger.getLogger(AckTaskUI.class);

	protected Object result;
	protected Shell shlAcquittementDuneTche;
	private Text _textAge;
	private Task _task;

	private Text _textRemarques;

	private Entry _entry;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @wbp.parser.constructor
	 */
	public AckTaskUI(Task selectedTask, Shell parent, int style) {
		super(parent, style);
		setText("Ajout d'une entrée dans " + selectedTask.get_name());
		
		if(selectedTask == null){
			_logger.error("La tache ne peut etre null !");
		}
		_task = selectedTask;
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AckTaskUI(Task selectedTask, Entry entry, Shell parent, int style) {
		this(selectedTask, parent, style);
		
		setText("Edition d'une entrée");
		if(entry == null){
			_logger.error("L'entrée ne peut etre null !");
		}
		_entry = entry;
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlAcquittementDuneTche.open();
		shlAcquittementDuneTche.layout();
		Display display = getParent().getDisplay();
		while (!shlAcquittementDuneTche.isDisposed()) {
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
		shlAcquittementDuneTche = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlAcquittementDuneTche.setSize(317, 375);
		shlAcquittementDuneTche.setText("Acquittement d'une t\u00E2che");
		shlAcquittementDuneTche.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shlAcquittementDuneTche, SWT.NONE);
		composite.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblAcquittementDeLa = new Label(composite, SWT.NONE);
		lblAcquittementDeLa.setText("Acquittement de la t\u00E2che :");
		
		Label taskName = new Label(composite, SWT.NONE);
		taskName.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		taskName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		taskName.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		taskName.setSize(0, 0);
		taskName.setText(_task.get_name());
		
		Label lblALaDate = new Label(composite, SWT.NONE);
		lblALaDate.setSize(0, 0);
		lblALaDate.setText("A la date :");
		
		DateTime dateTime = new DateTime(composite, SWT.BORDER);
		if(_entry != null){
			try {
				dateTime.setDate(_entry.get_dateUTC().get(GregorianCalendar.YEAR),
						_entry.get_dateUTC().get(GregorianCalendar.MONTH),
						_entry.get_dateUTC().get(GregorianCalendar.DAY_OF_MONTH));
			} catch (ParseException e1) {
				_logger.error("Erruer de parsing de date", e1);
			}
		}
		dateTime.setSize(0, 0);
		
		Label lblAgeDuMoteur = new Label(composite, SWT.NONE);
		lblAgeDuMoteur.setSize(0, 0);
		lblAgeDuMoteur.setText("Age du moteur :");
		
		_textAge = new Text(composite, SWT.BORDER);
		_textAge.addListener(SWT.Verify, new Listener() {
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
		_textAge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				_textAge.selectAll();
			}
		});
		_textAge.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				_textAge.selectAll();
			}
		});
		if(_entry == null){
			_textAge.setText(Integer.toString((int)_task.getEngine().get_age()));
		}
		else{
			if(_entry.get_age() != -1){
				_textAge.setText(Integer.toString(_entry.get_age()));
			}
			else{
				_textAge.setText("");
			}
		}
		_textAge.setSize(0, 0);
		
		Composite composite_1 = new Composite(shlAcquittementDuneTche, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new GridLayout(4, true));
		new Label(composite_1, SWT.NONE);
		
		Button btnValider = new Button(composite_1, SWT.NONE);
		btnValider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
				cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
				
				if(_textAge.getText().isEmpty() && _task.get_engineHours() != -1){
					MessageDialog dialog = new MessageDialog(AckTaskUI.this.shlAcquittementDuneTche, "Acquittement non valide", null, "L'heure du moteur doit être renseigné.", MessageDialog.ERROR, 
							new String[] { "OK"}, 0);
					dialog.open();
				}
				else{
					int ageMoteur = -1;
					if(!_textAge.getText().isEmpty()){
						ageMoteur = Integer.parseInt(_textAge.getText());
					}
					
					if(ageMoteur > _task.getEngine().get_age()){
						MessageDialog dialog = new MessageDialog(AckTaskUI.this.getParent(), "Ajout d'une réalisation de la tâche", null, "Erreur : L'age du moteur à la date de réalisation doit être inférieur ou égal à l'age du moteur courant.", MessageDialog.ERROR, 
								new String[] { "OK"}, 0);
							dialog.open();
					}
					else{
						if(_entry == null){
							_task.addEntry(cal, ageMoteur, _textRemarques.getText()); 
							shlAcquittementDuneTche.dispose();
						}
						else{
							_entry.set_age(ageMoteur);
							_entry.set_dateUTC(cal);
							_entry.set_remarks(_textRemarques.getText());
							shlAcquittementDuneTche.dispose();
						}
					}
				}
			}
		});
		btnValider.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnValider.setSize(0, 0);
		btnValider.setText("Valider");
		new Label(composite_1, SWT.NONE);
		
		Button btnAnnuler = new Button(composite_1, SWT.NONE);
		btnAnnuler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlAcquittementDuneTche.dispose();
			}
		});
		btnAnnuler.setSize(311, 232);
		btnAnnuler.setText("Annuler");
		
		Group grpRemarques = new Group(shlAcquittementDuneTche, SWT.NONE);
		grpRemarques.setText("Remarques");
		grpRemarques.setLayoutData(BorderLayout.CENTER);
		grpRemarques.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		_textRemarques = new Text(grpRemarques, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		if(_entry == null){
		_textRemarques.setText("");
		}
		else{
			_textRemarques.setText(_entry.get_remarks());
		}
	}

}
