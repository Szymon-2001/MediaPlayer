package studiplayer.audio;

public class NotPlayableException extends Exception{

	private String pathname;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public NotPlayableException(String pathname, String msg) {
		super(msg);
		this.pathname = pathname;
	}
	public NotPlayableException(String pathname, Throwable t) {
		super(t);
		this.pathname = pathname;
	}
	public NotPlayableException(String pathname, String msg, Throwable t) {
		super(msg, t);
		this.pathname = pathname;
	}
	public String getPath() {
		return pathname;
	}
	public void setPath(String path) {
		this.pathname = path;
	}
	
	public String toString() {

	return "studiplayer.audio.NotPlayableException: " + pathname + "cannot be played; " + super.getMessage();
	}

}
