package ecogium.tools.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

//import com.enterprisedt.net.ftp.FileTransferClient;

public class FileUtilities {

	private final static Logger _logger = Logger.getLogger(FileUtilities.class);
	private final static SimpleDateFormat _dateFormat = new SimpleDateFormat("yy_M_d-h_mm_ss-a");
	public final static String _separator = System.getProperty("file.separator");

	public static long getLastModified(URL u, Proxy proxy) throws IOException{
		URLConnection  uc = null;
		if(proxy != null){
			uc = u.openConnection(proxy);
		}
		else{
			uc = u.openConnection();
		}

		uc.setUseCaches(false);
		uc.connect();

		long lastModified = uc.getLastModified();
		
		if(uc instanceof HttpURLConnection){
			((HttpURLConnection)uc).disconnect();
		}

		if(lastModified == 0)
		{
			lastModified = System.currentTimeMillis();
		}

		return lastModified;
	}

	public static long getLastModified(URL url){
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			_logger.error(e.getMessage(), e);
		}

		while(!file.canRead()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				_logger.error(e.getMessage(), e);
			}
		}

		return file.lastModified();
	}


	/**
	 * Permet de récupérer un fichier à partir d'une URL
	 * @param u URL du fichier à récupérer
	 * @param proxy Paramètres du proxy à utiliser
	 * @param directory Répertoire dans lequel télécharger le fichier
	 * @return Le nom du fichier sur le disque
	 * @throws IOException
	 */
	public static String downloadFile(URL u, Proxy proxy, String directory) throws IOException {
		String fileName = u.getFile();
		fileName = directory + _separator + fileName.substring(fileName.lastIndexOf('/') + 1);

		return downloadFileTo(u, proxy, fileName);
	}
	
	/**
	 * Permet de récupérer un fichier à partir d'une URL
	 * @param u URL du fichier à récupérer
	 * @param proxy Paramètres du proxy à utiliser
	 * @param absoluteFilename Chemin absolue du fichier à télécharger
	 * @return Le nom absolue du fichier sur le disque
	 * @throws IOException
	 */
	public static String downloadFileTo(URL u, Proxy proxy, String absoluteFilename) throws IOException {
		HttpURLConnection  uc = null;
		if(proxy != null){

			uc = (HttpURLConnection )u.openConnection(proxy);
		}
		else{
			uc = (HttpURLConnection )u.openConnection();
		}

		uc.setRequestProperty("content-coding", "compress");
		uc.setUseCaches(false);
		uc.connect();

		String fileType = uc.getContentType();
		_logger.debug("nom du fichier : " + u.toString());
		_logger.debug("fichier : "+fileType);

		String directory = absoluteFilename.substring(0, absoluteFilename.lastIndexOf(_separator));

		File dir = new File(directory);
		if(!dir.exists()?dir.mkdirs():true){
			
			InputStream inputStr = null;
			BufferedOutputStream bos = null;
			try
			{
				inputStr = uc.getInputStream();
				bos = new BufferedOutputStream( new FileOutputStream(absoluteFilename) );

				int i;
				while ((i = inputStr.read()) != -1)
				{
					bos.write( i );
				}
			}
			finally
			{
				if (inputStr != null){
					try
					{
						inputStr.close();
					}
					catch (IOException ioe)
					{
						_logger.error(ioe.getMessage(), ioe);
					}
				}
				if (bos != null)
				{
					try
					{
							bos.close();
					}
					catch (IOException ioe)
					{
						_logger.error(ioe.getMessage(), ioe);
					}
				}
			}
		}
		else{
			_logger.error("Erreur lors de la création du répertoire de téléchargement " + directory);
		}

		uc.disconnect();

		return absoluteFilename;
	}


	private static boolean deleteEmptyDirOrFile(File file){
		if(!file.exists()){
			_logger.error("Le fichier à supprimer n'existe pas.");
			return true;
		}

		int nbTry = 10;
		int i = 0;
		while(!file.delete() && i++ < nbTry){
			try {
				Thread.currentThread().wait(100);
			} catch (Exception e) {
				_logger.error(e.getMessage(), e);
			}
		}
		if(i >= nbTry){
			_logger.error("Le fichier " + file.getAbsolutePath() + " n'a pas pu être supprimé.");
			return false;
		}
		else{
			return true;
		}
	}

	public static boolean deleteDirectory(File path) {
		boolean resultat = true;

		if( path.exists() ) {
			if(path.isDirectory()){
				File[] files = path.listFiles();

				for(File file : files) {
					if(file.isDirectory()) {
						resultat &= deleteDirectory(file);
					}
					else {
						resultat &= deleteEmptyDirOrFile(file);
					}
				}
			}

			if(resultat){
				resultat &= deleteEmptyDirOrFile(path);
			}

			return resultat;
		}
		else{
			_logger.error("Le fichier à supprimer n'existe pas : " + path.getAbsolutePath());
			return true;
		}

	}
	
	/**
	 * Fonction qui permet de dézipper une archive ne contenant qu'un seul fichier.
	 * @param filename Archive à dézipper
	 * @param directory Répertoire dans lequel dézipper l'archive
	 * @param errorDirectory Répertoire d'erreur
	 * @return Un booléen indiquant si une erreur s'est produite ...
	 */
	public static boolean unGZip(String filename, String directory, String errorDirectory){
		boolean success = true;
		//Si le répertoire n'existe pas, on le crée
		File dir = new File(directory);
		boolean dirExists = dir.exists();
		if(!dirExists)
		{
			dirExists = dir.mkdirs();
		}

		if(dirExists){
			//Et on le dézippe à l'intérieur de ce répertoire.
			BufferedOutputStream dest = null;
			FileInputStream fis = null;
			BufferedInputStream buffi = null;
			GZIPInputStream gzis = null;
			try {
				//Et on le dézippe à l'intérieur de ce répertoire.
				fis = new FileInputStream(filename);
				buffi = new BufferedInputStream(fis);
				gzis = new GZIPInputStream(buffi);

				int count = 0;
				byte data[] = new byte[2048];

				if(gzis.available() == 1){
					int firstIndex = filename.lastIndexOf(FileUtilities._separator) != -1 ? filename.lastIndexOf(FileUtilities._separator):0;
					String newFilename = filename.substring(firstIndex, filename.lastIndexOf('.'));

					FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + FileUtilities._separator + newFilename);

					dest = new BufferedOutputStream(fos, 2048);
					while ((count = gzis.read(data, 0, 2048)) != -1) 
					{
						dest.write(data, 0, count);
					}
				}

				success = true;
			} 
			catch (Exception e) {
				_logger.fatal(e.getMessage(), e);
				success = false;
			}

			try{
				if(dest != null){
					dest.flush();
					dest.close();
				}
			}
			catch(IOException ex){
				_logger.fatal(ex.getMessage(), ex);
			}
			try{
				if(fis != null){
					fis.close();
				}
			}
			catch(IOException ex){
				_logger.fatal(ex.getMessage(), ex);
			}
			try{
				if(buffi != null){
					buffi.close();
				}
			}
			catch(IOException ex){
				_logger.fatal(ex.getMessage(), ex);
			}

			try{
				if(gzis != null){
					gzis.close();
				}
			}
			catch(IOException ex){
				_logger.fatal(ex.getMessage(), ex);
			}

			if(success == false && !errorDirectory.isEmpty()){
				FileUtilities.moveFile(filename, errorDirectory);
			}
		}
		else{
			_logger.error("Le répertoire pour le dézippage n'a pas pu être créé : " + directory);
			success = false;
		}

		return success;
	}

	public static boolean GZip(String absoluteFilename, String errorDir) {
		File file = new File(absoluteFilename);
		if(!file.exists())
		{
			_logger.error("Le fichier à zipper est inexistant : " + file);
			return false;
		}

		FileOutputStream outputStr = null;
		GZIPOutputStream out = null;

		FileInputStream in = null;
		try {
			// Create the GZIP output stream
			String outFilenameTemp = absoluteFilename + ".gz.temp";
			outputStr = new FileOutputStream(outFilenameTemp);
			out = new GZIPOutputStream(outputStr);

			in = new FileInputStream(absoluteFilename);

			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			in = null;

			// Complete the GZIP file
			out.finish();
			out.close();
			out = null;

			outputStr.flush();
			outputStr.close();
			outputStr = null;

			File fileTemp = new File(outFilenameTemp);
			String outFilename = absoluteFilename + ".gz";
			File finalFile = new File(outFilename);
			if(finalFile.exists()){
				finalFile.delete();
			}
			fileTemp.renameTo(finalFile);

			return true;
		} 
		catch (IOException e) {
			if(in != null){
				try {
					in.close();
					in = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			if(out != null){
				try {
					out.finish();
					out.close();
					out = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			if(outputStr != null){
				try {
					outputStr.flush();
					outputStr.close();
					outputStr = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			FileUtilities.moveFile(absoluteFilename, errorDir);
			_logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public static File copyFile(File source, File destination){
		try {
			if(destination.createNewFile()){
				FileInputStream sourceFile = new FileInputStream(source);
				FileOutputStream destinationFile = new FileOutputStream(destination);
				// Lecture par segment de 0.5Mo
				byte buffer[]=new byte[512*1024];
				int nbLecture;
				while( (nbLecture = sourceFile.read(buffer)) != -1 ) 
				{
					destinationFile.write(buffer, 0, nbLecture); 
				}
				sourceFile.close();
				destinationFile.close();

				return destination;
			}
			else{
				_logger.error("Impossible de créer le fichier : " + destination);
				return null;
			}
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static File moveFile(String absoluteFilename, String toDirectory) {
		Date dateTime = new Date(System.currentTimeMillis());
		String newDir = _dateFormat.format(dateTime);

		if(!toDirectory.endsWith(FileUtilities._separator))
		{
			toDirectory += FileUtilities._separator + newDir;
		}
		else{
			toDirectory += newDir;
		}

		File dirDest = new File(toDirectory);
		boolean dirDestExists = dirDest.exists();
		if(!dirDestExists){
			dirDestExists = dirDest.mkdirs();
		}

		if(dirDestExists){
			//Retourne le dernier index du separateur + 1 ou 0 si il n'y a pas de separateur
			int firstIndex = absoluteFilename.lastIndexOf(FileUtilities._separator) != -1 ? absoluteFilename.lastIndexOf(FileUtilities._separator) + 1:0;
			//Retourne le nom du fichier sans separateur au debut
			String filename = absoluteFilename.substring(firstIndex, absoluteFilename.length());

			File source = new File(absoluteFilename);
			File destination = new File(toDirectory + FileUtilities._separator + filename);
			try {
				if(destination.createNewFile()){
					FileInputStream sourceFile = new FileInputStream(source);
					FileOutputStream destinationFile = new FileOutputStream(destination);
					// Lecture par segment de 0.5Mo
					byte buffer[]=new byte[512*1024];
					int nbLecture;
					while( (nbLecture = sourceFile.read(buffer)) != -1 ) 
					{
						destinationFile.write(buffer, 0, nbLecture); 
					}
					sourceFile.close();
					destinationFile.close();

					return destination;
				}
				else{
					_logger.error("Impossible de créer le fichier : " + destination);
					return null;
				}
			} catch (Exception e) {
				_logger.error(e.getMessage(), e);
				return null;
			}
		}
		else{
			_logger.error("Le répertoire de destination n'a pas pu être créé.\nRépertoire de destination : " + toDirectory);
			return null;
		}
	}

	public static void moveFile(String strFile, String strToDirectory, String strFileName) {

		//Si le répertoire de destination ne se termine pas par un separateur, on le rajoute
		if(!strToDirectory.endsWith(FileUtilities._separator))
		{
			strToDirectory += FileUtilities._separator;
		}

		//Création du repertoire de destination
		File dirDest = new File(strToDirectory);
		boolean dirDestExists = dirDest.exists();
		if(!dirDestExists){
			dirDestExists = dirDest.mkdirs();
		}

		if(dirDestExists){
			//Récuperation du nom du fichier à copier
			int firstIndex = strFile.lastIndexOf(_separator) != -1 ? strFile.lastIndexOf(_separator) + 1:0;
			String filename = strFile.substring(firstIndex, strFile.length());


			File source = new File(strFile);
			File destination = new File(strToDirectory + filename);
			FileInputStream sourceFile = null;
			FileOutputStream destinationFile = null;
			try {
				//Création du fichier à copier ...
				if(destination.createNewFile()){
					sourceFile = new FileInputStream(source);
					destinationFile = new FileOutputStream(destination);
					// Lecture par segment de 0.5Mo
					byte buffer[]=new byte[512*1024];
					int nbLecture;
					while( (nbLecture = sourceFile.read(buffer)) != -1 ) 
					{
						destinationFile.write(buffer, 0, nbLecture); 
					}
					sourceFile.close();
					destinationFile.close();


					File newFile = new File(strToDirectory + strFileName);
					if(newFile.exists()?newFile.delete():true){
						if(!destination.renameTo(newFile)){
							_logger.error("Le fichier " + destination.getAbsolutePath() + " n'a pas été correctement renommé en " + newFile.getAbsolutePath());
						}
					}
					else{
						_logger.error("Le fichier " + newFile.getAbsolutePath() + " n'a pas été correctement supprimé.");
					}

				}
				else{
					_logger.error("Impossible de créer le fichier : " + destination);
				}
			} 
			catch (Exception e) {
				_logger.error(e.getMessage(), e);
			}
			finally{
				if(sourceFile != null){
					try {
						sourceFile.close();
					} catch (IOException e) {
						_logger.error(e.getMessage(), e);
					}
				}
				if(destinationFile != null){
					try {
						destinationFile.close();
					} catch (IOException e) {
						_logger.error(e.getMessage(), e);
					}
				}
			}
		}
		else{
			_logger.error("Le répertoire de destination n'a pas pu être créé : " + strToDirectory);
		}
	}

	/**
	 * Upload a file to a FTP server. A FTP URL is generated with the
	 * following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer , FTP server address (optional port ':portNumber').
	 * @param user , Optional user name to login.
	 * @param password , Optional password for user.
	 * @param fileName , Destination file name on FTP server (with optional
	 *            preceding relative path, e.g. "myDir/myFile.txt").
	 * @param source , Source file to upload.
	 * @throws MalformedURLException, IOException on error.
	 */
	public static void upload( Proxy proxy, URL url, File source ) throws MalformedURLException, IOException
	{
		if (source != null)
		{
			
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try
			{
				URLConnection urlc = null;
				if(proxy != null){
					urlc = url.openConnection(proxy);
				}
				else{
					urlc = url.openConnection();
				}

				bos = new BufferedOutputStream( urlc.getOutputStream() );
				bis = new BufferedInputStream( new FileInputStream( source ) );

				int i;
				// read byte by byte until end of stream
				while ((i = bis.read()) != -1)
				{
					bos.write( i );
				}
			}
			finally
			{
				if (bis != null)
					try
				{
						bis.close();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
				if (bos != null)
					try
				{
						bos.close();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println( "Input not available." );
		}
	}
	
	/**
	 * Upload a file to a FTP server. A FTP URL is generated with the
	 * following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer , FTP server address (optional port ':portNumber').
	 * @param user , Optional user name to login.
	 * @param password , Optional password for user.
	 * @param fileName , Destination file name on FTP server (with optional
	 *            preceding relative path, e.g. "myDir/myFile.txt").
	 * @param source , Source file to upload.
	 * @throws MalformedURLException, IOException on error.
	 */
	public static void upload( Proxy proxy, String ftpServer, String user, String password,
			String fileName, File source ) throws MalformedURLException,
			IOException
	{
		if (ftpServer != null && fileName != null && source != null)
		{
			StringBuffer sb = new StringBuffer( "ftp://" );
			// check for authentication else assume its anonymous access.
			if (user != null && password != null)
			{
				sb.append( user );
				sb.append( ':' );
				sb.append( password );
				sb.append( '@' );
			}
			sb.append( ftpServer );
			sb.append( '/' );
			sb.append( fileName );
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append( ";type=i" );

			URL url = new URL( sb.toString());
			
			upload(proxy, url, source);
		}
		else
		{
			System.out.println( "Input not available." );
		}
	}

	/**
	 * Download a file from a FTP server. A FTP URL is generated with the
	 * following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer , FTP server address (optional port ':portNumber').
	 * @param user , Optional user name to login.
	 * @param password , Optional password for user.
	 * @param fileName , Name of file to download (with optional preceeding
	 *            relative path, e.g. one/two/three.txt).
	 * @param destination , Destination file to save.
	 * @throws MalformedURLException, IOException on error.
	 */
	public static void download(Proxy proxy, URL url, File destination ) throws MalformedURLException,
			IOException
			{
		if (destination != null)
		{
			//BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			URLConnection urlc = null;
			InputStream inputStr = null;
			try
			{
				if(proxy != null){
					urlc = url.openConnection(proxy);
				}
				else{
					urlc = url.openConnection();
				}
//				urlc.setDoInput(true);
				urlc.setUseCaches(false);
//				urlc.connect();
				
				inputStr = urlc.getInputStream();
				bos = new BufferedOutputStream( new FileOutputStream(destination) );
			
				int i;
				while ((i = inputStr.read()) != -1)
				{
					bos.write( i );
				}
			}
			finally
			{
				
				if (inputStr != null){
					try
					{
						inputStr.close();
						inputStr = null;
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
				if (bos != null){
					try
					{
						bos.close();
						bos = null;
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
		}
		else
		{
			System.out.println( "Input not available" );
		}
	}
	
	/**
	 * Download a file from a FTP server. A FTP URL is generated with the
	 * following syntax:
	 * ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer , FTP server address (optional port ':portNumber').
	 * @param user , Optional user name to login.
	 * @param password , Optional password for user.
	 * @param fileName , Name of file to download (with optional preceeding
	 *            relative path, e.g. one/two/three.txt).
	 * @param destination , Destination file to save.
	 * @throws MalformedURLException, IOException on error.
	 */
	public static void download(Proxy proxy,  String ftpServer, String user, String password,
			String fileName, File destination ) throws MalformedURLException,
			IOException
			{
		if (ftpServer != null && fileName != null && destination != null)
		{
			StringBuffer sb = new StringBuffer( "ftp://" );
			// check for authentication else assume its anonymous access.
			if (user != null && password != null)
			{
				sb.append( user );
				sb.append( ':' );
				sb.append( password );
				sb.append( '@' );
			}
			sb.append( ftpServer );
			sb.append( '/' );
			sb.append( fileName );
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append( ";type=i" );
			
			URL url = new URL( sb.toString() );
			
			download(proxy, url, destination);
		}
		else
		{
			System.out.println( "Input not available" );
		}
	}

	public static boolean unGZip(File archiveTemp, String dirDestStr, String errorDirectory) {
		return unGZip(archiveTemp.getAbsolutePath(), dirDestStr, errorDirectory);
	}

	public static boolean Zip(String absoluteZipFilename, String errorDir,	String[] listFilename, boolean removeFileList) {
		FileOutputStream outputStr = null;
		ZipOutputStream out = null;

		FileInputStream in = null;
		try {
			// Create the GZIP output stream
			String outFilenameTemp = absoluteZipFilename + ".temp";
			outputStr = new FileOutputStream(outFilenameTemp);
			CheckedOutputStream checksum = new CheckedOutputStream(outputStr, new Adler32());
			out = new ZipOutputStream(new BufferedOutputStream(checksum));
			
			for(String filename : listFilename){
				FileInputStream fis = new FileInputStream(filename);
		        int size = 0;
		        byte[] buffer = new byte[1024];
	
		        //Ajouter une entree à l'archive zip
		        File file = new File(filename);
		        ZipEntry zipEntry = new ZipEntry(file.getName());
		        out.putNextEntry(zipEntry);
		        
		        //copier et compresser les données
		        while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
		        	out.write(buffer, 0, size);
		        }
	
		        out.closeEntry();
		        fis.close();
		        
		        if(removeFileList){
		        	file.delete();
				}
			}
			out.close();
			outputStr.flush();
			outputStr.close();
			outputStr = null;

			File fileTemp = new File(outFilenameTemp);
			String outFilename = absoluteZipFilename;
			File finalFile = new File(outFilename);
			if(finalFile.exists()){
				finalFile.delete();
			}
			fileTemp.renameTo(finalFile);

			return true;
		} 
		catch (IOException e) {
			if(in != null){
				try {
					in.close();
					in = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			if(out != null){
				try {
					out.finish();
					out.close();
					out = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			if(outputStr != null){
				try {
					outputStr.flush();
					outputStr.close();
					outputStr = null;
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}

			FileUtilities.moveFile(absoluteZipFilename, errorDir);
			_logger.error(e.getMessage(), e);
			return false;
		}
	}

	public static InputStream getInputStreamFromGzipFile(File file) throws Exception{
		if(file == null){
			throw new Exception("Le fichier d'entrée est null.");
		}
		else{
			//Et on le dézippe à l'intérieur de ce répertoire.
			FileInputStream fis = new FileInputStream(file);
			//BufferedInputStream buffi = new BufferedInputStream(fis);
			return new GZIPInputStream(fis);
		}
	}

}
