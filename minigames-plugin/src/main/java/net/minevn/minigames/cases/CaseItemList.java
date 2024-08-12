package net.minevn.minigames.cases;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CaseItemList {
	private final List<SingleItem> items;
	private int stackedRate = 0;

	public CaseItemList(List<SingleItem> items) {
		for (SingleItem i : items) {
			i.setStackedRate(stackedRate);
			stackedRate += i.getRate();
		}
		this.items = items.stream().sorted(Comparator.comparingDouble(SingleItem::getStackedRate).reversed())
				.collect(Collectors.toList());
	}

	public double getTotalRate() {
		return stackedRate;
	}

	public List<SingleItem> getItems() {
		return items;
	}

	public SingleItem getRandomItem() {
		int random = new Random().nextInt(stackedRate);
		return items.stream().filter(x -> x.getStackedRate() <= random).findFirst().orElse(null);
	}
}
