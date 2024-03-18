import processing.core.PImage;

import java.util.List;

public class Castle extends Entity implements Building, Animateable{
    public static final String CASTLE_KEY = "castle";
    public static final int CASTLE_PARSE_PROPERTY_COUNT = 0;

    public int upgradeStatus;
    public double behaviorPeriod;
    public double animationPeriod;

    public Castle(String id, Point position, List<PImage> images, double behaviorPeriod){
        super(id, position, images);
        this.upgradeStatus = 0;
        this.behaviorPeriod = behaviorPeriod;
        this.animationPeriod = 5;
    }

    /** Called when an animation action occurs. */
    @Override
    public void updateImage() {
        setImageIndex(getImageIndex() + 1);
    }

    /** Begins all animation updates for the entity. */
    @Override
    public void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        //transform(world, scheduler, imageLibrary);
        //scheduleBehavior(scheduler, world, imageLibrary);
        scheduleAnimation(scheduler, world, imageLibrary);
    }


    public int getUpgradeStatus(){
        return upgradeStatus;
    }

    public void setUpgradeStatus(int i){
        upgradeStatus = i;
    }

    @Override
    public double getAnimationPeriod(){
        return animationPeriod;
    }

    @Override
    public void setAnimationPeriod(double d){
        animationPeriod = d;
    }
    public void setBehaviorPeriod(double d){
        this.behaviorPeriod = d;
    }

    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }
}
