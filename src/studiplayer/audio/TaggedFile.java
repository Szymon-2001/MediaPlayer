package studiplayer.audio;
import java.util.Map;
import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile{
	
	private String album;

	public static void main(String[] args) {
	}
	
	public TaggedFile() {
		
	}
	
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		try {
			readAndStoreTags();
		} catch (Exception e){
			throw new NotPlayableException(super.getPathname(), "The file cannot be read!");
		}
	}
	
	public String getAlbum() {
		return album;
	}
	
	public void readAndStoreTags() throws NotPlayableException {
		try {
			Map<String, Object> tags = TagReader.readTags(super.getPathname());
			Object auth = tags.get("author");
			if (auth != null && auth instanceof String) {
				String author = (String) auth;
				super.setAuthor(author.trim());
			}
			Object ti = tags.get("title");
			if (ti != null && ti instanceof String) {
				String title = (String) ti;
				super.setTitle(title.trim());
			}
			Object alb = tags.get("album");
			if (alb != null && alb instanceof String) {
				album = (String) alb;
				album = album.trim();
			}
			Object dur = tags.get("duration");
			if (dur != null && Long.class.isInstance(dur)) {
				super.setDuration((long) dur);
			}
		} catch (Exception e) {
			throw new NotPlayableException(super.getPathname(), "Cannot read tags of the file!");
		}
	}
	
	public String toString() {
		String string = super.toString();
		if (album != null) {
			string += " - " + album + " - " + super.formatDuration();
		} else {
			string += " - " + super.formatDuration();
		}
		return string.trim();
	}

}
