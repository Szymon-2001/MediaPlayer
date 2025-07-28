package studiplayer.audio;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ControllablePlayListIterator implements Iterator<AudioFile> {

	private List<AudioFile> list;
	private int index;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public  ControllablePlayListIterator(List<AudioFile> list) {
		this.list = new LinkedList<>(list);
		index = 0;
	}
	
	public  ControllablePlayListIterator(List<AudioFile> list, String search, SortCriterion sortCriterion) {
		this.list = new LinkedList<>();
		index = 0;
		for (AudioFile file : list) {
		
			if (search == null || search.trim().equals("")) {
				this.list.add(file);
			
			} else if (file.getAuthor() != null && file.getAuthor().contains(search)) {
				this.list.add(file);
			
			} else if (file.getTitle() != null && file.getTitle().contains(search)) {
				this.list.add(file);
				
			} else if (file instanceof TaggedFile) {
				if (((TaggedFile)file).getAlbum() != null && ((TaggedFile)file).getAlbum().contains(search)) { 
					this.list.add(file);
					
				}
			} 	
		}
		
		
		if (sortCriterion != null) {
		
			if (sortCriterion.equals(SortCriterion.DEFAULT)) {
				// ignore
			} else if (sortCriterion.equals(SortCriterion.AUTHOR)) {
					this.list.sort(new AuthorComparator());
				
			} else if (sortCriterion.equals(SortCriterion.ALBUM)) {
					this.list.sort(new AlbumComparator());
				
			} else if (sortCriterion.equals(SortCriterion.TITLE)) {
					this.list.sort(new TitleComparator());
				
			} else if (sortCriterion.equals(SortCriterion.DURATION)) {
					this.list.sort(new DurationComparator());
				
			}
		}
		
	}
	
	@Override
	public boolean hasNext() {
		return index < list.size();
	}

	@Override
	public AudioFile next() {
		if (hasNext()) {
			return list.get(index++);
		}
		return null;
	}
	
	public AudioFile jumpToAudioFile(AudioFile file) {
		if (list.contains(file)) {
			index = list.indexOf(file);
			return next();
		} else {
			return null;
		}
		
	}
	
}
