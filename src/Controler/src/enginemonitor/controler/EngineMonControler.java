package enginemonitor.controler;

import java.io.File;
import org.apache.log4j.Logger;
import enginemonitor.data.EngineMaintenance;
import enginemonitor.ui.interfaces.UIActionListener;

public class EngineMonControler implements UIActionListener {
	
	private static Logger _logger = Logger.getLogger(EngineMonControler.class);
	
	private EngineMaintenance _model = null;

	public EngineMonControler(EngineMaintenance model) {
		_model = model;
	}

	@Override
	public boolean saveModel(String absolutePath) {
		return _model.saveInXMLFile(new File(absolutePath));
	}

	@Override
	public boolean exportPDF(String absolutePath) {
		return createPdf(absolutePath);
	}
	
	private boolean createPdf(String absolutePath) {
		return false;
	}

}
