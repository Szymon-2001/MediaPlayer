package studiplayer.audio;

public enum SortCriterion {
	
	DEFAULT(0), AUTHOR(1), TITLE(2), ALBUM(3), DURATION(4);
	
	private int sortCriterion;
	
	SortCriterion() {}
	
	SortCriterion(int sortCriterion) {
		this.sortCriterion = sortCriterion;
	}

}
