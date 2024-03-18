import processing.core.PImage;

import java.util.List;

public class Barn extends Entity implements Building, Behavioral{
    public static final String BARN_KEY = "barn";
    public static final int BARN_PARSE_PROPERTY_COUNT = 0;

    public static final int BARN_UPGRADE_STATUS_LIMIT = 30;
    public static final int BARN_WHEAT_UPGRADE_STATUS_LIMIT = 30;
    private int upgradeStatus;
    private int wheatUpgradeStatus;
    double behaviorPeriod;

    public Barn(String id, Point position, List<PImage> images, double behaviorPeriod) {
        super(id, position, images);
        this.upgradeStatus = 0;
        this.wheatUpgradeStatus = 0;
        this.behaviorPeriod = behaviorPeriod;
        //System.out.println("test");
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary){
        if ((upgradeStatus >= Barn.BARN_UPGRADE_STATUS_LIMIT) && (wheatUpgradeStatus >= Barn.BARN_WHEAT_UPGRADE_STATUS_LIMIT)) {
            Castle castle = new Castle(
                    Castle.CASTLE_KEY + "_" + getId(),
                    getPosition(),
                    imageLibrary.get(Castle.CASTLE_KEY),
                    behaviorPeriod
            );

            world.removeEntity(scheduler, this);

            world.addEntity(castle);
            castle.scheduleAnimation(scheduler, world, imageLibrary);


            return true;
        }

        return false;
    }

    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        //upgradeStatus = upgradeStatus + 1;
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    public int getUpgradeStatus(){
        return upgradeStatus;
    }

    public void setUpgradeStatus(int i){
        upgradeStatus = i;
    }

    public int getWheatUpgradeStatus(){
        return wheatUpgradeStatus;
    }

    public void setWheatUpgradeStatus(int i){ wheatUpgradeStatus= i;}
    public void setBehaviorPeriod(double d){
        this.behaviorPeriod = d;
    }

    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }

}
