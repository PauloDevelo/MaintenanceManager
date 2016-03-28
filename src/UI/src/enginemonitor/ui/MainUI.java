package enginemonitor.ui;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import enginemonitor.data.EngineMaintenance;
import enginemonitor.data.IDataListener;
import enginemonitor.ui.interfaces.UIActionListener;

public class MainUI implements IDataListener{

	protected Shell shell;
	
	private DetailsTaskUI _detailsTaskUi = null;
	private EngineInfoUI _engineInfo = null;
	private ListChronoTask _listChrono = null;

	private EngineMaintenance _model;

	private Display _display;

	private ArrayList<UIActionListener> _listListener = new ArrayList<UIActionListener>();
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainUI window = new MainUI();
			window.open(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @param model 
	 */
	public void open(EngineMaintenance model) {
		_display = Display.getDefault();
		createContents();
		setModel(model);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!_display.readAndDispatch()) {
				_display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				onDispose();
			}
		});
		shell.setSize(640, 480);
		shell.setText("Gestionnaire de la maintenance du moteur");
		shell.setLayout(new BorderLayout(0, 0));
		
		
		shell.setImage(SWTResourceManager.getImage(EngineInfoUI.class, "/enginemonitor/ui/images/engine.gif"));
		
		SashForm sashForm = new SashForm(shell, SWT.BORDER);
		sashForm.setLayoutData(BorderLayout.CENTER);
		
		Composite compositeLeft = new Composite(sashForm, SWT.NONE);
		compositeLeft.setLayout(new BorderLayout(0, 0));
		
		_engineInfo = new EngineInfoUI(compositeLeft, SWT.NONE);
		_engineInfo.setLayoutData(BorderLayout.NORTH);
		
		Composite compositeRight = new Composite(sashForm, SWT.NONE);
		compositeRight.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		_detailsTaskUi = new DetailsTaskUI(compositeRight, SWT.NONE);
		
		_listChrono = new ListChronoTask(compositeLeft, SWT.NONE);
		_listChrono.setLayoutData(BorderLayout.CENTER);
		_listChrono.setDetailTaskUi(_detailsTaskUi);
		
		
		
		sashForm.setWeights(new int[] {1, 1});
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem subMenuFichier = new MenuItem(menu, SWT.CASCADE);
		subMenuFichier.setText("Fichier");
		
		Menu menuOuvrir = new Menu(subMenuFichier);
		subMenuFichier.setMenu(menuOuvrir);
		
		MenuItem mntmOuvrir = new MenuItem(menuOuvrir, SWT.NONE);
		mntmOuvrir.setText("Ouvrir");
		
		MenuItem mntmSauver = new MenuItem(menuOuvrir, SWT.NONE);
		mntmSauver.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				_model.save();
			}
		});
		mntmSauver.setText("Sauver");
		
		MenuItem mntmSauverSous = new MenuItem(menuOuvrir, SWT.NONE);
		mntmSauverSous.setText("Sauver sous ...");
		mntmSauverSous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
		        fd.setText("Save As ...");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.xml" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        for(UIActionListener listenner : _listListener){
		        	listenner.saveModel(selected);
		        }
			}
		});
		
		new MenuItem(menuOuvrir, SWT.SEPARATOR);
		
		MenuItem mntmExporterAuFormat = new MenuItem(menuOuvrir, SWT.NONE);
		mntmExporterAuFormat.setText("Exporter au format PDF");
		mntmExporterAuFormat.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
		        fd.setText("Export in pdf ...");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.pdf" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        for(UIActionListener listenner : _listListener){
		        	listenner.exportPDF(selected);
		        }
			}
		});
		
		new MenuItem(menuOuvrir, SWT.SEPARATOR);
		
		MenuItem mntmQuitter = new MenuItem(menuOuvrir, SWT.NONE);
		mntmQuitter.setText("Quitter");
		mntmQuitter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}

			
		});
		
		MenuItem mntmEdition_1 = new MenuItem(menu, SWT.CASCADE);
		mntmEdition_1.setText("Edition");
		
		Menu menu_1 = new Menu(mntmEdition_1);
		mntmEdition_1.setMenu(menu_1);
		
		MenuItem mntmInfosDuMoteur = new MenuItem(menu_1, SWT.NONE);
		mntmInfosDuMoteur.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openEngineEditor();
			}
		});
		mntmInfosDuMoteur.setText("Infos du moteur");
		
		MenuItem mntmTches = new MenuItem(menu, SWT.CASCADE);
		mntmTches.setText("T\u00E2ches");
		
		Menu menu_2 = new Menu(mntmTches);
		mntmTches.setMenu(menu_2);
		
		MenuItem mntmGestionnaireDeTche = new MenuItem(menu_2, SWT.NONE);
		mntmGestionnaireDeTche.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openTaskManager();
			}

			
		});
		mntmGestionnaireDeTche.setText("Gestionnaire de t\u00E2che");
		
		MenuItem mntmHistorique = new MenuItem(menu_2, SWT.NONE);
		mntmHistorique.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openHistoricUi();
			}
		});
		mntmHistorique.setText("Historique");
		
		
		MenuItem mntmAide = new MenuItem(menu, SWT.NONE);
		mntmAide.setText("Aide");

	}

	public void setModel(EngineMaintenance model) {
		if(model != null){
			_model = model; 
			_engineInfo.setModel(model.get_engine());
			_listChrono.setModel(model.getArrayListTask());
			_detailsTaskUi.setModel(null);
			
			_model.get_histo().addListenner(this);
			_model.get_engine().addListenner(this);
		}
		else{
			_engineInfo.setModel(null);
			_listChrono.setModel(null);
			_detailsTaskUi.setModel(null);
		}
		
	}
	
	public void addUIActionListener(UIActionListener listenner){
		if(!_listListener.contains(listenner))
			_listListener.add(listenner);
	}
	
	private void openTaskManager() {
		ListTaskUI listTaskUI = new ListTaskUI(this.shell, SWT.APPLICATION_MODAL);
		listTaskUI.setModel(_model);
		listTaskUI.open();
		setModel(_model);
		
	}
	
	private void openEngineEditor() {
		InfoMoteurUI infoEngineUi = new InfoMoteurUI(this.shell, SWT.APPLICATION_MODAL);
		infoEngineUi.setModel(_model.get_engine());
		infoEngineUi.open();
	}
	
	private void openHistoricUi() {
		HistoUI histoUi = new HistoUI(this.shell, SWT.APPLICATION_MODAL);
		histoUi.setModel(_model.get_engine(), _model.get_histo());
		histoUi.open();
	}
	
	private void onDispose() {
		if(!_model.hasBeenSaved()){
			MessageDialog dialog = new MessageDialog(shell, "Sauvegarde", null, "Voulez-vous sauver les modifications ?", MessageDialog.CONFIRM, 
					new String[] { "Oui", "Non" }, 0);
				int result = dialog.open();
				
				if(result == 0){
					_model.save();
				}	
		}
	}
	
	private void close(){
		shell.dispose();
		SWTResourceManager.dispose();
	}

	@Override
	public void onDataChanged(Object source, String param) {
		_display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if(param.compareTo("histo") == 0 || param.compareTo("age") == 0){
					_listChrono.setModel(_model.getArrayListTask());
				}
			}
		});
	}

}
