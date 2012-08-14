package de.htwg_konstanz.in.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipInputStream;

public class FileChecker {

	/**
	 * Reads the File and Re-Writes it to the File system to be sure there are
	 * no differences produced later due outputstream writing process
	 * 
	 * @return the path to the Re-Written File
	 * @throws IOException
	 */
	public static String convertIt(String path) throws IOException{
		File f1 = new File(path);
		FileInputStream fis1 = new FileInputStream(f1);
		byte[] buff = new byte[1024];
		OutputStream out = new FileOutputStream("C:/convertedFile");
		while(fis1.read(buff) != -1){
			out.write(buff);
		}
		fis1.close();
		out.flush();
		out.close();
		return "C:/convertedFile";
	}

	// reading bytes and compare
	public static int CompareFilesbyByte(String file1, String file2)
			throws IOException {
		File f1 = new File(file1);
		File f2 = new File(file2);
		FileInputStream fis1 = new FileInputStream(f1);
		FileInputStream fis2 = new FileInputStream(f2);
		if (f1.length() == f2.length()) {
			int n = 0;
			byte[] b1;
			byte[] b2;
			while ((n = fis1.available()) > 0) {
				if (n > 80)
					n = 80;
				b1 = new byte[n];
				b2 = new byte[n];
				int res1 = fis1.read(b1);
				int res2 = fis2.read(b2);
				if (Arrays.equals(b1, b2) == false) {
					System.out.println(file1 + " :\n\n " + new String(b1));
					System.out.println();
					System.out.println(file2 + " : \n\n" + new String(b2));
					return -1;
				}
			}
		} else
			return -2; // length is not matched.
		return 0;
	}

	public static String MD5HashFile(String filename) throws Exception {
		byte[] buf = ChecksumFile(filename);
		String res = "";
		for (int i = 0; i < buf.length; i++) {
			res += Integer.toString((buf[i] & 0xff) + 0x100, 16).substring(1);
		}
		return res;
	}

	public static byte[] ChecksumFile(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);
		byte[] buf = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int n;
		do {
			n = fis.read(buf);
			if (n > 0) {
				complete.update(buf, 0, n);
			}
		} while (n != -1);
		fis.close();
		return complete.digest();
	}

}
