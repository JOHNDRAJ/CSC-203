import processing.core.PImage;

import java.util.List;

public class Wheat extends Entity implements Healthy, Behavioral{

    public static final String WHEAT_KEY = "wheat";
    public static final double WHEAT_BEHAVIOR_PERIOD = 2;
    public static final double WHEAT_ANIMATION_PERIOD = 3; // Very small to react to health changes
    private double animationPeriod;
    private double behaviorPeriod;
    private int health;
    public Wheat(String id, Point position, List<PImage> images){
        super(id, position, images);
        this.animationPeriod = WHEAT_ANIMATION_PERIOD;
        this.behaviorPeriod = WHEAT_BEHAVIOR_PERIOD;
        this. health = 5;
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {

            world.removeEntity(scheduler, this);

            return true;
        }

        return false;
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        //scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }
    @Override
    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    @Override
    /** Executes Tree specific Logic. */
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }



    @Override
    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }

    @Override
    public void setBehaviorPeriod(double d){
        behaviorPeriod = d;
    }

    public double getAnimationPeriod(){
        return animationPeriod;
    }

    public void setAnimationPeriod(double d){
        animationPeriod = d;
    }

    @Override
    public int getHealth(){
        return health;
    }
    @Override
    public void setHealth(int i){
        health = i;
    }

}


