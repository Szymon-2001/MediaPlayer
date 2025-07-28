package studiplayer.audio;

public abstract class AudioFile {
	// Attributes
	private String pathname;
	private String filename;
	private String author;
	private String title;
	
	public static void main(String[] args) {}
	
	public AudioFile() {
		
	}
	
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public  String getPathname() {
		return pathname;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public void parsePathname(String path) {
		// Checking whether path is empty
		path = path.trim();
		if (path.isEmpty()) {
			pathname = "";
			filename = "";
		// By existing path shortening the separators
		} else if((path.contains("/")) || (path.contains("\\"))){
			while(path.contains("//") || path.contains("\\\\")) {
				path.replace("//", "/");
				path.replace("\\\\", "\\");
			}
			// Changing to the absolute Linux path prefix
			if(path.contains(":")) {
				if(System.getProperty("os.name").toLowerCase().contains("linux") || 
						System.getProperty("os.name").toLowerCase().contains("mac") || 
						System.getProperty("os.name").toLowerCase().contains("unix")) {
					path = "/" + path.charAt(path.indexOf(':')) + path.substring(path.indexOf(':')+1);
				}
			// Changing to the absolute Windows path prefix
			} else if(path.indexOf('/') == 0) {
				if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
					path = path.substring(1);
					path = path.substring(0, path.indexOf('/')) + ':' + path.substring(path.indexOf('/'));
				}
			}
			// Returning Windows pathname and filename
			if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				while(path.contains("/")) {
					path.replace("/", "\\");
				}
				filename = path.substring(path.lastIndexOf('\\')+1).trim();
				pathname = path;
			// Returning Linux pathname and filename
			} else if(System.getProperty("os.name").toLowerCase().contains("linux") || 
					System.getProperty("os.name").toLowerCase().contains("mac") || 
					System.getProperty("os.name").toLowerCase().contains("unix")) {
				while(path.contains("\\")) {
					path.replace("\\", "/"); 
				}
				filename = path.substring(path.lastIndexOf('/')+1).trim();
				pathname = path;
			}
		} else if(!(path.contains("/")) && !(path.contains("\\"))) {
			pathname = path;
			filename = path;
		}
	}
	
	public void parseFilename(String filename) {
		if (filename.indexOf(" - ") != -1) {
			author = filename.substring(0, filename.indexOf(" - "));
			author = author.trim();
			if (filename.indexOf(".") != -1) {
				title = filename.substring(filename.indexOf(" - ") + 3 , filename.lastIndexOf("."));
				title = title.trim();
			}else {
				title = filename.substring(filename.indexOf(" - ") + 3).trim();
			}
		} else if (filename.indexOf(".") != -1){
			title = filename.substring(0, filename.lastIndexOf("."));
			title = title.trim();
			author = "";
		}else {
			title = filename.trim();
			author = "";
		}
		
	}
	
	public AudioFile(String path) throws NotPlayableException {
		try {
			parsePathname(path);
			parseFilename(filename);
		} catch (Exception e) {
			throw new NotPlayableException(pathname, "The file cannot be read!");
		}
	}
	
	public String toString() {
		if (getAuthor() == "") {
			return title;
		}else {
		return author + " - " + title;
		}
	}
	public abstract void play() throws NotPlayableException;
	public abstract void togglePause();
	public abstract void stop();
	public abstract String formatDuration();
	public abstract String formatPosition();
	
}
