package drogatkin.util.justtags;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * For support AAC (QuickTime) formats
 */
public class QTTags implements TagNames {

	public static final String ALAC = "Apple Lossless audio file";

	public static int asInt(String str) {
		try {
			return ((ByteBuffer) ByteBuffer.allocate(4).put(str.getBytes("ISO8859_1")).order(ByteOrder.LITTLE_ENDIAN)
					.flip()).getInt();
		} catch (UnsupportedEncodingException uee) {
		}
		throw new IllegalArgumentException("Can't get  " + str + " as int");
	}

	public static String asString(int i) {
		return Charset.forName("ISO8859_1")
				.decode((ByteBuffer) ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).flip()).toString();
	}

	// see
	// http://developer.apple.com/documentation/QuickTime/APIREF/SOURCESIV/atomidcodes.htm
	// � -> 0xA9
	public static final int MOVIEDATAATOMTYPE = asInt("mdat");

	public static final int SKIPATOMTYPE = asInt("skip");

	public static final int FREEATOMTYPE = asInt("free");

	public static final int WIDEATOMTYPE = asInt("wide");

	public static final int MOVIEAID = asInt("moov");

	public static final int TRACKAID = asInt("trak");

	public static final int RGNCLIPAID = asInt("clip");

	public static final int MATTEAID = asInt("matt");

	public static final int EDITSAID = asInt("edts");

	public static final int KQTVRTRACKREFARRAYATOMTYPE = asInt("tref");

	public static final int MEDIAAID = asInt("mdia");

	public static final int MEDIAINFOAID = asInt("minf");

	public static final int DATAINFOAID = asInt("dinf");

	public static final int USERDATAAID = asInt("udta");

	public static final int SAMPLETABLEAID = asInt("stbl");

	public static final int COMPRESSEDMOVIEAID = asInt("cmov");

	public static final int REFERENCEMOVIERECORDAID = asInt("rmra");

	public static final int REFERENCEMOVIEDESCRIPTORAID = asInt("rmda");

	public static final int NAME = asInt("\u00a9nam");

	public static final int MP4_ARTIST = asInt("\u00a9ART");

	public static final int MP4_ALBUM = asInt("\u00a9alb");

	public static final int COMMENTARY = asInt("\u00a9cmt");

	public static final int MP4_DATE = asInt("\u00a9day");

	public static final int TOOL = asInt("\u00a9too");

	public static final int MP4_GENRE = asInt("gnre");

	public static final int CUSTOM_GENRE = asInt("\u00a9gen");

	public static final int MP4_LYRICS = asInt("\u00a9lyr");

	public static final int MP4_ALBUM_ARTIST = asInt("aART");
	// TODO other tags from http://code.google.com/p/mp4v2/wiki/iTunesMetadata

	public static final int CUSTOM_REQ = asInt("\u00a9req");

	public static final int MP4_TRACK = asInt("trkn");

	public static final int DISK_NUMBER = asInt("disk");

	public static final int WRITER = asInt("\u00a9wrt");

	public static final int GROUPING = asInt("\u00a9grp");

	public static final int MP4_BPM = asInt("tmpo");

	public static final int FREE_FORM = asInt("----");

	public static final int MEAN = asInt("mean");

	public static final int NAME_MEAN = asInt("mean");

	public static final int DATA = asInt("data");

	public static final int MP4_COMPILATION = asInt("cpil");

	public static final int HDRL = asInt("hdrl");

	public static final int HDLR = asInt("hdlr");

	public static final int ILST = asInt("ilst");

	public static final int STSAMPLEDESCAID = asInt("stsd");

	public static final int MEDIAHEADERAID = asInt("mdhd");

	public static final int MOVIEHEADERAID = asInt("mvhd");

	public static final int TRACKHEADERAID = asInt("tkhd");

	public static final int QUICKTIMEIMAGEFILEMETADATAATOM = asInt("meta");

	public static final int STSAMPLESIZEAID = asInt("stsz");

	public static final int COVERART = asInt("covr");

	// TODO add more tags from http://atomicparsley.sourceforge.net/mpeg-4files.html

	public static final int LOSSLESS = 1024;

	protected static final String DATA_LENGHT = "DATALENGTH";

	protected byte[] defaultIconData; // should be static

	// Use for heavy debug
	private final static boolean __debug = false;

	File file;
	String charset;

	public QTTags(File f, String cs) {
		file = f;
		charset = cs;
	}

	public int getType(HashMap<String, Object> tagsTarget) {
		return (Integer) tagsTarget.get(FORMAT);
	}

	public File getFile() {
		return file;
	}

	static class AtomHeaderInfo {
		int bodySize;

		int signature;

		FieldInfo[] fields;

		// Map values;
		AtomHeaderInfo(int signature, int size, FieldInfo[] fields) {
			this.signature = signature;
			bodySize = size;
			this.fields = fields;
		}
	}

	static class FieldInfo {
		String name;

		int offset;

		FieldInfo(String name, int offset) {
			this.name = name;
			this.offset = offset;
		}
	}

	int sampleRate, channels, bitrate;
	long length;

	static public int getIntAttribute(String name, HashMap<String, Object> tags) {
		//if (LENGTH.equalsIgnoreCase(name))
		//	return (int) length;
		Object val = tags.get(name);
		if (val instanceof Integer)
			return ((Integer) val).intValue();
		try {
			return Integer.parseInt(val.toString());
		} catch (Exception e) {
		}
		return 0;
	}

	static public boolean getBoolAttribute(String name, HashMap<String, Object> tags) {
		Object val = tags.get(name);
		if (val != null && COMPILATION.equalsIgnoreCase(name) && val instanceof Boolean)
			return ((Boolean) val).booleanValue();
		return false;
	}

	public void parse(HashMap<String, Object> tagsTarget) throws IOException {
		// read atom container header
		FileChannel channel = new FileInputStream(file).getChannel();
		try {
			while (channel.position() < channel.size())
				readAtom(channel, tagsTarget);
		} finally {
			try {
				channel.close();
			} catch (IOException se) {
			}
		}
	}

	protected AtomHeader readAtom(FileChannel channel, HashMap<String, Object> tagsTarget) throws IOException {
		AtomHeader atom = new AtomHeader();
		atom.readHeader(channel);
		if (__debug)
			System.err.println("Read " + atom);
		if (atom.signature == MOVIEAID || atom.signature == TRACKAID || atom.signature == RGNCLIPAID
				|| atom.signature == MATTEAID || atom.signature == EDITSAID
				|| atom.signature == KQTVRTRACKREFARRAYATOMTYPE || atom.signature == MEDIAAID
				|| atom.signature == MEDIAINFOAID || atom.signature == DATAINFOAID || atom.signature == USERDATAAID
				|| atom.signature == SAMPLETABLEAID || atom.signature == COMPRESSEDMOVIEAID
				|| atom.signature == REFERENCEMOVIERECORDAID || atom.signature == REFERENCEMOVIEDESCRIPTORAID) {
			readAtomFolder(channel, atom.extSize - 8, atom.signature, tagsTarget);
		} else if (atom.signature == SKIPATOMTYPE || atom.signature == FREEATOMTYPE || atom.signature == WIDEATOMTYPE) {
			atom.toNextAtom();
		} else if (atom.signature == MOVIEDATAATOMTYPE) {
			if (length > 0) {
				bitrate = (int) (atom.extSize / length / 1000) * 8;
				tagsTarget.put(BITRATE, new Integer(bitrate));
			} else { // calculate later
				tagsTarget.put(DATA_LENGHT, new Long(atom.extSize));
			}
			atom.toNextAtom();
		} else if (atom.signature == ILST) {
			readAtomFolder(channel, atom.extSize - 8, atom.signature, tagsTarget);
		} else if (atom.signature == STSAMPLEDESCAID) {
			String format, encodeVendor;
			ByteBuffer bb = atom.read(8);
			int num = bb.getInt(4);
			if (__debug)
				System.err.println("Entries " + num);
			int f = tagsTarget.get(FORMAT) == null ? 0 : (Integer) tagsTarget.get(FORMAT);
			for (int i = 0; i < num; i++) {
				int size = atom.read(4).getInt();
				// System.err.println("Size of b "+size);
				bb = atom.read(4);
				size -= 8;
				format = Charset.forName("ISO8859_1").decode(bb).toString();
				if (__debug)
					System.err.printf("FORMAT %s%n", format);
				if ("avc1".equals(format) || "mp4v".equals(format))
					f |= CLASS_VIDEO;
				else if ("mp4a".equals(format))
					f |= CLASS_AUDIO;
				else if ("alac".equals(format))
					f |= CLASS_AUDIO + LOSSLESS;
				// System.err.println("Format "+format);
				bb = atom.read(size);// .order(ByteOrder.LITTLE_ENDIAN);
				byte[] encodeVendorBytes = new byte[4];
				bb = (ByteBuffer) bb.position(12);
				bb.get(encodeVendorBytes, 0, 4);
				if (encodeVendorBytes[0] != 0) {
					encodeVendor = Charset.forName("ISO8859_1").decode(ByteBuffer.wrap(encodeVendorBytes)).toString();
					// System.err.println("Encode "encodeVendor);
				} else {
					channels = bb.getShort(16);
					sampleRate = bb.getInt(22);
					tagsTarget.put(SAMPLERATE, new Integer(sampleRate));
				}
			}

			tagsTarget.put(FORMAT, f);
			atom.toNextAtom();
		} else if (atom.signature == QUICKTIMEIMAGEFILEMETADATAATOM) {
			atom.read(4);
			readAtomFolder(channel, atom.extSize - 8 - 4, atom.signature, tagsTarget);
		} else if (atom.signature == NAME) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(TITLE, data.data);
		} else if (atom.signature == MP4_ARTIST) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(ARTIST, data.data);
		} else if (atom.signature == MP4_ALBUM) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(ALBUM, data.data);
		} else if (atom.signature == COMMENTARY) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(COMMENTS, data.data);
		} else if (atom.signature == MP4_DATE) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			try {
				tagsTarget.put(YEAR, new Integer(data.data));
			} catch (Exception e) {
				///
			}
		} else if (atom.signature == GROUPING) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(CONTENTGROUP, data.data);
		} else if (atom.signature == TOOL) {
			atom.toNextAtom();
		} else if (atom.signature == MP4_GENRE) {
			AtomHeader data = new AtomHeader();
			data.readHeader(channel);
			data.move(8);
			int gi = data.read(2).getShort();
			if (gi > 0)
				tagsTarget.put(GENRE, new Integer(gi - 1));
			else
				tagsTarget.put(GENRE, new Integer(12));
			data.toNextAtom();
		} else if (atom.signature == CUSTOM_GENRE) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(GENRE, data.data);
		} else if (atom.signature == MP4_TRACK) {
			AtomHeader data = new AtomHeader();
			data.readHeader(channel);
			data.move(10);
			int t = data.read(2).getShort();
			int nt = data.read(2).getShort();
			tagsTarget.put(TRACK, new Integer(t));
			tagsTarget.put(OFTRACKS, new Integer(nt));
			data.toNextAtom();
		} else if (atom.signature == DISK_NUMBER) {
			AtomHeader data = new AtomHeader();
			data.readHeader(channel);
			data.move(10);
			int d = data.read(2).getShort();
			int nd = data.read(2).getShort();
			tagsTarget.put(PARTOFSET, String.valueOf(d) + '/' + nd);
			data.toNextAtom();
		} else if (atom.signature == WRITER) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(COMPOSER, data.data);
		} else if (atom.signature == COVERART) {
			// COVERART
			DataHeaderBin data = new DataHeaderBin();
			data.read(channel);
			tagsTarget.put(PICTURE, data.data);
		} else if (atom.signature == MP4_COMPILATION) {
			AtomHeader data = new AtomHeader();
			data.readHeader(channel);
			data.move(8);
			int c = data.read(1).get();
			// System.err.println("comp "+c);
			tagsTarget.put(COMPILATION, new Boolean(c == 1));
			data.toNextAtom();
		} else if (atom.signature == MP4_BPM) {
			AtomHeader data = new AtomHeader();
			data.readHeader(channel);
			data.move(8);
			int bpm = data.read(2).getShort();
			tagsTarget.put(BPM, new Integer(bpm));
			data.toNextAtom();
		} else if (atom.signature == FREE_FORM) {
			// mean, name, data
			atom.toNextAtom();
		} else if (atom.signature == MEDIAHEADERAID) {
			ByteBuffer bb = atom.read(20);
			length = bb.getInt(16) / bb.getInt(12);
			tagsTarget.put(LENGTH, new Long(length));
			// System.err.println("Play time (duration "+bb.getInt(16)
			// +")/(time_scale "+bb.getInt(12)
			// +")="+(bb.getInt(16)/bb.getInt(12)));
			atom.toNextAtom();
		} else if (atom.signature == MP4_ALBUM_ARTIST) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(ALBUMARTIST, data.data);
		} else if (atom.signature == MP4_LYRICS) {
			DataHeader data = new DataHeader(charset);
			data.read(channel);
			tagsTarget.put(LYRICS, data.data);
		} else if (atom.signature == MOVIEHEADERAID) {
			atom.toNextAtom();
		} else if (atom.signature == CUSTOM_REQ) {
			atom.toNextAtom();
		} else {
			atom.toNextAtom();
		}
		return atom;
	}

	protected void readAtomFolder(FileChannel channel, long size, int signature, HashMap<String, Object> tagsTarget)
			throws IOException {
		AtomHeader atom = null;
		if (__debug)
			System.err.printf("Processing list %s size %d------------->>>%n", asString(signature), size);
		while (size > 0) {
			atom = readAtom(channel, tagsTarget);
			size -= atom.extSize;
			if (__debug)
				System.err.printf("<<<>>>Size %d of %s afte %s %n", size, asString(signature), atom);
		}
		if (__debug)
			System.err.printf("<<<------------ exit list %s size %d%n", asString(signature), size);
	}

	class AtomHeader {
		int size;

		long extSize;

		long position;

		long start;

		int signature;

		ByteBuffer buffer;

		FileChannel channel;

		void reset() {
			size = 0;
			extSize = 0;
			position = 0;
			signature = -1;
			buffer = null;
			start = -1;
			channel = null;
		}

		void readHeader(FileChannel channel) throws IOException {
			start = channel.position();
			this.channel = channel;
			buffer = ByteBuffer.allocateDirect(8).order(ByteOrder.BIG_ENDIAN);
			channel.read(buffer);
			position += 8;
			buffer.rewind();
			size = buffer.getInt();
			signature = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
			if (size == 1) {
				buffer = ByteBuffer.allocateDirect(8).order(ByteOrder.BIG_ENDIAN);
				channel.read(buffer);
				position += 8;
				buffer.rewind();
				extSize = buffer.getLong();
				if (__debug)
					System.err.printf("Extend size to %d after 1%n", extSize);
			} else
				extSize = size;
		}

		void move(int offset) throws IOException {
			if (offset > extSize - position || (offset < 0 && offset < -position))
				throw new IOException("Can't move beyond of the header " + offset + " - " + (extSize - position));
			channel.position(channel.position() + offset);
			position += offset;
		}

		ByteBuffer read(int len) throws IOException {
			if (len > extSize - position || len < 0)
				throw new IOException("Can't move beyond of the header by " + len + " - " + (extSize - position));
			buffer = ByteBuffer.allocateDirect(len).order(ByteOrder.BIG_ENDIAN);
			channel.read(buffer);
			buffer.rewind();
			position += len;
			return buffer;
		}

		AtomHeader toNextChild() throws IOException {
			AtomHeader result = new AtomHeader();
			result.readHeader(channel);
			position += result.extSize;
			return result;
		}

		void toNextAtom() throws IOException {
			if (extSize - position > 0)
				channel.position(channel.position() + extSize - position);
			else {
				if (extSize - position < 0)
					throw new IOException("Beyond atom " + asString(signature) + " limit " + position + " size "
							+ extSize);
			}
		}

		// void close() {
		// position = extSize;
		// }
		@Override
		public String toString() {
			return "Atom " + asString(signature) + " size: " + size + ", ext: " + extSize;
		}
	}

	class DataHeader extends AtomHeader {
		String encoding;

		String data;

		DataHeader(String encoding) {
			this.encoding = encoding;
		}

		void read(FileChannel channel) throws IOException {
			start = channel.position();
			this.channel = channel;
			buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.BIG_ENDIAN);
			channel.read(buffer);
			position += 4;
			buffer.rewind();
			size = buffer.getShort();
			if (size != 0) {
				extSize = size + 4;
				if (__debug)
					System.err.printf("Non standard data size %d%n", size);
				data = Charset.forName(charset == null ? "ISO8859_1" : encoding).decode(read(size)).toString();
				if (__debug)
					System.err.printf("NData: %s%n", data);
			} else {
				buffer.rewind();
				size = buffer.getInt();
				buffer.rewind();
				channel.read(buffer);
				position += 4;
				buffer.rewind();
				signature = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
				extSize = size;
				if (__debug)
					System.err.printf("Data size %d  %s%n", size, asString(signature));
				if (signature == DATA) {
					int num = read(4).getInt();
					move(4);
					data = Charset.forName(charset == null ? "ISO8859_1" : encoding).decode(read(size - 8 - 8))
							.toString();
					if (__debug)
						System.err.printf("Data: %s%n", data);
				} else {
					System.err.printf("Date header signature 'data' was expected but found %s (size %d)%n",
							asString(signature), size);
					read(size - 8);
				}
			}
			toNextAtom();
		}

		@Override
		public String toString() {
			return data == null ? "null" : data;
		}
	}

	class DataHeaderBin extends AtomHeader {
		byte[] data;

		void read(FileChannel channel) throws IOException {
			readHeader(channel);
			if (signature == DATA) {
				int num = read(4).getInt();
				move(4);
				data = new byte[size - 8 - 8];
				read(size - 8 - 8).get(data);
			} else
				System.err.println("Header data expected where " + asString(signature) + " met.");
			toNextAtom();
		}

		public String toString() {
			return data == null ? "null" : "Image: ";
		}
	}
}