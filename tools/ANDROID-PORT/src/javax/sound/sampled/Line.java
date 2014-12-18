package javax.sound.sampled;

public interface Line {

	
	public static class Info {
		public Info(java.lang.Class clazz,javax.sound.sampled.AudioFormat format,int mode) {
			
		}
		
		public Info(java.lang.Class clazz,javax.sound.sampled.AudioFormat format) {
			
		}
	}
	
	public void start();
	
	int write(byte[] samples,int start,int length);
	
	void stop();
	
	void close();
	
	void drain();
	
	void addLineListener(LineListener listener);
	
	void open(javax.sound.sampled.AudioFormat format,int mode);
	
	void open(javax.sound.sampled.AudioFormat format);
	
	Control getControl(javax.sound.sampled.Control.Type type);
	
	boolean isControlSupported(Control.Type type);
}
