package net.didion.loopy.impl;

import net.didion.loopy.FileSystem;
import net.didion.loopy.LoopyException;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public abstract class AbstractFileSystem implements FileSystem {
    /**
     * The file containing the file system image.
     */
    private File file;
    /**
     * Whether the image is writeable. Currently, only read-only images
     * are supported.
     */
    private boolean readOnly;
    /**
     * Channel to the open file.
     */
    private RandomAccessFile channel;
    
    protected long originalSize;
    
    private boolean buggyAndroid;
    
    private byte[] oneMegBuff; // can be static but need sync for creation

    protected AbstractFileSystem(File file, boolean readOnly) throws LoopyException {
        if (!readOnly) {
            throw new IllegalArgumentException(
                    "Currrently, only read-only is supported");
        }
        this.file = file;
        this.readOnly = readOnly;
        try {
            // check that the underlying file is valid
            checkFile();
            // open the channel
            this.channel = new RandomAccessFile(this.file, getMode(readOnly));
        } catch (IOException ex) {
            throw new LoopyException("Error opening the file", ex);
        }
        buggyAndroid = checkForBuggyAndroid();
    }

	private boolean checkForBuggyAndroid() {
		try {
			return Class.forName("android.os.Build$VERSION").getField("SDK_INT").getInt(null) < 11;
			//Class.forName("android.os.Build$VERSION_CODES").getField("HONEYCOMB").getInt(null);
		} catch (Exception e) {

		}
		return false;
	}

	private void checkFile() throws FileNotFoundException {
        if (this.readOnly && !file.exists()) {
            throw new FileNotFoundException("File does not exist: " + this.file);
        }
        originalSize = file.exists()?file.length():0;
    }

    private String getMode(boolean readOnly) {
        return (readOnly) ? "r" : "rw";
    }

    // TODO: close open streams automatically
    public synchronized void close() throws LoopyException {
        if (isClosed()) {
            return;
        }
        try {
            this.channel.close();
        } catch (IOException ex) {
            throw new LoopyException("Error closing file system", ex);
        } finally {
            this.channel = null;
        }
    }

    public boolean isClosed() {
        return null == this.channel;
    }

    protected void ensureOpen() throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException("File has been closed");
        }
    }

	protected void seek(long pos) throws IOException {
		ensureOpen();
		if (buggyAndroid && pos > Integer.MAX_VALUE) {

			this.channel.seek(Integer.MAX_VALUE);
			int n = (int) (pos - Integer.MAX_VALUE);
			synchronized (this) {
				if (oneMegBuff == null)
					oneMegBuff = new byte[1024 * 1024];
			}
			for (int i = 0, k = n / 1024 / 1024; i < k; i++)
				this.channel.read(oneMegBuff);
			for (int i = 0, k = n % (1024 * 1024); i < k; i++)
				this.channel.read();
		} else
			this.channel.seek(pos);

	}

    protected int read(byte[] buffer, int bufferOffset, int len) throws IOException {
        ensureOpen();
        return this.channel.read(buffer, bufferOffset, len);
    }
}