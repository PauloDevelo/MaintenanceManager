package enginemonitor.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import enginemonitor.data.Engine;
import enginemonitor.data.IDataListener;

public class EngineInfoUI extends Composite implements IDataListener {
	
	private static final Logger _logger = Logger.getLogger(EngineInfoUI.class);

	private Engine _engine = null;
	
	private Label lblBrand;
	private Label lblDate;
	private Label lblImage;
	
	private final SimpleDateFormat _df = new SimpleDateFormat("dd/MM/yyyy");
	private Label lblInstalle;
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public EngineInfoUI(Composite parent, int style) {
		super(parent, style);
		
		setSize(new Point(300, 120));
		setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite compositeDate = new Composite(this, SWT.NONE);
		RowLayout rl_compositeDate = new RowLayout(SWT.HORIZONTAL);
		rl_compositeDate.center = true;
		compositeDate.setLayout(rl_compositeDate);
		
		lblDate = new Label(compositeDate, SWT.NONE);
		lblDate.setText("Aujourd'hui 01/08/2013");
		
		lblImage = new Label(compositeDate, SWT.NONE);
		lblImage.setAlignment(SWT.CENTER);
		lblImage.setImage(SWTResourceManager.getImage(EngineInfoUI.class, "/enginemonitor/ui/images/bt_off_24x24.gif"));
		
		Composite compositeEngineInfo = new Composite(this, SWT.NONE);
		compositeEngineInfo.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lblBrand = new Label(compositeEngineInfo, SWT.NONE);
		lblBrand.setText("Marque type age");
		
		lblInstalle = new Label(compositeEngineInfo, SWT.NONE);
		lblInstalle.setText("Installe le ");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setModel(Engine engine) {
		if(engine != null){
			engine.removeListener(this);
		}
		_engine = engine;
		_engine.addListenner(this);
		
		Date now = new Date(System.currentTimeMillis());
		lblDate.setText("Aujourd'hui " + _df.format(now) + "    ");
		
		if(engine != null){
			lblBrand.setText(_engine.get_brand() + " " + _engine.get_type() + " " + Integer.toString((int)(_engine.get_age() + (float)0.5)) + " h");
			
			lblInstalle.setText("Installé le " + _df.format(_engine.get_installation().getTime()));
			if(_engine.is_hasBeenUpdated()){
				lblImage.setImage(SWTResourceManager.getImage(EngineInfoUI.class, "/enginemonitor/ui/images/bt_on_24x24.gif"));
			}
			else{
				lblImage.setImage(SWTResourceManager.getImage(EngineInfoUI.class, "/enginemonitor/ui/images/bt_off_24x24.gif"));
			}
		}
		else{
			lblBrand.setText("No brand No type NA");
			lblInstalle.setText("Installé le NA");
			lblImage.setImage(SWTResourceManager.getImage(EngineInfoUI.class, "/enginemonitor/ui/images/bt_off_24x24.gif"));
		}
	}

	@Override
	public synchronized void onDataChanged(Object source, String param) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if(source == _engine){
					setModel(_engine);
				}
			}
		});
		
	}

}
