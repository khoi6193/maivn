package net.minevn.minigames.cases;

import net.minevn.minigames.award.PlayerAward;

public class SingleItem {
	private PlayerAward item;
	private int rate, stackedRate;
	private boolean preview;
	private boolean announce;

	public SingleItem(PlayerAward item, int rate, boolean preview, boolean announce) {
		this.item = item;
		this.rate = rate;
		stackedRate = -1;
		this.preview = preview;
		this.announce = announce;
	}

	public PlayerAward getItem() {
		return item;
	}

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
