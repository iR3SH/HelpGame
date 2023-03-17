package org.starloco.locos.fight.ia;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Locos on 18/09/2015.
 */
public abstract class AbstractIA implements IA {

    private final ScheduledExecutorService executor;

    protected Fight fight;
    protected Fighter fighter;
    protected boolean stop;
    protected byte count;

    public AbstractIA(Fight fight, Fighter fighter, byte count) {
        this.fight = fight;
        this.fighter = fighter;
        this.count = count;
        this.executor = Executors.newSingleThreadScheduledExecutor( r -> {
            Thread thread = new Thread(r);
            thread.setName(AbstractIA.class.getName());
            return thread;
        });
    }

    public Fight getFight() {
        return fight;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void endTurn() {
        if (this.stop && !this.fighter.isDead()) {
            if (this.fighter.haveInvocation()) {
                this.addNext(() -> {
                    this.fight.endTurn(false, this.fighter);
                    this.executor.shutdownNow();
                }, 0); // 1000 to 0 by coding mestre (vérifier si à induit des bugs)
            } else {
                this.fight.endTurn(false, this.fighter);
                this.executor.shutdownNow();
            }
        } else {
            if(!this.fight.isFinish())
                this.addNext(this::endTurn, 0); // 500 to 0 by coding mestre (vérifier si à induit des bugs)
            else
                this.executor.shutdownNow();
        }
    }

    protected void decrementCount() {
        this.count--;
        this.apply();
    }

    public void addNext(Runnable runnable, Integer time) {
    	while(this.fight.isCurAction() || this.fight.isTraped())
			try {
				time -= 20;
				Thread.sleep(20);
			} catch (InterruptedException e) {}
        executor.schedule(runnable,time < 0 ? 0 : time,TimeUnit.MILLISECONDS);
    }
}
