package enginemonitor.controler;

import org.apache.log4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import enginemonitor.data.Engine;
import enginemonitor.data.IDataListener;

public class BTReaderMonEngine implements IDataListener, Runnable {
	private static Logger _logger = Logger.getLogger(BTReaderMonEngine.class);
	
	
	private Engine _engineModel = null;
	
	public BTReaderMonEngine(Engine engine){
		_engineModel = engine;
		
		_engineModel.addListenner(this);
		
		updateAgeEngine();
	}
	
	private synchronized void updateAgeEngine(){
		if(!_engineModel.get_portName().isEmpty()){
			SerialPort engineMonPort = new SerialPort(_engineModel.get_portName());
			try {
				if(engineMonPort.openPort() == true){
					if(engineMonPort.setParams(SerialPort.BAUDRATE_4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE) == true){
						
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							_logger.error(e.getMessage(), e);
						}
						
						String sentence = engineMonPort.readString();
						if(sentence != null){
							int iBegin = sentence.indexOf("$ENGIN");
							if(iBegin != -1){
								sentence = sentence.substring(iBegin);
								String[] fields = sentence.split(",");
								float newAge = Float.parseFloat(fields[2])/(float)3600;
								_engineModel.set_age(newAge);
								_engineModel.set_hasBeenUpdated(true);
							}
							else{
								_engineModel.set_hasBeenUpdated(false);
							}
						}
						else{
							_engineModel.set_hasBeenUpdated(false);
						}
						if(engineMonPort.closePort() == false){
							_logger.error("Impossible de fermer le port COM4");
						}
					}
					else{
						_engineModel.set_hasBeenUpdated(false);
						_logger.error("Impossible de paramétrer le port COM4");
					}
				}
				else{
					_engineModel.set_hasBeenUpdated(false);
					_logger.error("Impossible d'ouvrir le port COM4");
				}
			} catch (SerialPortException e) {
				_engineModel.set_hasBeenUpdated(false);
				_logger.error(e.getMessage());
			}
			
		}
	}

	@Override
	public synchronized void onDataChanged(Object source, String param) {
		if(param.compareTo("portName") == 0){
			updateAgeEngine();
		}
	}

	@Override
	public void run() {
		try{
			while(_engineModel.is_hasBeenUpdated() == false){
				try {
					Thread.sleep(5000);
					updateAgeEngine();
				} catch (InterruptedException e) {
					_logger.error(e.getMessage(), e);
				}
			}
		}
		catch(ThreadDeath e){
			_logger.debug("Tchao");
		}
	}
}
