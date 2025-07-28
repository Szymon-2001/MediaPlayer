package studiplayer.audio;

import java.util.Comparator;

public class AuthorComparator implements Comparator<AudioFile>{

	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		
		if (o1.getAuthor().equals("") || o1.getAuthor() == null) {
			return -1;
		}
		
		if (o2.getAuthor().equals("") || o2.getAuthor() == null) {
			return 1;
		}
		
		return o1.getAuthor().compareTo(o2.getAuthor());
	}

}
