package javax.sound.sampled;

public class AudioFileFormat {
	
	public static class Type {
		public Type AIFC;
		public Type AIFF;
		public Type AU;
		public Type SND;
		public Type WAVE;
		
		public Type(java.lang.String name,java.lang.String description){
			
		}
	}
	
	public AudioFileFormat(AudioFileFormat.Type type,javax.sound.sampled.AudioFormat format,int length) {
		
	}
	
	public int getFrameLength() {
		return 0;
	}

	public AudioFormat getFormat() {
		return null;
	}
}
