package enginemonitor.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import enginemonitor.data.EngineMaintenance;
import enginemonitor.data.Task;

public class EditorTaskUI extends Dialog {
	
	private static Logger _logger = Logger.getLogger(EditorTaskUI.class);

	protected Object result;
	protected Shell shlEditionDuneTche;
	private Text textNom;
	private Text textNbMois;
	private Text textNbHeure;
	private Text textDesc;
	
	private Task _originalTask;
	
	private EngineMaintenance _model;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditorTaskUI(Shell parent, int style) {
		super(parent, style);
		setText("Ajout d'une tâche");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlEditionDuneTche.open();
		shlEditionDuneTche.layout();
		Display display = getParent().getDisplay();
		while (!shlEditionDuneTche.isDisposed()) {
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
		shlEditionDuneTche = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlEditionDuneTche.setSize(472, 317);
		if(_originalTask != null){
			shlEditionDuneTche.setText("Edition d'une t\u00E2che");
		}
		else{
			shlEditionDuneTche.setText("Création d'une t\u00E2che");
		}
		RowLayout rl_shlEditionDuneTche = new RowLayout(SWT.VERTICAL);
		rl_shlEditionDuneTche.fill = true;
		shlEditionDuneTche.setLayout(rl_shlEditionDuneTche);
		
		Composite composite = new Composite(shlEditionDuneTche, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.fill = true;
		composite.setLayout(rl_composite);
		
		Label lblNom = new Label(composite, SWT.NONE);
		lblNom.setLayoutData(new RowData(42, SWT.DEFAULT));
		lblNom.setText("Nom :");
		
		textNom = new Text(composite, SWT.BORDER);
		textNom.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textNom.selectAll();
			}
		});
		textNom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textNom.selectAll();
			}
		});
		textNom.setLayoutData(new RowData(146, SWT.DEFAULT));
		if(_originalTask != null)textNom.setText(_originalTask.get_name());
		
		Composite composite_1 = new Composite(shlEditionDuneTche, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblAEffectuerToutes = new Label(composite_1, SWT.NONE);
		lblAEffectuerToutes.setText("A effectuer toutes les ");
		
		textNbHeure = new Text(composite_1, SWT.BORDER);
		textNbHeure.addListener(SWT.Verify, new Listener() {
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
		textNbHeure.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textNbHeure.selectAll();
			}
		});
		textNbHeure.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textNbHeure.selectAll();
			}
		});
		if(_originalTask != null){
			if(_originalTask.get_engineHours() != -1){
				textNbHeure.setText(Integer.toString(_originalTask.get_engineHours()));
			}
			else{
				textNbHeure.setText("");
			}
		}
		
		
		Label lblHMoteur = new Label(composite_1, SWT.NONE);
		lblHMoteur.setText(" H moteur");
		
		Composite composite_2 = new Composite(shlEditionDuneTche, SWT.NONE);
		composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblOuTousLes = new Label(composite_2, SWT.NONE);
		lblOuTousLes.setText("ou tous les");
		
		textNbMois = new Text(composite_2, SWT.BORDER);
		textNbMois.addListener(SWT.Verify, new Listener() {
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
		textNbMois.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textNbMois.selectAll();
			}
		});
		textNbMois.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textNbMois.selectAll();
			}
		});
		if(_originalTask != null){
			textNbMois.setText(Integer.toString(_originalTask.get_month()));
		}
		else{
			textNbMois.setText("12");
		}
		
		
		Label lblMois = new Label(composite_2, SWT.NONE);
		lblMois.setText(" mois");
		
		Group grpDescription = new Group(shlEditionDuneTche, SWT.NONE);
		grpDescription.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpDescription.setLayoutData(new RowData(452, 135));
		grpDescription.setText("Description");
		
		textDesc = new Text(grpDescription, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		if(_originalTask != null)textDesc.setText(_originalTask.get_description());
		
		Composite composite_5 = new Composite(shlEditionDuneTche, SWT.NONE);
		
		Button btnSauvegarder = new Button(composite_5, SWT.NONE);
		btnSauvegarder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				validateTask();
			}

			
		});
		btnSauvegarder.setBounds(153, 10, 75, 25);
		btnSauvegarder.setText("Sauvegarder");
		
		Button btnAnnuler = new Button(composite_5, SWT.NONE);
		btnAnnuler.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				annuler();
			}

			
		});
		btnAnnuler.setBounds(234, 10, 75, 25);
		btnAnnuler.setText("Annuler");

	}

	private void annuler() {
		this.shlEditionDuneTche.dispose();
	}
	
	public void setTask(Task selectedTask) {
		_originalTask = selectedTask;
	}
	
	private void validateTask() {
		
		if(textNbMois.getText().isEmpty()){
			MessageDialog dialog = new MessageDialog(EditorTaskUI.this.shlEditionDuneTche, "Tâche non valide", null, "La période d'éxécution d'une tâche en mois ne peut être vide.", MessageDialog.ERROR, 
					new String[] { "OK"}, 0);
			dialog.open();
		}
		else if(textNom.getText().isEmpty()){
			MessageDialog dialog = new MessageDialog(EditorTaskUI.this.shlEditionDuneTche, "Tâche non valide", null, "Le nom d'une tâche doit être renseigné.", MessageDialog.ERROR, 
					new String[] { "OK"}, 0);
			dialog.open();
		}
		else{
			short nbMois = Short.parseShort(textNbMois.getText());
			int engineHours = -1;
			if(!textNbHeure.getText().isEmpty()){
				engineHours = Integer.parseInt(textNbHeure.getText());
			}
			
			if(_originalTask == null){
				Task newTask = new Task(textNom.getText(), textDesc.getText(), nbMois, engineHours, _model.get_histo(), _model.get_engine());
				_model.addTask(newTask);
			}
			else{
				_originalTask.set_name(textNom.getText());
				_originalTask.set_description(textDesc.getText());
				_originalTask.set_month(nbMois);
				_originalTask.set_engineHours(engineHours);
			}
			
			this.shlEditionDuneTche.dispose();
		}
	}

	public void setModel(EngineMaintenance model) {
		_model = model;
	}

}
