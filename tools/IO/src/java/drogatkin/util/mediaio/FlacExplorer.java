package drogatkin.util.mediaio;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileFilter;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.io.RandomFileInputStream;

import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.Picture;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.metadata.VorbisComment;
import org.kc7bfi.jflac.metadata.SeekTable;

public class FlacExplorer {

	public static void main(String ...args) {
		System.out.printf("FLAC explorer %s%n", " version 1.0");
		if (args.length == 0) {
			System.out.printf("Please use with file[ file ...] arguments%n");
		}
		for(String s:args) {
			processRequest(s);
		}
	}
	
	private static void processRequest(String s) {
		File f = new File(s);
		if (f.exists()) {
			if (f.isDirectory())
				processDirectory(f);
			else
				report(f);
		}
		
	}

	private static void processDirectory(File f) {
		System.out.printf("->%s%n", f);
		f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					processDirectory(pathname);
				else if (pathname.getName().endsWith(".flac"))
					report(pathname);
				return false;
			}});
		
	}

	static void report(File f) {
		if (f.getName().endsWith(".flac") == false)
			return;
		System.out.printf("Exploring %s%n", f.getName());
		try (InputStream inputStream = new RandomFileInputStream(f)) {
			FLACDecoder decoder = new FLACDecoder(inputStream);
			for (Metadata md: decoder.readMetadata()) {
				if (md instanceof SeekTable) {
					SeekTable st = (SeekTable)md;
					System.out.printf("Found seek table of %d entries%n", st.numberOfPoints());
				} else if (md instanceof Picture) {
					Picture pic = (Picture)md;
					System.out.printf("Found picture of %s%n", pic.toString());
				}
				
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
	}
}
