package enginemonitor.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.BorderLayout;
import swing2swt.layout.FlowLayout;
import enginemonitor.data.EngineMaintenance;
import enginemonitor.data.Task;

public class ListTaskUI extends Dialog {
	
	private static Logger _logger = Logger.getLogger(ListTaskUI.class);

	protected Object result;
	protected Shell shlGestionnaireDesTches;
	private EngineMaintenance _model;
	private List _list;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ListTaskUI(Shell parent, int style) {
		super(parent, style);
		setText("Gestionnaire de tâche");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlGestionnaireDesTches.open();
		shlGestionnaireDesTches.layout();
		Display display = getParent().getDisplay();
		while (!shlGestionnaireDesTches.isDisposed()) {
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
		shlGestionnaireDesTches = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shlGestionnaireDesTches.setSize(368, 439);
		shlGestionnaireDesTches.setText("Gestionnaire des t\u00E2ches");
		shlGestionnaireDesTches.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shlGestionnaireDesTches, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Button btnAnnuler = new Button(composite, SWT.CENTER);
		btnAnnuler.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				fermer();
			}
		});
		btnAnnuler.setText("Fermer");
		
		Composite composite_1 = new Composite(shlGestionnaireDesTches, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new BorderLayout(0, 0));
		
		Composite compositeButton = new Composite(composite_1, SWT.NONE);
		compositeButton.setLayoutData(BorderLayout.EAST);
		
		_list = new List(composite_1, SWT.BORDER | SWT.V_SCROLL);
		_list.setLayoutData(BorderLayout.CENTER);
		_list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				editTask(_list.getSelection());
			}
		});
		_list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 127){
					removeTask(_list.getSelection());
				}
			}
		});
		
		Label lblListesDesTches = new Label(composite_1, SWT.NONE);
		lblListesDesTches.setLayoutData(BorderLayout.NORTH);
		lblListesDesTches.setText("Listes des t\u00E2ches :");
		compositeButton.setLayout(null);
		
		Button buttonPlus = new Button(compositeButton, SWT.CENTER);
		buttonPlus.setBounds(0, 68, 59, 25);
		buttonPlus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				addTask();
			}

			
		});
		buttonPlus.setText("+");
		
		Button button_Moins = new Button(compositeButton, SWT.CENTER);
		button_Moins.setBounds(0, 99, 59, 25);
		button_Moins.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				removeTask(_list.getSelection());
			}
		});
		button_Moins.setText("-");
		
		Button btnEditer = new Button(compositeButton, SWT.CENTER);
		btnEditer.setBounds(0, 155, 59, 25);
		btnEditer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				editTask(_list.getSelection());
			}

			
		});
		btnEditer.setText("Editer");

		initListTask();
	}

	public void setModel(EngineMaintenance model) {
		_model = model;
	}

	private void initListTask() {
		_list.removeAll();
		
		if(_model != null){
			for(Task task : _model.getArrayListTask()){
				_list.add(task.get_name());
			}
		}
		this._list.redraw();
	}
	
	private void addTask() {
		EditorTaskUI taskUi = new EditorTaskUI(this.shlGestionnaireDesTches, SWT.APPLICATION_MODAL);
		taskUi.setModel(_model);
		taskUi.open();
		initListTask();
	}
	
	private void removeTask(String[] selection) {
		if(selection.length == 0 || selection.length > 1){
			_logger.warn("Une et une seule tache doit être sélectionnée.");
		}
		else{
			MessageDialog dialog = new MessageDialog(ListTaskUI.this.shlGestionnaireDesTches, "Suppression d'une tâche", null, "Etes-vous sûre de vouloir supprimer cette tâche ?", MessageDialog.CONFIRM, 
					new String[] { "Oui", "Non"}, 0);
				int result = dialog.open();
				if(result == 0){
					_model.removeTask(selection[0]);
					initListTask();
				}
			
		}
	}
	
	private void editTask(String[] selection) {
		if(selection.length == 0 || selection.length > 1){
			_logger.warn("Une et une seule tache doit être sélectionnée.");
		}
		else{
			
			Task selectedTask = _model.getTask(selection[0]);
			if(selectedTask != null){
				EditorTaskUI editorTask = new EditorTaskUI(shlGestionnaireDesTches, SWT.APPLICATION_MODAL);
				editorTask.setModel(_model);
				editorTask.setTask(selectedTask);
				editorTask.open();
				initListTask();
			}
			
		}
	}
	
	private void fermer() {
		this.shlGestionnaireDesTches.dispose();
	}
}
