package edu.vt.tlos.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.vt.tlos.domain.ContentResource;
import edu.vt.tlos.domain.User;

public class Utilities {
	
	
	private static final String FORM_HELPER_EXTENSION = ".xml";
	private static final Logger logger = LoggerFactory.getLogger("edu.vt.tlos.service.Utilities");

	public static User findUser(List<User> users, String userId) {
		if (users != null) {
			for (User user: users) {
				if (user.userId.equals(userId)) {
					return user;
				}
			}
		}
		return null;
	}
	
	public static void clearFile(String filePath) {
		File file = new File(filePath);
		try{
			if (!file.exists()) {
				logger.debug("creating file: " + file.getPath());
		        file.createNewFile();
			} else {
				logger.debug("clear file: " + file.getPath());
				FileOutputStream writer = new FileOutputStream(filePath);
	            writer.write(("").getBytes());
	            writer.close();
			}
		} catch(SecurityException se){
	    	logger.error("Unable to create file: " + file + " " + se.getMessage());
	    } catch (IOException ioe) {
			 logger.error("Unable to create file: " + file + " " + ioe.getMessage());
		}
		
	}
	
	public static List<String> readFile(String filePath)  {
		List<String> lines = new ArrayList<String>();
		
		try {
			if (filePath == null)
				return lines;
						
			File file = new File(filePath);

			if (file.exists()) {
				logger.debug("File: " + filePath + " exists. Start reading content...");

				BufferedReader br = new BufferedReader(new FileReader(filePath));
					
				try {
				    String line = br.readLine();
				
				    while (line != null) {
				        if (!line.isEmpty())
				        	lines.add(line);
				        line = br.readLine();
				    }
				} catch (IOException ioe) {
					 logger.error("Unable read file: " + file + " " + ioe.getMessage());
				} finally {
				    br.close();
				}
			} else {
				logger.debug("creating file: " + file.getPath());
			    try{
			        file.createNewFile();
			    } 
			    catch(SecurityException se){
			    	logger.error("Unable to create file: " + file + " " + se.getMessage());
			    } catch (IOException ioe) {
					 logger.error("Unable to create file: " + file + " " + ioe.getMessage());
				}   
			}
		} catch (Exception e) {
			logger.error("Unable to process file: " + filePath);
		}
		
		
		return lines;
	}
	
	public static boolean createDirectories(String dir) {
		if (dir == null)
			return false;
					
		File theDir = new File(dir);

		if (!theDir.exists()) {
			logger.info("Creating directory: " + theDir.getPath());
		    try{
		        return theDir.mkdirs();
		    } 
		    catch(SecurityException se){
		    	logger.error("Unable to create directory: " + dir);
		    	return false;
		    }        
		} else {
			logger.debug("Directory: " + theDir.getAbsolutePath() + "/" + " already exists.");
			return true;
		}
		
	}
	
	public static boolean directoryExist(String dir) {
		File theDir = new File(dir);

		if (theDir.exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Read the contents of content from file system
	 * @param filePath the path to the file
	 * @return the contents of the file
	 */
	public static String readFileContents(String filePath) {
		StringBuffer xml = new StringBuffer();
		
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filePath));
 
			while ((sCurrentLine = br.readLine()) != null) {
				xml.append(sCurrentLine);
			}
 
		} catch (IOException e) {
			logger.debug("Error. File Not Found: " + filePath);
			return null;
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				return null;
			}
		}		
		
		return xml.toString();
	}
	
	/**
	 * Form contents are stored as an XML document. This returns the contents 
	 * of the document as an array of key/value pairs. 
	 * @param xml the xml that we wish to extract the key/value pairs from.
	 * @return an array of key/value pairs representing the xml
	 */
	public static ArrayList<TreeMap<String, String>>parseXmlKeyValues(String xml) {
		ArrayList<TreeMap<String, String>> contents = new ArrayList<TreeMap<String, String>>();
		
		if (xml == null || xml.trim().equals("")) {
			return contents;
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			
			Document doc = dBuilder.parse(stream);
			doc.getDocumentElement().normalize();
			
			NodeList contentsNodeList = doc.getElementsByTagName("*");
			
			for (int i=0; i < contentsNodeList.getLength(); i++) {
				Node elementNode = contentsNodeList.item(i);
				
				if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
					TreeMap<String, String> map = new TreeMap<String, String>();
					
					String nodeName = elementNode.getNodeName();
					String nodeValue = null;
					
					if (elementNode.getFirstChild() != null) {
						nodeValue = elementNode.getFirstChild().getNodeValue();
					}
					
					if (nodeValue != null && ! nodeValue.trim().equals("")) {
						map.put(nodeName, nodeValue);
					}
					
					contents.add(map);
				}
			}
			
		} catch (ParserConfigurationException e) {
			logger.debug(e.toString());
		} catch (IOException e) {
			logger.debug(e.toString());
		} catch (SAXException e) {
			logger.debug(e.toString());
		}
		
		return contents;
	}

	public static void copyFileFrom(String serverFilePath, String filePath) {
		logger.info("From: " + serverFilePath + " To: " + filePath);
		
		serverFilePath="/Users/jnajdi/Development/sakai/migration/content/note.xlsx";
		logger.info("From: " + serverFilePath + " To: " + filePath);
		
		try {
			File src = new File(serverFilePath);
			File dest = new File(filePath);
			
			if (dest.exists()) {
				logger.info("File already exists: " + filePath + ". Overiding file content.");
				dest.delete();
			} 
			
			Path newFile = Files.copy(src.toPath(), dest.toPath());
			logger.info("New File was created: " + newFile.toString());
			
		} catch (IOException e) {
			logger.error("Unable to copy file from: " + serverFilePath + " To: " + filePath);
			e.printStackTrace();
		}
		
		
	}

	private static String getLocalFilePath(String resourceId, String resourceType, String resourceDir) {
		
		String filePath = resourceId;
		
		if (resourceType.equals(ContentResource.FORM_HELPER)) {
			// FormHelper: org.sakaiproject.metaobj.shared.FormHelper
			int index = resourceId.indexOf("/user/");
			if (index != -1) {
				if (resourceId.lastIndexOf("/") != -1) {
					String filename = resourceId.substring(resourceId.lastIndexOf("/")+1);
					return resourceDir + File.separator + "Forms" + File.separator + filename + FORM_HELPER_EXTENSION;
				}
			}
		}
		
		// /group: /group/5be5d330-a1f9-4754-805f-04daf488c978/Accounts Receivable/AR Guidelines.PA Version.pdf
		// /attachment: /attachment/5be5d330-a1f9-4754-805f-04daf488c978/Exposed OSP Matrix/dd63dfca-6f8a-43e8-b0c6-b68ac9f9d88e/100_1149.JPG
		// /user: /user/563c2a92-8735-49c4-0067-b048b3b3bea5/Pre-Award Training Matrix.xls
		int index = indexOf(resourceId, "/", 3);
		
		if (index != -1) {
			return resourceDir + resourceId.substring(index);
		}
		
		return filePath;
	}

	public static String downloadLocalResource(ContentResource contentResource, String archiveTempDirectory, String resourceDir) {
			
			String filePath = getLocalFilePath(contentResource.resourceId, contentResource.resourceTypeId, resourceDir);
			
			String tempFilePath = archiveTempDirectory + filePath;
			
			int indexLast = tempFilePath.lastIndexOf(File.separator);
			String directory = tempFilePath.substring(0, indexLast);
			
			logger.info("Directory: " + directory);
			
			
			createDirectories(directory);
			
			copyFileFrom(contentResource.serverFilePath, tempFilePath);
			
			return filePath;
		}
	
	private static int indexOf(String word, String str, int occurence) {
		
		int index = word.indexOf(str);
		int count = 1;
		
		while (index >= 0) {
		    if (count == occurence) {
		    	return index;
		    }
		    
		    index = word.indexOf(str, index + 1);
		    
		    count ++;
		}
		
		return -1;
		
	}

	public static String getDate(String dateStr) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = format.parse(dateStr);
			DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	        return newFormat.format(date);
		} catch (ParseException e) {
		}
		return "";
		
	}

	public static void createFile(String filePath, String siteId, String content) {
		//Make sure we lock the file before writing to it. 
		//Wait until the file is available to write. Try 10 times and then exit. 
		
		int count = 0;
		
		boolean written = false;
		
        // Get a file channel for the file
        File file = new File(filePath);
        
        if (file.exists()) {
        	logger.info("File already exists" + filePath);
        	logger.debug("Delete file first");
        	file.delete();
        } 
        logger.info("Creating new file: " + filePath);
        
        try {
        	RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();
            try {    
    	        if (channel != null) {
    	        	while (!written && count < 10) {
    	        		// Use the file channel to create a lock on the file.
    			        // This method blocks until it can retrieve the lock.
    	        		try {
    	        			FileLock lock  = channel.lock();
    		        		
    			        	try {
    			        	
	    			        	FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
	    		 				
	    			        	BufferedWriter bw = new BufferedWriter(fw);
	    			        	
	    		 				bw.append(content);
	    		 				bw.newLine();
	    		 				
	    		 				bw.close();
	    		 				
	    		 	            written = true;
	    		 	            
    			        	} finally {
    			        		// Release the lock.
    			        		lock.release();
    			        	}
    			        } catch (OverlappingFileLockException e) {
    			        	try {
    			                Thread.sleep(10);
    			                
    			              } catch (InterruptedException ex) {
    			            	  throw new InterruptedIOException("Interrupted waiting for a file lock.");
    			              } finally {
    			            	  count =+1;  
    			              }
    			        } 
    				}
    	        }
    	    } catch (IOException ex) {
    	    	logger.info("Failed to lock " + filePath, ex);
    	    } finally {
    	    	 // Close the file
    	    	raf.close();
    	        channel.close();
    	    }
        } catch (IOException io) {
        	logger.error("Failed to write to: " + filePath);
        } 
        
        if (written) {
        	logger.info("Site: " + siteId + " was added to filePath: " + filePath);
        } else {
        	logger.error("Failed to write to: " + filePath);
        }
        
	}
	
	public static String replaceContentPath(String rootContent, String filePath, String regex) {
		// rootContent: /apps/nfs/sakaixx/scholar.vt.edu/content
		// file_path: /content12/2016/273/23/369a17e9-a641-41e4-9079-67177073bb29
		// Return: /apps/nfs/sakai12/scholar.vt.edu/content/2016/273/23/369a17e9-a641-41e4-9079-67177073bb29
		
		if (filePath != null && rootContent != null && rootContent.indexOf(regex) != -1) {
			int index = indexOf(filePath, "/", 2);
			
			String contentType = filePath.substring(1, index);
			String contentId = contentType.substring(contentType.length()-2);
			
			return rootContent.replaceFirst(regex, contentId) + filePath.substring(index);
		}
		
		return filePath;
		
	}

	
	
}
