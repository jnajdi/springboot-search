package edu.vt.tlos.domain.binary;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.sql.rowset.serial.SerialBlob;

public class BinaryEntity {

	public static final int BLOCK_END = 2;
	
	private Blob blob;
	
	private String blobId;
	private int typeId;
	private int blockId;

	private Block1 block1;
	private Block2 block2;
	private Block3 block3;
	private Block4 block4;
	private Block5 block5;
	private Block6 block6;
	
	public BinaryEntity(Blob blob) throws IOException, SQLException {
		
		this.blob = blob;
		
		DataInputStream dis = new DataInputStream(blob.getBinaryStream());

		byte[] buffer = new byte[6];

		//TEST for blob type
		dis.read(buffer, 0, 6);
		blobId = new String(buffer, 0, 6);
		if (! blobId.equals("CHSBRE"))
			return;

		typeId = dis.readInt();

		boolean finished = false;
		while (! finished) {

			blockId = dis.readInt();
			
			if (blockId == Block1.BLOCK_1) {

				block1 = new Block1();
				block1.setId(dis.readUTF());
				block1.setResourceType(dis.readUTF());
				block1.setAccess(dis.readUTF());
				block1.setHidden(dis.readBoolean());

			} else if (blockId == Block2.BLOCK_2) {

				block2 = new Block2();
				block2.setReleaseDate(dis.readLong());
				block2.setRetractDate(dis.readLong());
				
			} else if (blockId == Block3.BLOCK_3) {
				
				block3 = new Block3();

				int numGroups = dis.readInt();
				ArrayList<String> groups = new ArrayList<String>();
				for (int i = 0; i < numGroups; i++) {
					groups.add(dis.readUTF());
				}
				
				block3.setGroups(groups);

			} else if (blockId == Block4.BLOCK_4) {

				block4 = new Block4();
				
				block4.setPropertiesType(dis.readInt());

				if (block4.getPropertiesType() == 1) {
					
					int block = dis.readInt();

					if (block == PropertyBlock1.PROP_BLOCK_1) {
						
						PropertyBlock1 propertyBlock1 = new PropertyBlock1();

						propertyBlock1.setNumPropertyBlocks(dis.readInt());
						
						block4.setPropertyBlock1(propertyBlock1);
						
						for (int i = 0; i < propertyBlock1.getNumPropertyBlocks(); i++) {
							block = dis.readInt();
							switch (block) {
								case PropertyBlock2.PROP_BLOCK_2: {
									
									PropertyBlock2 propertyBlock2 = new PropertyBlock2();
									propertyBlock2.setKey(dis.readUTF());
									propertyBlock2.setValue(dis.readUTF());
									
									block4.getPropertyBlocks().add(propertyBlock2);
									
								}
							
								break;
							
								case PropertyBlock3.PROP_BLOCK_3: {
								
									PropertyBlock3 propertyBlock3 = new PropertyBlock3();
								
									propertyBlock3.setKey(dis.readUTF());
									int arraySize = 0;
								
									for (int j = 0; j < arraySize; j++) {
										propertyBlock3.getValues().add(dis.readUTF());
									}
									
									block4.getPropertyBlocks().add(propertyBlock3);
									
								}
							}
						}
					}
				}

			
			} else if (blockId == Block5.BLOCK_5) {
				block5 = new Block5();
				
				block5.setContentType(dis.readUTF());
				block5.setContentLength(dis.readLong());
				block5.setFilePath(dis.readUTF());
				
			} else if (blockId == Block6.BLOCK_6) {
				
				byte[] body = new byte[dis.readInt()];
				dis.read(body);
				
				block6 = new Block6();
				block6.setBody(body);
	
			} else if (blockId == BLOCK_END) {
				
				finished = true;
			}
		}
	}
	
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeBytes(blobId);
		dos.writeInt(typeId);
		
		if (block1 != null) {
			dos.writeInt(Block1.BLOCK_1);
			dos.writeUTF(block1.getId());
			dos.writeUTF(block1.getResourceType());
			dos.writeUTF(block1.getAccess());
			dos.writeBoolean(block1.isHidden());
		}

		if (block2 != null) {
			dos.writeInt(Block2.BLOCK_2);
			dos.writeLong(block2.getReleaseDate());
			dos.writeLong(block2.getRetractDate());
		}
		
		if (block3 != null) {
			dos.writeInt(Block3.BLOCK_3);
			dos.writeInt(block3.getGroups().size());
			
			for (int i=0; i < block3.getGroups().size(); i++) {
				dos.writeUTF(block3.getGroups().get(i));
			}
		}

		if (block4 != null) {
			dos.writeInt(Block4.BLOCK_4);
		
			dos.writeInt(block4.getPropertiesType());

			if (block4.getPropertyBlock1() != null) {
				dos.writeInt(PropertyBlock1.PROP_BLOCK_1);
				
				dos.writeInt(block4.getPropertyBlocks().size());
			}
			
			for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
				
				Object block = block4.getPropertyBlocks().get(i);
				
				if (block instanceof PropertyBlock2) {
					
					PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
					
					dos.writeInt(PropertyBlock2.PROP_BLOCK_2);
					dos.writeUTF(propertyBlock2.getKey());
					dos.writeUTF(propertyBlock2.getValue());
					
				} else if (block instanceof PropertyBlock3) {
					
					PropertyBlock3 propertyBlock3 = (PropertyBlock3) block;
					
					dos.writeInt(PropertyBlock3.PROP_BLOCK_3);
					dos.writeUTF(propertyBlock3.getKey());
					dos.writeInt(propertyBlock3.getValues().size());
					
					for (int j=0; j < propertyBlock3.getValues().size(); j++) {
						String value = (String) propertyBlock3.getValues().get(j);
						dos.writeUTF(value);
					}
					
				}
				
			}
		}
		
		if (block5 != null) {
			dos.writeInt(Block5.BLOCK_5);
		
			dos.writeUTF(block5.getContentType());
			dos.writeLong(block5.getContentLength());
			dos.writeUTF(block5.getFilePath());
		}
		
		if (block6 != null) {
			dos.writeInt(Block6.BLOCK_6);
			
			dos.writeInt(block6.getBody().length);
			dos.write(block6.getBody());
		}
		
		dos.writeInt(BLOCK_END);
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
		
	}
	
	public static BinaryEntity binaryEntityForResourceUuid(String resourceUuid, Connection conn) throws SQLException, IOException {
		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			ResultSet rset = stmt.executeQuery("select binary_entity from content_resource where resource_uuid = '" + resourceUuid + "'");
			
			try {
				while (rset.next()) {

					//Read BLOB representing binary field retrieved from DB
					Blob dbBlob = rset.getBlob(1);

					//Create a local copy of this field
					BinaryEntity binaryEntity = new BinaryEntity(dbBlob);
					
					boolean success = performSanityCheck(binaryEntity);
					
					if (! success) {
						System.exit(0);
					}
					
					return binaryEntity;
					
				}

			} finally {
				try { rset.close(); } catch (Exception ignore) {}
			}
		} finally {
			try { stmt.close(); } catch (Exception ignore) {}
		}

		return null;
	}

	private static boolean performSanityCheck(BinaryEntity binaryEntity) throws SQLException, IOException {
		
		//Get the array of bytes in the binary entity field
		byte[] outBuffer = binaryEntity.toByteArray();
		
		//Create a local BLOB of the binary entity
		Blob localBlob = new SerialBlob(outBuffer);
		
		//Do some checks to make sure that the binary field structure isn't
		//different from what we expect.
		
		if (binaryEntity.getBlob().length() != localBlob.length()) {
			System.out.println("Binary Entity Sanity Check: FAILED. Binary Entity Blob Length != Local Blob Length");
			return false;
		}
		
		//Check to make sure the same bytes are in the remote binary field and the local version we created
		int length = (new Long(binaryEntity.getBlob().length())).intValue();
		
		byte[] dbBytes = binaryEntity.getBlob().getBytes(1, length);
		byte[] localBytes = localBlob.getBytes(1, length);
		
		for (int i=0; i < dbBytes.length; i++) {
			
			if (dbBytes[i] != localBytes[i]) {
				System.out.println("Binary Entity Sanity Check: FAILED. Binary Entity Blob Byte != Local Blob Byte");
				return false;
			}
		}
		
		return true;
	}
	
	public String toString() {
		String s = "";
		
		s += "BLOB_ID:" + blobId + "\n";
		s += "TYPE_ID:" + typeId + "\n";
		s += "\n";
		
		if (block1 != null) {
			s += "BLOCK_1_ID:" + Block1.BLOCK_1 + "\n";
			s += "ID:" + block1.getId() + "\n";
			s += "RESOURCE_TYPE:" + block1.getResourceType() + "\n";
			s += "ACCESS:" + block1.getAccess() + "\n";
			s += "HIDDEN:" + block1.isHidden() + "\n";
			s += "\n";
		}
		
		if (block2 != null) {
			s += "BLOCK_2_ID:" + Block2.BLOCK_2 + "\n";
			s += "RELEASE_DATE:" + new Date(block2.getReleaseDate()) + "\n";
			s += "RETRACT_DATE:" + new Date(block2.getRetractDate()) + "\n";
			s += "\n";
		}
		
		if (block3 != null) {
			s += "BLOCK_3_ID:" + Block3.BLOCK_3 + "\n";
			s += "NUM_ARRAY_ELEMENTS:" + block3.getGroups().size() + "\n";
			
			for (int i=0; i < block3.getGroups().size(); i++) {
				s += "GROUP " + i + ":" + block3.getGroups().get(i) + "\n";
			}
			
			s += "\n";
		}

		if (block4 != null) {
			s += toStringBlock4();
		}
		
		if (block5 != null) {
			s += "BLOCK_5_ID:" + Block5.BLOCK_5 + "\n";
			
			s += "CONTENT_TYPE:" + block5.getContentType() + "\n";
			s += "CONTENT_LENGTH:" + block5.getContentLength() + "\n";
			s += "FILE_PATH:" + block5.getFilePath() + "\n";	
			
			s += "\n";
		}

		if (block6 != null) {
			s += "BLOCK_6_ID:" + Block6.BLOCK_6 + "\n";
			
			s += "BYTE_ARRAY_LENGTH:" + block6.getBody().length;
			
			for (int i=0; i < block6.getBody().length; i++) {
				s += block6.getBody()[i] + " ";
			}
			
			s += "\n";
		}
		
		s += "BLOCK_END_ID:" + BLOCK_END + "\n";
		
		return s;
	}

	private String toStringBlock4() {
		String s = "";
		
		s += "BLOCK_4_ID:" + Block4.BLOCK_4 + "\n";
		
		s += "PROERTY_TYPE_ID:" + block4.getPropertiesType() + "\n";
		
		if (block4.getPropertyBlock1() != null) {

			s += "PROPERTY_BLOCK_ID:" + PropertyBlock1.PROP_BLOCK_1 + "\n"; 
			
			s += "PROPERTY_BLOCK_COUNT:" + block4.getPropertyBlocks().size() + "\n";
			
			s += "\n";
			
			for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
				
				Object block = block4.getPropertyBlocks().get(i);
						
				if (block instanceof PropertyBlock2) {
					
					s += "PROPERTY_BLOCK_ID:" + PropertyBlock2.PROP_BLOCK_2 + "\n"; 
					
					PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
					
					s += propertyBlock2.getKey() + ":" + propertyBlock2.getValue() + "\n";
					
					s += "\n";
					
				} else if (block instanceof PropertyBlock3) {
					
					s += "PROPERTY_BLOCK_ID:" + PropertyBlock3.PROP_BLOCK_3 + "\n"; 
					
					PropertyBlock3 propertyBlock3 = (PropertyBlock3) block;
					
					s += "PROPERTY_NAME:" + propertyBlock3.getKey() + "\n";
					s += "ARRAY_SIZE:" + propertyBlock3.getValues().size() + "\n";
					
					s += "ARRAY_VALUES:";
					
					for (int j=0; j < propertyBlock3.getValues().size(); j++) {
					
						s += propertyBlock3.getValues().get(j) + " ";
								
					}
					
					s += "\n";
				}
			}
		}
		return s;
	}

	public String getDescription() {
		for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
			
			Object block = block4.getPropertyBlocks().get(i);
					
			if (block instanceof PropertyBlock2) {
				
				PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
				
				if (propertyBlock2.getKey().equals("CHEF:description")) {
					return propertyBlock2.getValue();
				}
			}
		}
		
		return "";
	}
	
	public String getLastModified() {
		for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
			
			Object block = block4.getPropertyBlocks().get(i);
					
			if (block instanceof PropertyBlock2) {
				
				PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
				
				if (propertyBlock2.getKey().equals("DAV:getlastmodified")) {
					if (propertyBlock2.getValue() != null && propertyBlock2.getValue().length() >=8) {
						 
						try {
							String dateStr = propertyBlock2.getValue().substring(0, 7);
							DateFormat blobFormat = new SimpleDateFormat("yyyymmdd");
							java.util.Date date = blobFormat.parse(dateStr);
							
							DateFormat newFormat = new SimpleDateFormat("yyyy-mm-dd"); 
				            return newFormat.format(date);
				            
						} catch (ParseException e) {
						
						}
						
						return propertyBlock2.getValue();
					}
				}
			}
		}
		
		return "";
	}
	
	public String getModifiedBy() {
		for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
			
			Object block = block4.getPropertyBlocks().get(i);
					
			if (block instanceof PropertyBlock2) {
				
				PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
				
				if (propertyBlock2.getKey().equals("CHEF:modifiedby")) {
					return propertyBlock2.getValue();
				}
			}
		}
		
		return "";
	}
	
	
	public String getDisplayName() {
		for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
			
			Object block = block4.getPropertyBlocks().get(i);
					
			if (block instanceof PropertyBlock2) {
				
				PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
				
				if (propertyBlock2.getKey().equals("DAV:displayname")) {
					return propertyBlock2.getValue();
				}
			}
		}
		
		return "";
	}
	
	public String getCreator() {
		for (int i=0; i < block4.getPropertyBlocks().size(); i++) {
			
			Object block = block4.getPropertyBlocks().get(i);
					
			if (block instanceof PropertyBlock2) {
				
				PropertyBlock2 propertyBlock2 = (PropertyBlock2) block;
				
				if (propertyBlock2.getKey().equals("CHEF:creator")) {
					return propertyBlock2.getValue();
				}
			}
		}
		
		return null;
	}
	
	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	public String getBlobId() {
		return blobId;
	}

	public void setBlobId(String blobId) {
		this.blobId = blobId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	public Block1 getBlock1() {
		return block1;
	}

	public void setBlock1(Block1 block1) {
		this.block1 = block1;
	}

	public Block2 getBlock2() {
		return block2;
	}

	public void setBlock2(Block2 block2) {
		this.block2 = block2;
	}

	public Block3 getBlock3() {
		return block3;
	}

	public void setBlock3(Block3 block3) {
		this.block3 = block3;
	}

	public Block4 getBlock4() {
		return block4;
	}

	public void setBlock4(Block4 block4) {
		this.block4 = block4;
	}

	public Block5 getBlock5() {
		return block5;
	}

	public void setBlock5(Block5 block5) {
		this.block5 = block5;
	}

	public Block6 getBlock6() {
		return block6;
	}

	public void setBlock6(Block6 block6) {
		this.block6 = block6;
	}

	public static int getBlockEnd() {
		return BLOCK_END;
	}
	
	
}

