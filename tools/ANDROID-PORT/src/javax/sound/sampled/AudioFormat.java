package javax.sound.sampled;

public class AudioFormat {
	
	public static class Type {
		
	}

	public static class Encoding {
		public Encoding PCM_SIGNED;
		public Encoding(java.lang.String encoding) {
			
		}
	}
	
	public AudioFormat(float rate,int bits,int channels,boolean bigEndian,boolean signed) {
		
	}
	
	public AudioFormat(AudioFormat.Encoding encoding,float rate,int bits,int channels,int frame,float clip,boolean interlaced) {
		
	}
	

	public float getFrameRate() {
		return 0.0f;
	}
	
	public int getFrameSize() {
		return 0;
	}
	
	public float getSampleRate() {
		return 0f;
	}
	
	public int getChannels() {
		return 0;
	}
	
	public boolean isBigEndian() {
		return true;
	}
	
	public int getSampleSizeInBits() {
		return 0;
	}
	
	public Encoding getEncoding() {
		return null;
	}
}
