package enginemonitor.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import enginemonitor.data.Entry;
import enginemonitor.data.Task;

public class ListChronoTask extends Composite implements SelectionListener {
	private static Logger _logger = Logger.getLogger(ListChronoTask.class);
	
	private Table table;
	private DetailsTaskUI _detailsTaskUi;

	private Menu _menuToDispl;
	
	private final SimpleDateFormat _df = new SimpleDateFormat("dd/MM/yyyy");

	private Collection<Task> _collTask;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ListChronoTask(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addDragDetectListener(new DragDetectListener() {
			public void dragDetected(DragDetectEvent e) {
				_logger.debug("dragDetected -> " + e.toString());
			}
		});
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 3){
					TableItem[] selection = table.getSelection();
					if(selection.length == 1){
						_menuToDispl = new Menu(ListChronoTask.this.getParent());
						MenuItem itemAcqu = new MenuItem(_menuToDispl, ListChronoTask.this.getParent().getStyle());
						itemAcqu.setData(selection[0].getData());
						itemAcqu.setText("Acquitter");
						itemAcqu.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Task selectedTask = (Task)((MenuItem)e.getSource()).getData();
									AckTaskUI uiAcquit = new AckTaskUI(selectedTask, getShell(), style);
									uiAcquit.open();
								}
								else{
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
							
							
						});
						MenuItem itemEditer = new MenuItem(_menuToDispl, ListChronoTask.this.getParent().getStyle());
						itemEditer.setData(selection[0].getData());
						itemEditer.setText("Editer");
						itemEditer.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if(e.getSource() instanceof MenuItem){
									Task selectedTask = (Task)((MenuItem)e.getSource()).getData();
									EditorTaskUI uiEditer = new EditorTaskUI(getShell(), style);
									uiEditer.setTask(selectedTask);
									uiEditer.open();
									redrawTable();
								}
								else if(e.data != null){
									_logger.debug("Objet non reconnu : " + e.data.getClass().getName());
								}
							}
							
							
						});
						_menuToDispl.setVisible(true);
					}
				}
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item instanceof TableItem){
					TableItem item = (TableItem)e.item;
					onTaskSelectionChanged((Task)item.getData());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(e.item instanceof TableItem){
					TableItem item = (TableItem)e.item;
					
					Task selectedTask = (Task)item.getData();
					AckTaskUI uiAcquit = new AckTaskUI(selectedTask, getShell(), style);
					uiAcquit.open();
				}	
			}
		});
		
		TableColumn tblclmnNomDeLa = new TableColumn(table, SWT.NONE);
		tblclmnNomDeLa.setWidth(100);
		tblclmnNomDeLa.setText("Nom de la t\u00E2che");
		
		TableColumn tblclmnAEffectuer = new TableColumn(table, SWT.NONE);
		tblclmnAEffectuer.setWidth(100);
		tblclmnAEffectuer.setText("A effectuer");
		
		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(100);
		tblclmnDescription.setText("Description");
		
		int operations = DND.DROP_MOVE | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(table, operations);
		
		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {textTransfer};
		target.setTransfer(types);
		
		target.addDropListener(new DropTargetListener() {
			
			private Task _taskOver;

			@Override
			public void dropAccept(DropTargetEvent event) {
			}
			
			@Override
			public void drop(DropTargetEvent event) {
				if (textTransfer.isSupportedType(event.currentDataType)) {
					String filename = (String)event.data;
					ObjectInputStream ois = null;
					
					FileInputStream fis;
					try {
						fis = new FileInputStream(filename);
						ois = new ObjectInputStream(fis);
						
						final Entry entry = (Entry)ois.readObject();
						_taskOver.addEntry(entry);
						
					} catch (IOException e) {
						_logger.error(e);
					} catch (ClassNotFoundException e) {
						_logger.error(e);
					}
				}
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				if(event.item != null && event.item instanceof TableItem){
					TableItem itemOver = (TableItem)event.item;
					_taskOver = (Task)itemOver.getData();
				}
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setModel(Collection<Task> collTask) {
		_collTask = collTask;
		redrawTable();
	}

	private void redrawTable() {
		table.removeAll();
		table.clearAll();
		
		if(_collTask != null){
			for(Task task : _collTask){
				TableItem tableItem= new TableItem(table, SWT.NONE);
				tableItem.setText(0, task.get_name());
				
				String str;
				if(task.get_engineHours() == -1){
					str = "Le " + 
							_df.format(task.whenTodoIt().getTime());
				}
				else{
					str = "Dans " + 
								Integer.toString((int)(task.howManyEngineHours() + 0.5)) + 
								" heures moteur ou le " + 
								_df.format(task.whenTodoIt().getTime());
				}
				tableItem.setText(1, str);
				
				tableItem.setText(2, task.get_description());
				tableItem.setBackground(getColor(task));
				tableItem.setData(task);
			}
		}
		table.redraw();
	}

	//Permet de déterminer la couleur à afficher dans la liste des choses à faire
	private Color getColor(Task task) {
		switch(task.getLevel()){
		case 1 :
			return new Color(getDisplay(), 43, 209, 29);
		case 2 :
			return new Color(getDisplay(), 255, 247, 40);
		case 3 :
			return new Color(getDisplay(), 255, 15, 23);
		default :
			_logger.warn("Ce niveau n'est pas pris en compte : " + task.getLevel());
			return new Color(getDisplay(), 255, 255, 255);
		}
	}
	
	public void setDetailTaskUi(DetailsTaskUI detailsTaskUi) {
		_detailsTaskUi = detailsTaskUi;
	}
	
	private void onTaskSelectionChanged(Task data) {
		_detailsTaskUi.setModel(data);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		_logger.debug(e.toString());
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		_logger.debug(e.toString());
		
	}

}
