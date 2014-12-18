package javax.sound.sampled;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream implements Closeable, AutoCloseable {

	public AudioInputStream(java.io.InputStream input,javax.sound.sampled.AudioFormat format,long len) {
		
	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getFrameLength() {
		return -1;
	}
	
	public AudioFormat getFormat() {
		return null;
	}
}
