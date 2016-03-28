package ecogium.tools.exec;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

public class LaunchExec extends Thread {
	
	private static Logger _logger = Logger.getLogger(LaunchExec.class);
	
    InputStream is;
    String type;
    
    LaunchExec(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }
    
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String ligne=null;
            while ( (ligne = br.readLine()) != null){
            	if(type.compareTo("stderr") == 0){
            		_logger.error(type + "> " + ligne);
            	}
            	else{
            		_logger.debug(type + "> " + ligne);
            	}
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static void launch(String command) throws Throwable {
        //String command = "cmd;/d;Extracttgz.bat 20111205_FCD_trafficstate_tmc.tgz 20111205_FCD_trafficstate_tmc.tar";
    	String[] args = command.split(";");
        
        _logger.debug("\nSortie de la commande " + Arrays.toString(args) + ": \n");
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(args);
        
        LaunchExec stderr = new LaunchExec(process.getErrorStream(), "stderr");
        
        LaunchExec stdout = new LaunchExec(process.getInputStream(), "stdout");
        
        stderr.start();
        stdout.start();
        int status = process.waitFor();
        _logger.debug("Valeur de retour du sous proc: " + status);
    }
}



