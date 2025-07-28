package studiplayer.audio;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class PlayList extends AudioFileFactory implements Iterable<AudioFile>{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private List<AudioFile> list = new LinkedList<>();
	private String search;
	private int index;
	private SortCriterion sortCriterion = SortCriterion.DEFAULT;
	ControllablePlayListIterator iterator = new ControllablePlayListIterator(list, search, sortCriterion);
	
	public PlayList() {}
	
	public PlayList(String m3uPathname) {
		loadFromM3U(m3uPathname);
	}
	
	public void add(AudioFile file) {
		list.add(file);
		iterator = new ControllablePlayListIterator(list, search, sortCriterion);
	}
	
	public void remove(AudioFile file) {
		list.remove(file);
		iterator = new ControllablePlayListIterator(list, search, sortCriterion);
	}
	
	public int size() {
		return list.size();
	}
	
	public AudioFile currentAudioFile() {
		iterator.setIndex(index);
		return iterator.next();
	}
	
	public void nextSong() {
		if (iterator.hasNext()) {
			index += 1;
		} else {
			index = 0;
		}
		
	}
	
	public void loadFromM3U(String pathname) {
		list.clear();
		nextSong();
		Scanner scanner = null;
		
		try {
			// open the file for reading
			scanner = new Scanner(new File(pathname));
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					if (!line.trim().startsWith("#") || !line.trim().isEmpty()) {
						AudioFile file = super.createAudioFile(line.trim());
						add(file);
					}
				} catch (NotPlayableException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// ignore
				}
			}
			
			iterator = new ControllablePlayListIterator(list, search, sortCriterion);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				scanner.close();	
			} catch (Exception e) {
				// ignore; probably because file could not be opened
			}
		}
		
	}
	
	public void saveAsM3U(String pathname) {
		FileWriter writer = null;
		String sep = System.getProperty("line.separator");
		
		try {
			// create the file if it does not exist, otherwise reset the file
			// and open it for writing
			writer = new FileWriter(pathname);
			for (AudioFile audiofile: list) {
				// write the pathname of each AudioFile in the list
				writer.write(audiofile.getPathname() + sep);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to write file " + pathname + "!");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				// ignore exception; probably because file could not be opened
			}
		}
	}
	
	
	public List<AudioFile> getList() {
		return list;
	}
	
	public void setSearch(String value) {
		search = value;
		index = 0;
		iterator = new ControllablePlayListIterator(list, search, sortCriterion);
	}
	
	public String getSearch() {
		return search;
	}
	
	
	public void setSortCriterion(SortCriterion sortCriterion) {
		this.sortCriterion = sortCriterion;
		index = 0;
		iterator = new ControllablePlayListIterator(list, search, this.sortCriterion);
	}
	
	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}
	
	public Iterator<AudioFile> iterator() {
		return new ControllablePlayListIterator(list, search, sortCriterion);
	}
	
	public void jumpToAudioFile(AudioFile audioFile) {
		iterator.jumpToAudioFile(audioFile);
		index = iterator.getIndex()-1;
	}
	
	public String toString() {
		
		return list.toString();
		
	}

}
