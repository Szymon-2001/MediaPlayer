package studiplayer.audio;

public class AudioFileFactory {

	public static void main(String[] args) {
	}
	
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		String extension = path.substring(path.lastIndexOf('.')+1);
		extension.trim();
		if (extension.equals("wav")) {
			AudioFile file = new WavFile(path);
			return file;
		} else if (extension.equals("ogg") || extension.equals("oGg") || extension.equals("mp3")  ) {
			AudioFile file = new TaggedFile(path);
			return file;
		} else {
			throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\"");
		}
	}

}
