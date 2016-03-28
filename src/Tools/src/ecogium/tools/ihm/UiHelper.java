package ecogium.tools.ihm;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class UiHelper {
	public static void execCodeInIHMThread(Runnable code) {
		if (SwingUtilities.isEventDispatchThread()) {
			  code.run();
		  } else {
			  SwingUtilities.invokeLater(code);
		  }
	}

	public static void InvokeAndWait(Runnable code) {
		try {
			SwingUtilities.invokeAndWait(code);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
