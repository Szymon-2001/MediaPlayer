package studiplayer.audio;
import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile>{
	
	private String album1;
	private String album2;

	public AlbumComparator() {
		
	}
	
	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		
		if (o1 instanceof TaggedFile) {
			album1 = ((TaggedFile) o1).getAlbum();
		}

		
		if (o2 instanceof TaggedFile) {
			album2 = ((TaggedFile) o2).getAlbum();
		}
		
		if (!(o1 instanceof TaggedFile) && !(o2 instanceof TaggedFile)) {
			return 0;
		} else if (!(o1 instanceof TaggedFile)) {
			return -1;
		} else if (!(o2 instanceof TaggedFile)) {
			return 1;
		}
		
		if (album1 == null && album2 == null) {
			return 0;
		} else if (album1 == null) {
			return -1;
		} else if (album2 == null) {
			return 1;
		}
		
		return album1.compareTo(album2);
		
	}

}
