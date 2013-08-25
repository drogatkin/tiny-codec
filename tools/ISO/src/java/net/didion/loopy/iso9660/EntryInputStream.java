package net.didion.loopy.iso9660;

import java.io.IOException;
import java.io.InputStream;

class EntryInputStream extends InputStream {
    // entry within the IsoFile
    private ISO9660FileEntry entry;
    // current position within entry data
    private long pos;
    // number of remaining bytes within entry
    private long rem;
    // the source IsoFile
    private ISO9660FileSystem isoFile;

    EntryInputStream(ISO9660FileEntry entry, ISO9660FileSystem file) {
        this.pos = 0;
        this.rem = entry.getSize();
        this.entry = entry;
        this.isoFile = file;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (this.rem == 0) {
            return -1;
        }
        if (len <= 0) {
            return 0;
        }
        if (len > this.rem) {
            len = (int) this.rem;
        }

        synchronized (this.isoFile) {
            if (this.isoFile.isClosed()) {
                throw new IOException("ZipFile closed.");
            }
            len = this.isoFile.readData(this.entry, this.pos, b, off, len);
        }

        if (len > 0) {
            this.pos += len;
            this.rem -= len;
        }

        if (this.rem == 0) {
            close();
        }

        return len;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        if (read(b, 0, 1) == 1) {
            return b[0] & 0xff;
        } else {
            return -1;
        }
    }

    @Override
    public long skip(long n) {
        long len = n > rem ? rem :  n;
        this.pos += len;
        this.rem -= len;
        if (this.rem == 0) {
            close();
        }
        return len;
    }
    
    public long seek(long position) throws IOException {
    	if (position < 0)
    		throw new IOException("Seek value less than zero: "+position);
    	else if (position > pos+rem)
    		position = pos+rem;
    	long adj = position - pos;
    	pos += adj;
    	rem -= adj;
    	return pos;
    }

    @Override
    public int available() {
    	if (this.rem > Integer.MAX_VALUE)
    		return Integer.MAX_VALUE;
        return (int)this.rem;
    }
    
    public long size() {
        return this.entry.getSize();
    }

   @Override
   public void close() {
       this.rem = 0;
       this.entry = null;
       this.isoFile = null;
   }
}