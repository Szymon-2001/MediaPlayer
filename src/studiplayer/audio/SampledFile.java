package studiplayer.audio;

import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {

	private long duration;
	
	public static void main(String[] args) {
	}
	
	public SampledFile() {}
	
	public SampledFile(String path) throws NotPlayableException {
		super(path);
	}
	
	@Override
	public void play() throws NotPlayableException {
		try {
			BasicPlayer.play(super.getPathname());
		} catch (Exception e) {
			throw new NotPlayableException(super.getPathname(), "File can't be read!");
		}
		
	}

	@Override
	public void togglePause() {
		BasicPlayer.togglePause();
	}

	@Override
	public void stop() {
		BasicPlayer.stop();
	}

	@Override
	public String formatDuration() {
		return timeFormatter(this.duration);
	}

	@Override
	public String formatPosition() {
		long time = (long) BasicPlayer.getPosition();
		return timeFormatter(time);
	}
	
	public static String timeFormatter(long timeInMicroSeconds) {
		if (timeInMicroSeconds <= 5999999999L && timeInMicroSeconds >= 0) {
			long min = timeInMicroSeconds / 60000000;
			long sec = (timeInMicroSeconds - min * 60000000) / 1000000;
			return String.format("%02d:%02d", min, sec);
		} else {
			throw new RuntimeException("Can't convert the time!");
		}
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

}
