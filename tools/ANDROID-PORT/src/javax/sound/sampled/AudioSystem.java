package javax.sound.sampled;

public class AudioSystem {

	public AudioInputStream getAudioInputStream(java.io.File f) {
		return null;
	}
	
	public SourceDataLine getSourceDataLine(javax.sound.sampled.AudioFormat format) {
		return null;
	}
	
	
	
	public int write(javax.sound.sampled.AudioInputStream input,javax.sound.sampled.AudioFileFormat.Type type,
			java.io.File file) {
		return 0;
	}
	
	public int write(javax.sound.sampled.AudioInputStream input,javax.sound.sampled.AudioFileFormat.Type type,
			java.io.OutputStream output) {
		return 0;
	}
	
	public Line getLine(Line.Info info) {
		return null;
	}
	
	public AudioInputStream getAudioInputStream(javax.sound.sampled.AudioFormat format,javax.sound.sampled.AudioInputStream input) {
		return null;
	}
	
	public boolean isLineSupported(javax.sound.sampled.Line.Info info) {
		return false;
	}
}
