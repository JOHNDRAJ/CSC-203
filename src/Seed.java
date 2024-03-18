import processing.core.PImage;

import java.util.List;

public class Seed extends Entity implements Behavioral{
    public static final double SEED_BEHAVIOR_PERIOD = 50;
    public static final String SEED_KEY = "seed";
    private double behaviorPeriod;
    private boolean pauseRequired;
    public Seed(String id, Point position, List<PImage> images) {
        super(id, position, images);
        this.behaviorPeriod = SEED_BEHAVIOR_PERIOD;
        this.pauseRequired = true;
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary){

        Sapling sapling = new Sapling(
                Sapling.SAPLING_KEY + "_" + getId(),
                getPosition(),
                imageLibrary.get(Sapling.SAPLING_KEY)
        );
        world.removeEntity(scheduler, this);

        world.addEntity(sapling);
        sapling.scheduleActions(scheduler, world, imageLibrary);
        return true;
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
            transform(world, scheduler, imageLibrary);
            //scheduleBehavior(scheduler, world, imageLibrary);
    }

    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }
    public void setBehaviorPeriod(double d){
        this.behaviorPeriod = d;
    }

    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }

}
