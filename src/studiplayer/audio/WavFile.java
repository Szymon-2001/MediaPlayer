package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {

	public static void main(String[] args) {
	}
	
	public WavFile() {}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		try {
			readAndStoreDurationFromFile();
		} catch (Exception e) {
			throw new NotPlayableException(super.getPathname(), "The file cannot be read!");
		}

		
	}
	
	public void readAndStoreDurationFromFile() throws NotPlayableException {
		try {
			WavParamReader.readParams(super.getPathname());
			long numberOfFrames = (long) WavParamReader.getNumberOfFrames();
			float frameRate = (float) WavParamReader.getFrameRate();
			super.setDuration(computeDuration(numberOfFrames, frameRate));
		} catch (Exception e) {
			throw new NotPlayableException(super.getPathname(), "The parameters of the file cannot be read!");
		}
				
	}
	
	public String toString() {
		String string = super.toString() + " - " + super.formatDuration();
		return string.trim();
	}
	
	public static long computeDuration(long numberOfFrames, float frameRate) {
		double insec = (double)numberOfFrames / (double)frameRate;
		long dur = (long)(insec * Math.pow(10, 6));
		return dur;
		
	}

}
