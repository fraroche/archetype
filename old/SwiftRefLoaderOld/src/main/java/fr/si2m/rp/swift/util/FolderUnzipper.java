package fr.si2m.rp.swift.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.sun.servicetag.UnauthorizedAccessException;

public class FolderUnzipper {

	private URL		zipFileUrl;
	private byte		zipStream[];
	private boolean	deleteUnzipFolderOnExit;

	protected FolderUnzipper() {
		this.zipFileUrl = null;
		this.zipStream = null;
	}

	public FolderUnzipper(final byte zippedInsputSrteam[], final boolean deleteUnzipFolderOnExit) {
		this.zipFileUrl = null;
		this.zipStream = null;
		this.deleteUnzipFolderOnExit = deleteUnzipFolderOnExit;
		this.zipStream = zippedInsputSrteam;
	}

	public FolderUnzipper(final String zipFilePath, final boolean deleteUnzipFolderOnExit) throws FileNotFoundException {
		this.zipFileUrl = null;
		this.zipStream = null;
		this.deleteUnzipFolderOnExit = deleteUnzipFolderOnExit;
		try {
			File lFile = new File(zipFilePath);
			if (!lFile.exists()) {
				throw new FileNotFoundException(zipFilePath);
			} if (!lFile.canRead()) {
				throw new UnauthorizedAccessException(zipFilePath);
			}
			this.zipFileUrl = lFile.toURL();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public FolderUnzipper(final URL zipFileUrl, final boolean deleteUnzipFolderOnExit) {
		this.zipFileUrl = null;
		this.zipStream = null;
		this.deleteUnzipFolderOnExit = deleteUnzipFolderOnExit;
		this.zipFileUrl = zipFileUrl;
	}

	public FolderUnzipper(final File zipFile, final boolean deleteUnzipFolderOnExit) {
		this.zipFileUrl = null;
		this.zipStream = null;
		this.deleteUnzipFolderOnExit = deleteUnzipFolderOnExit;
		try {
			this.zipFileUrl = zipFile.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void unzipToFolder(final File destFolder) {
		try {
			java.io.InputStream in = null;
			if (this.zipStream != null) {
				in = new ByteArrayInputStream(this.zipStream);
			} else {
				in = this.zipFileUrl.openStream();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(in));
			this.unzip(destFolder, zin);
			zin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void unzip(final File destFolder, final ZipInputStream zin) throws IOException, FileNotFoundException {
		String slash = File.separator;
		ZipEntry entry;
		while ((entry = zin.getNextEntry()) != null) {
			File f = new File(destFolder.getAbsolutePath() + slash + entry.getName());
			String workingPath = "/";
			for (StringTokenizer strTok = new StringTokenizer(f.getAbsolutePath(), slash); strTok.hasMoreElements();) {
				workingPath = workingPath + (String) strTok.nextElement();
				File localFile = new File(workingPath);
				if (!localFile.exists()) {
					if (strTok.hasMoreTokens() || entry.isDirectory()) {
						localFile.mkdir();
					} else {
						localFile.createNewFile();
					}
					if (this.deleteUnzipFolderOnExit) {
						localFile.deleteOnExit();
					}
				}
				workingPath = workingPath + slash;
			}

			if (!entry.isDirectory()) {
				BufferedOutputStream fo = new BufferedOutputStream(new FileOutputStream(workingPath));
				for (int c = zin.read(); c != -1; c = zin.read()) {
					fo.write(c);
				}

				fo.close();
			}
		}
	}

	private static boolean deleteFolder(final File treatmentFolder) {
		File children[] = treatmentFolder.listFiles();
		for (int i = 0; (children != null) && (i < children.length); i++) {
			if (!deleteFolder(children[i])) {
				return false;
			}
		}

		return treatmentFolder.delete();
	}

	public static void main(String args[]) throws FileNotFoundException {
		String in = "/TEMP/Reports.zip";
		File outputFolder1 = new File("/TEMP/ReportsOut1");
		outputFolder1.mkdir();
		outputFolder1.delete();
		FolderUnzipper unzipper = new FolderUnzipper("/TEMP/Reports.zip", false);
		unzipper.unzipToFolder(outputFolder1);
		deleteFolder(outputFolder1);
	}
}