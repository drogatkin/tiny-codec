package de.vdheide.mp3;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class IOAdapter {
	RandomAccessFile raf;
	InputStream ins;

	public IOAdapter(File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "r"));
	}

	public IOAdapter(InputStream in) {
		ins = in;
	}

	public IOAdapter(RandomAccessFile file) {
		raf = file;
	}

	public void read(byte[] bs) throws IOException {
		//raf.read(bs);
		if (raf != null)
			raf.readFully(bs);
		else if (ins != null)
			if (ins.read(bs) != bs.length)
				throw new EOFException();
	}

	public void close() throws IOException {
		if (raf != null)
			raf.close();
		else if (ins != null)
			ins.close();
	}
}
