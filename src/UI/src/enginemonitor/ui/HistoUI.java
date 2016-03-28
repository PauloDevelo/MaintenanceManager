package enginemonitor.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import swing2swt.layout.BorderLayout;
import enginemonitor.data.Engine;
import enginemonitor.data.Entry;
import enginemonitor.data.Historic;
import enginemonitor.data.IDataListener;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class HistoUI extends Dialog implements IDataListener{
	private static Logger _logger = Logger.getLogger(HistoUI.class);
	
	
	protected Object result;
	protected Shell shlHistoriqueDuMoteur;
	private Table table;
	private Historic _histo;
	private Engine _engine;
	private Display _display;
	
	private final SimpleDateFormat _df = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public HistoUI(Shell parent, int style) {
		super(parent, style);
		setText("Historique du moteur");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlHistoriqueDuMoteur.open();
		shlHistoriqueDuMoteur.layout();
		_display = getParent().getDisplay();
		while (!shlHistoriqueDuMoteur.isDisposed()) {
			if (!_display.readAndDispatch()) {
				_display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlHistoriqueDuMoteur = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shlHistoriqueDuMoteur.setSize(599, 300);
		shlHistoriqueDuMoteur.setText("Historique du moteur");
		shlHistoriqueDuMoteur.setLayout(new BorderLayout(0, 0));
		
		table = new Table(shlHistoriqueDuMoteur, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				TableItem[] selection = table.getSelection();
				Entry selectedEntry = null;
				if(selection.length == 1){
					selectedEntry = (Entry)selection[0].getData();
				}
				if(selectedEntry != null){
					EntryUI entryUI = new EntryUI(_engine, _histo, getParent(), getStyle());
					entryUI.setEntry(selectedEntry);
					entryUI.open();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 127){
					TableItem[] selection = table.getSelection();
					Entry selectedEntry = null;
					if(selection.length == 1){
						selectedEntry = (Entry)selection[0].getData();
					}
					if(selectedEntry != null){
						removeEntry(selectedEntry);
					}
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			private Menu _menuToDispl;

			@Override
			public void mouseDown(MouseEvent e) {
				
				if(e.button == 3){
					TableItem[] selection = table.getSelection();
					if(selection.length == 1){
						_menuToDispl = new Menu(HistoUI.this.getParent());
						
						MenuItem itemEditer = new MenuItem(_menuToDispl, HistoUI.this.getParent().getStyle());
						itemEditer.setData(selection[0].getData());
						itemEditer.setText("Editer");
						itemEditer.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Entry selectedEntry = (Entry)((MenuItem)e.getSource()).getData();
									EntryUI entryUI = new EntryUI(_engine, _histo, getParent(), getStyle());
									entryUI.setEntry(selectedEntry);
									entryUI.open();
								}
								else{
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
						});
						
						MenuItem itemRemove = new MenuItem(_menuToDispl, HistoUI.this.getParent().getStyle());
						itemRemove.setData(selection[0].getData());
						itemRemove.setText("Supprimer");
						itemRemove.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Entry selectedEntry = (Entry)((MenuItem)e.getSource()).getData();
									removeEntry(selectedEntry);
								}
								else{
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
						});
						
						_menuToDispl.setVisible(true);
					}
				}
			}
		});
		table.setLayoutData(BorderLayout.CENTER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNom = new TableColumn(table, SWT.NONE);
		tblclmnNom.setWidth(151);
		tblclmnNom.setText("Nom");
		
		TableColumn tblclmnDate = new TableColumn(table, SWT.NONE);
		tblclmnDate.setWidth(100);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnAgeDuMoteur = new TableColumn(table, SWT.NONE);
		tblclmnAgeDuMoteur.setWidth(86);
		tblclmnAgeDuMoteur.setText("Age du moteur");
		
		TableColumn tblclmnRemarques = new TableColumn(table, SWT.NONE);
		tblclmnRemarques.setWidth(239);
		tblclmnRemarques.setText("Remarques");
		
		if(_histo != null){
			updateTable();
		}
		
		Composite composite = new Composite(shlHistoriqueDuMoteur, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new GridLayout(6, false));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Button btnAjouterUneEntre = new Button(composite, SWT.NONE);
		btnAjouterUneEntre.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EntryUI entryUI = new EntryUI(_engine, _histo, getParent(), SWT.APPLICATION_MODAL);
				entryUI.open();
			}
		});
		btnAjouterUneEntre.setText("Ajouter une entr\u00E9e");

	}
	
	public void setModel(Engine engine, Historic histo){
		if(_histo != null){
			_histo.removeListener(this);
		}
		_histo = histo;
		_engine = engine;
		
		if(_histo != null){
			_histo.addListenner(this);
		}
		
		if(table != null && !table.isDisposed()){
			
			updateTable();
		}
	}

	private void updateTable() {
		table.removeAll();
		
		ArrayList<Entry> listEntry = _histo.getChronoEntryList();
		for(int i = listEntry.size() - 1; i >= 0; i--){
			Entry myEntry = listEntry.get(i);
			TableItem tableItem= new TableItem(table, SWT.NONE);
			tableItem.setText(0, myEntry.get_name());
			
			try {
				tableItem.setText(1, _df.format(myEntry.get_dateUTC().getTime()));
			} catch (ParseException e) {
				_logger.error("Erreur de parsing de date", e);
			}
			if(myEntry.get_age() != -1){
				tableItem.setText(2, Integer.toString(myEntry.get_age()));
			}
			else{
				tableItem.setText(2, "NR");
			}
			tableItem.setText(3, myEntry.get_remarks());
			tableItem.setBackground(getColor(myEntry));
			tableItem.setData(myEntry);
			
			myEntry.addListenner(this);
		}
		
		table.redraw();
	}

	private Color getColor(Entry myEntry) {
		if(myEntry.get_type().startsWith("Task_")){
			return new Color(_display, 255, 255, 255);
		}
		else{
			return new Color(_display, 255, 247, 40);
		}
	}

	@Override
	public void onDataChanged(Object source, String param) {
		_display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setModel(_engine, _histo);
			}
		});
	}

	private void removeEntry(Entry selectedEntry) {
		MessageDialog dialog = new MessageDialog(HistoUI.this.shlHistoriqueDuMoteur, "Suppression d'une entrée de l'historique", null, "Etes-vous sûre de vouloir supprimer cette entrée ?", MessageDialog.CONFIRM, 
				new String[] { "Oui", "Non"}, 0);
			int result = dialog.open();
			if(result == 0){
				if(_histo.removeEntry(selectedEntry) == false){
					_logger.error("Erreur lors de la suppression d'une entrée");
				}
			}
	}
}
