package studiplayer.audio;

import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile>{

	private String title1;
	private String title2;

	public TitleComparator() {
		
	}
	
	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		
		title1 = o1.getTitle();
		title2 = o2.getTitle();
		
		return title1.compareTo(title2);
	}

}
