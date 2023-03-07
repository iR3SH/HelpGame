package org.starloco.locos.util;

import java.util.ArrayList;
import java.util.Random;

public class RandomStats<Stats> {
	
	private ArrayList<Stats> randoms;
	
	public RandomStats() {
		this.randoms = new ArrayList<>();
	}
	
	public void add(int pct, Stats object) {
		for(int i = 0; i < pct; i++)
			this.randoms.add(object);
	}

    public int size() {
        return randoms.size();
    }
	
	public Stats get() {
		return this.randoms.get(new Random().nextInt(this.randoms.size()));
	}
}