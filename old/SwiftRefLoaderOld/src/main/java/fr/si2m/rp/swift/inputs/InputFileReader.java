package fr.si2m.rp.swift.inputs;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.si2m.rp.swift.orm.IbanBic;
import fr.si2m.rp.swift.report.IbanBicReporting;

public abstract class InputFileReader<E> {

	private static final Logger 			logger 					= LoggerFactory.getLogger(InputFileReader.class);

	private String filePath = null;
	private String separator = "\t";
	private boolean colsNamesLineReached = false;
	private BufferedReader inputReader = null;
	private boolean eof = false;
	private int fileIndex = 0;
	private String encoding = "ISO-8859-1";
	private final List<String> rowCache;

	protected InputFileReader(final String pSeparator, final String pFilePath, final String pEncoding, final List<String> pRowCache) throws FileNotFoundException, UnsupportedEncodingException {
		super();
		this.separator = pSeparator;
		this.filePath = pFilePath;
		this.encoding = pEncoding;
		this.rowCache = pRowCache;
		if ((this.inputReader == null) && !this.eof) {
			this.openStream();
		}
	}

	private void openStream() throws FileNotFoundException, UnsupportedEncodingException {
		this.inputReader = InputFileReader.openStream(this.filePath, this.encoding);
	}

	private StringBuilder readLine() throws IOException {
		StringBuilder strBldr = null;
		this.fileIndex++;
		String str = this.inputReader.readLine();
		if (str == null) {
			this.eof = true;
		} else {
			strBldr = new StringBuilder(str);
			logger.debug("l.{}\t- {}", this.fileIndex, strBldr.toString());
		}
		return strBldr;
	}

	private E parseLine(final StringBuilder pLine) {
		if (pLine == null) {
			return null;
		}
		StringTokenizer lStrTok = null;
		String lColValue = null;
		String prevToken = null;
		lStrTok = new StringTokenizer(pLine.toString(), this.separator, true);
		E lObject = this.newObject();
		for(int i=0; lStrTok.hasMoreElements();) {
			lColValue = (String) lStrTok.nextElement();
			if (!lColValue.equals(this.separator)) {
				lObject = this.populateObjectField(i++, lColValue, lObject);
			} else if (lColValue.equals(prevToken)) {
				lObject = this.populateObjectField(i++, "", lObject);
			}
			prevToken = lColValue;
		}
		return lObject;
	}

	protected abstract void setColumnOrdre(final int pColOrdre, final String pColName);
	protected abstract E populateObjectField(final int pNumCol, final String pColValue, E pObject);
	protected abstract E newObject();

	public E getObjectFromLine() throws IOException {
		StringBuilder lLine;
		while (!this.colsNamesLineReached) {
			lLine = this.readLine();
			if (this.eof) { 
				return null;
			}
			// File MetaData building
			if (lLine.toString().trim().length() > 1) {
				this.colsNamesLineReached = true;
				StringTokenizer lStrTok = null;
				lStrTok = new StringTokenizer(lLine.toString(), this.separator);
				for(int i=0; lStrTok.hasMoreElements(); i++) {
					String lColName = (String) lStrTok.nextElement();
					this.setColumnOrdre(i, lColName);
				}
			}
		}

		lLine = this.readLine();
		if (!this.eof) {
			this.rowCache.add(lLine.toString());
		}
		E lObj = this.parseLine(lLine);
		return lObj; 
	}

	public boolean isEof() {
		return this.eof;
	}

	public int getFileIndex() {
		return this.fileIndex;
	}

	public static BufferedReader openStream(final String pFilePath, final String pEncoding) throws FileNotFoundException, UnsupportedEncodingException {
		InputStream is = new FileInputStream(pFilePath);
		InputStreamReader isr = new InputStreamReader(is, pEncoding);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	public static void main(String[] args) throws IOException {
		String path = "/home/nta/Documents/ecommerce/SWIFT/sample.txt";
		InputIbanBicReader fr = new InputIbanBicReader("\t", path, "UTF-8", new IbanBicReporting(), new ArrayList<String>());
		IbanBic lst = null;

		while (!fr.isEof()) {
			lst = fr.getObjectFromLine();
			System.out.println(lst);
		}
	}

}