package edu.vt.tlos.service;

//Import all needed packages
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {

	
	private static final Logger logger = LoggerFactory.getLogger("edu.vt.tlos.service.ZipUtils");

	public static void createZip(String tempDirectoryDir, String archiveZipDirectory, String archiveZipFilename) throws IOException {
		
		logger.info("Creating Zip File: " + archiveZipDirectory + archiveZipFilename);
		logger.info("From: " + tempDirectoryDir);
		
		File archiveDirectory = new File(archiveZipDirectory);
		if (!archiveDirectory.exists()) {
			archiveDirectory.mkdirs();
		}
		
		File archiveFile = new File(archiveZipDirectory + archiveZipFilename);
		
		//Replace file
		if (archiveFile.exists()) {
			archiveFile.delete();
		}
		
		FileOutputStream fos = new FileOutputStream (archiveFile); 
	    
		File directory = new File(tempDirectoryDir);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		CheckedOutputStream checksum = null;
		ZipOutputStream zos = null;
		try {
			checksum = new CheckedOutputStream(out, new Adler32());
			zos = new ZipOutputStream(new BufferedOutputStream(checksum));
			recurseDirectory("", directory, zos);

			zos.finish();
			zos.flush();
			out.writeTo(fos);
			
		} finally {
			if (zos != null) {
				try {
					logger.info("Archive created: " + archiveFile);
					zos.close();
				} catch (IOException e) {
				}
			}
			if (checksum != null) {
				try {
					checksum.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	protected static void recurseDirectory(String parentPath, File directory,
			ZipOutputStream zos) throws IOException {
		// get all files... go through those
		File[] files = directory.listFiles(new DirectoryFileFilter(false));

		if (files == null)
			throw new NullPointerException(
					"recursing through a directory which is not a directory: "
							+ parentPath + " ---- " + directory);

		logger.debug("Parent Path: " + parentPath);

		addFiles(zos, parentPath, files);

		// get all directories... go through those...
		File[] directories = directory.listFiles(new DirectoryFileFilter(true));
		for (int i = 0; i < directories.length; i++) {
			recurseDirectory(parentPath + directories[i].getName() + "/",
					directories[i], zos);
		}

	}
	
	private static class DirectoryFileFilter implements FileFilter {
		private boolean directories = false;

		public DirectoryFileFilter(boolean directories) {
			this.directories = directories;
		}

		/**
		 * Tests whether or not the specified abstract pathname should be
		 * included in a pathname list.
		 * 
		 * @param pathname
		 *            The abstract pathname to be tested
		 * @return <code>true</code> if and only if <code>pathname</code> should
		 *         be included
		 */
		public boolean accept(File pathname) {
			if (directories) {
				return pathname.isDirectory();
			} else {
				return pathname.isFile();
			}
		}

	}

	protected static void addFiles(ZipOutputStream out, String parentPrefix,
			File[] files) throws IOException {

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			logger.debug("addFiles: " + file.getName());

			String fileName = URLDecoder.decode(parentPrefix + file.getName());

			if (fileName == null)
				throw new NullPointerException();

			logger.debug("Adding " + fileName + " - bytes: " + file.length());

			BufferedInputStream origin = null;
			InputStream in = null;
			// byte data[] = new byte[BUFFER];

			try {
				in = new FileInputStream(files[i]);
				origin = new BufferedInputStream(in);

				ZipEntry entry = new ZipEntry(fileName);
				out.putNextEntry(entry);

				int bytes_read;
				while ((bytes_read = origin.read()) != -1) {
					out.write(bytes_read);
				}
				out.closeEntry();

			} finally {
				try {
					if (origin != null) {
						origin.close();
					}
				} catch (Exception e) {
					logger.warn("Error cleaning up resource: ", e);
				}
				try {
					in.close();
				} catch (Exception e) {
					logger.warn("Error cleaning up resource: ", e);
				}
			}
		}
	}
	
	public static void deleteTemp(String tempDirectory) {
		File temp = new File(tempDirectory);

		 deleteContent(temp);
		 temp.delete();
	}

	private static void deleteContent(File directory) {
		File[] files = directory.listFiles(new DirectoryFileFilter(false));

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}

		// get all directories... go through those...
		File[] directories = directory.listFiles(new DirectoryFileFilter(true));
		if (directories != null) {
			for (int i = 0; i < directories.length; i++) {
				deleteContent(directories[i]);
				directories[i].delete();
			}
		}
	}
}