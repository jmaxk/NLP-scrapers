package max.nlp.scrape;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


public abstract class AbstractScraper {

	protected static Logger log = Logger.getLogger(AbstractScraper.class);

	protected ScrapingConfiguration config;
	protected String downloadDirectory;
	
	private static int BUFFER_SIZE= 2048;

	public enum WgetStatus {
		Success, MalformedUrl, IoException, UnableToCloseOutputStream;
	}

	public AbstractScraper() {
		config = ScrapingConfiguration.getInstance();
		downloadDirectory = config.getDownloadDirectory();
		BUFFER_SIZE = 2048;
		BasicConfigurator.configure();
	}

	public static WgetStatus wget(String saveAsFile, String urlOfFile) {
		InputStream httpIn = null;
		OutputStream fileOutput = null;
		OutputStream bufferedOut = null;
		try {
			// check the http connection before we do anything to the fs
			httpIn = new BufferedInputStream(new URL(urlOfFile).openStream());
			// prep saving the file
			fileOutput = new FileOutputStream(saveAsFile);
			bufferedOut = new BufferedOutputStream(fileOutput, 1024);
			byte data[] = new byte[1024];
			boolean fileComplete = false;
			int count = 0;
			while (!fileComplete) {
				count = httpIn.read(data, 0, 1024);
				if (count <= 0) {
					fileComplete = true;
				} else {
					bufferedOut.write(data, 0, count);
				}
			}
		} catch (MalformedURLException e) {
			return WgetStatus.MalformedUrl;
		} catch (IOException e) {
			return WgetStatus.IoException;
		} finally {
			try {
				bufferedOut.close();
				fileOutput.close();
				httpIn.close();
			} catch (IOException e) {
				return WgetStatus.UnableToCloseOutputStream;
			}
		}
		return WgetStatus.Success;
	}
	
	public static void uncompressTarGZ(String tarFileString, String destString) {
		
		try {
			File tarFile = new File(tarFileString);
			File dest = new File(destString);
			dest.mkdir();
			TarArchiveInputStream tarIn = null;

			tarIn = new TarArchiveInputStream(
			            new GzipCompressorInputStream(
			                new BufferedInputStream(
			                    new FileInputStream(
			                        tarFile
			                    )
			                )
			            )
			        );

			TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
			// tarIn is a TarArchiveInputStream
			while (tarEntry != null) {// create a file with the same name as the tarEntry
			    File destPath = new File(dest, tarEntry.getName());
			    System.out.println("working: " + destPath);
			    if (tarEntry.isDirectory()) {
			        destPath.mkdirs();
			    } else {
			        destPath.createNewFile();
			        //byte [] btoRead = new byte[(int)tarEntry.getSize()];
			        byte [] btoRead = new byte[1024];
			        //FileInputStream fin 
			        //  = new FileInputStream(destPath.getCanonicalPath());
			        BufferedOutputStream bout = 
			            new BufferedOutputStream(new FileOutputStream(destPath));
			        int len = 0;

			        while((len = tarIn.read(btoRead)) != -1)
			        {
			            bout.write(btoRead,0,len);
			        }

			        bout.close();
			        btoRead = null;

			    }
			    tarEntry = tarIn.getNextTarEntry();
			}
			tarIn.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public static void uncompressGzip(String gzipFileString, String destFileString) throws FileNotFoundException, IOException{
			FileInputStream fin = new FileInputStream(gzipFileString);
			BufferedInputStream in = new BufferedInputStream(fin);
			FileOutputStream out = new FileOutputStream(destFileString);
			GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
			final byte[] buffer = new byte[BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = gzIn.read(buffer))) {
			    out.write(buffer, 0, n);
			}
			out.close();
			gzIn.close();

	}
	
	
	public static void uncompressBz2(String bzipFileString, String destFileString) throws FileNotFoundException, IOException{
		FileInputStream fin = new FileInputStream(bzipFileString);
		BufferedInputStream in = new BufferedInputStream(fin);
		FileOutputStream out = new FileOutputStream(destFileString);
		BZip2CompressorInputStream  bz2In = new BZip2CompressorInputStream(in);
		final byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;
		while (-1 != (n = bz2In.read(buffer))) {
		    out.write(buffer, 0, n);
		}
		out.close();
		bz2In.close();

}
	
	public static void uncompressZip(String zipFileString,String destDirString) 
	{
	  
	        try {
				File file = new File(zipFileString);

				ZipFile zip = new ZipFile(file);
				String newPath = destDirString;

				new File(newPath).mkdir();
				Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

				// Process each entry
				while (zipFileEntries.hasMoreElements())
				{
				    // grab a zip file entry
				    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				    String currentEntry = entry.getName();

				    File destFile = new File(newPath, currentEntry);
				    //destFile = new File(newPath, destFile.getName());
				    File destinationParent = destFile.getParentFile();

				    // create the parent directory structure if needed
				    destinationParent.mkdirs();

				    if (!entry.isDirectory())
				    {
				        BufferedInputStream is = new BufferedInputStream(zip
				        .getInputStream(entry));
				        int currentByte;
				        // establish buffer for writing file
				        byte data[] = new byte[BUFFER_SIZE];

				        // write the current file to disk
				        FileOutputStream fos = new FileOutputStream(destFile);
				        BufferedOutputStream dest = new BufferedOutputStream(fos,
				        		BUFFER_SIZE);

				        // read and write until last byte is encountered
				        while ((currentByte = is.read(data, 0, BUFFER_SIZE)) != -1) {
				            dest.write(data, 0, currentByte);
				        }
				        zip.close();
				        dest.flush();
				        dest.close();
				        is.close();
				    }


				}
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


	}
}
