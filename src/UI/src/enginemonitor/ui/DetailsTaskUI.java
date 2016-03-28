package enginemonitor.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import enginemonitor.data.Entry;
import enginemonitor.data.IDataListener;
import enginemonitor.data.Task;

public class DetailsTaskUI extends SashForm implements IDataListener {
	private static Logger _logger = Logger.getLogger(DetailsTaskUI.class);
	
	private Text txtDesc;
	private Table table;
	private Label lblNom;
	private Label lblPeriod;

	private final SimpleDateFormat _df = new SimpleDateFormat("dd/MM/yyyy");

	private Task _selectedTask;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DetailsTaskUI(Composite parent, int style) {
		super(parent, SWT.VERTICAL);
		setLayoutData(BorderLayout.CENTER);
		
		Composite composite = new Composite(this, SWT.BORDER);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new BorderLayout(0, 0));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
		composite_1.setLayoutData(BorderLayout.NORTH);
		
		new Label(composite_1, SWT.NONE);
		
		lblNom = new Label(composite_1, SWT.NONE);
		lblNom.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		lblNom.setText("Nom de la t\u00E2che");
		
		new Label(composite_1, SWT.NONE);
		
		lblPeriod = new Label(composite_1, SWT.NONE);
		lblPeriod.setText("A effectuer toutes les ...h ou tous les 12 mois");
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setText(" ");
		
		Group grpDescription = new Group(composite, SWT.NONE);
		grpDescription.setSize(new Point(0, 120));
		grpDescription.setText("Description");
		grpDescription.setLayoutData(BorderLayout.CENTER);
		grpDescription.setLayout(new FillLayout(SWT.VERTICAL));
		
		txtDesc = new Text(grpDescription, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtDesc.setText("Par exemple, on va d\u00E9crire ici comment faire la vidange.\r\nGr\u00E2ce \u00E0 la pompe manuelle se trouvant c\u00F4t\u00E9 tribord du moteur, vidanger\r\n l'huile usag\u00E9e en utilisant le bidon vide.\r\nEnsuite on rempli avec 4L d'huile 15W40 neuve.\r\nV\u00E9rifier le niveau apr\u00E8s avoir fait trouner le moteur pendant 3 minutes.\r\nCompl\u00E9ter si besoin.");
		
		Group grpHistorique = new Group(this, SWT.NONE);
		grpHistorique.setText("Historique");
		grpHistorique.setLayoutData(BorderLayout.CENTER);
		grpHistorique.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(grpHistorique, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				TableItem[] selection = table.getSelection();
				if(selection.length == 1){
					Entry selectedEntry = (Entry)selection[0].getData();
					AckTaskUI editUi = new AckTaskUI(_selectedTask, selectedEntry, getShell(), style);
					editUi.open();
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			private Menu _menuToDispl;

			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 3 && _selectedTask != null){
					_menuToDispl = new Menu(DetailsTaskUI.this.getParent());
					
					TableItem[] selection = table.getSelection();
					if(selection.length == 1){
						MenuItem itemEdit = new MenuItem(_menuToDispl, DetailsTaskUI.this.getParent().getStyle());
						itemEdit.setData(selection[0].getData());
						itemEdit.setText("Editer");
						itemEdit.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Entry selectedEntry = (Entry)((MenuItem)e.getSource()).getData();
									AckTaskUI editUi = new AckTaskUI(_selectedTask, selectedEntry, getShell(), style);
									editUi.open();
								}
								else if(e.data != null){
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
						});
						
						MenuItem itemSupprimer = new MenuItem(_menuToDispl, DetailsTaskUI.this.getParent().getStyle());
						itemSupprimer.setData(selection[0].getData());
						itemSupprimer.setText("Supprimer");
						itemSupprimer.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Entry selectedEntry = (Entry)((MenuItem)e.getSource()).getData();
									
									MessageDialog dialog = new MessageDialog(DetailsTaskUI.this.getShell(), "Suppression d'une réalisation de tâche", null, "Etes-vous sûre de vouloir supprimer cette réalisation ?", MessageDialog.CONFIRM, 
											new String[] { "Oui", "Non"}, 0);
										int result = dialog.open();
										if(result == 0){
											if(_selectedTask.removeEntry(selectedEntry) == false){
												_logger.error("Erreur lors de la suppression d'une entrée d'une tache");
											}
										}
								}
								else if(e.data != null){
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
						});
						
					}
					
					MenuItem itemAjouter = new MenuItem(_menuToDispl, DetailsTaskUI.this.getParent().getStyle());
					itemAjouter.setData(_selectedTask);
					itemAjouter.setText("Ajouter une réalisation");
					itemAjouter.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(e.getSource() instanceof MenuItem){
								MenuItem menu = (MenuItem)e.getSource();
								AckTaskUI ackTaskUi = new AckTaskUI((Task)menu.getData(), getShell(), style);
								ackTaskUi.open();
							}
							else if(e.data != null){
								_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
							}
						}
					});
					
					_menuToDispl.setVisible(true);
				}
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnDateDeRalisation = new TableColumn(table, SWT.NONE);
		tblclmnDateDeRalisation.setWidth(100);
		tblclmnDateDeRalisation.setText("Date de r\u00E9alisation");
		
		TableColumn tblclmnAgeDuMoteur = new TableColumn(table, SWT.NONE);
		tblclmnAgeDuMoteur.setWidth(100);
		tblclmnAgeDuMoteur.setText("Age du moteur");
		
		TableColumn tblclmnRemarques = new TableColumn(table, SWT.NONE);
		tblclmnRemarques.setWidth(100);
		tblclmnRemarques.setText("Remarques");
		
		int operations = DND.DROP_MOVE;
		DragSource source = new DragSource(table, operations);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		
		source.addDragListener(new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {
				if(table.getSelection().length != 1){
					event.doit = false;
				}
			}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					TableItem selectedItem = table.getSelection()[0];
					Entry entry = ((Entry)selectedItem.getData());
					ObjectOutputStream oos = null;
					
					try {
						oos = new ObjectOutputStream(new FileOutputStream("dad.temp"));
						oos.writeObject(entry);
						oos.flush();
						event.data = "dad.temp";
					} catch (FileNotFoundException e) {
						_logger.error(e);
					} catch (IOException e) {
						_logger.error(e);
					}
					finally{
						if(oos != null){
							try {
								oos.flush();
								oos.close();
							} catch (IOException e) {
								_logger.error(e);
							}
						}
					}
				}
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE){
					TableItem selectedItem = table.getSelection()[0];
					_selectedTask.removeEntry((Entry)selectedItem.getData());
				}
				File fileToDelete = new File("dad.temp");
				fileToDelete.delete();
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setModel(Task task) {
		if(_selectedTask != null){
			for(Entry myEntry : _selectedTask.getHistoric()){
				myEntry.removeListener(this);
			}
			_selectedTask.removeListener(this);
		}
		_selectedTask = task;
		
		
		if(_selectedTask != null){
			_selectedTask.addListenner(this);
			
			lblNom.setText(task.get_name());
			txtDesc.setText(task.get_description());
			
			String str = "";
			if(task.get_engineHours() != -1){
				str = "A effectuer toutes les " + task.get_engineHours() + " ou tous les " + task.get_month() + " mois.";
			}
			else{
				str = "A effectuer tous les " + task.get_month() + " mois.";
			}
			lblPeriod.setText(str);
			
			table.removeAll();
			
			ArrayList<Entry> listEntry = task.getHistoric();
			for(int i = listEntry.size() - 1; i >= 0; i--){
				Entry myentry = listEntry.get(i);
				TableItem item = new TableItem(table, SWT.NONE);
				try {
					item.setText(0, _df.format(myentry.get_dateUTC().getTime()));
				} catch (ParseException e) {
					_logger.error("Erreur de parsing d'une date.", e);
				}
				
				if(myentry.get_age() != -1){
					item.setText(1, Integer.toString(myentry.get_age()));
				}
				else{
					item.setText(1, "NR");
				}
				
				item.setText(2, myentry.get_remarks());
				item.setData(myentry);
				
				myentry.addListenner(this);
			}
		}
		else{
			lblNom.setText("");
			txtDesc.setText("");
			lblPeriod.setText("");
		}
		
		this.redraw();
	}

	@Override
	public void onDataChanged(Object source, String param) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				setModel(_selectedTask);
			}
		});
		
	}
}
