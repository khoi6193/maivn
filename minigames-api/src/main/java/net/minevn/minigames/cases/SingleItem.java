package net.minevn.minigames.cases;

public class SingleItem {
	private int rate, stackedRate;
	private boolean preview;
	private boolean announce;



	public double getRate() {
		return rate;
	}

	public void setStackedRate(int rate) {
		stackedRate = rate;
	}

	public double getStackedRate() {
		return stackedRate;
	}

	public boolean isPreview() {
		return preview;
	}

	public boolean toBeAnnounced() {
		return announce;
	}
}
