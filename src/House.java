import processing.core.PImage;

import java.util.List;

public class House extends Entity implements Behavioral, Building{
    public static final String HOUSE_KEY = "house";

    public static final int HOUSE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int HOUSE_PARSE_PROPERTY_COUNT = 1;

    public static final int HOUSE_UPGRADE_STATUS_LIMIT = 15;

    private int upgradeStatus;

    private double behaviorPeriod;

    public House(String id, Point position, List<PImage> images, double behaviorPeriod) {
        super(id, position, images);
        this.upgradeStatus = 0;
        this.behaviorPeriod = behaviorPeriod;
    }

    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary){
        if (upgradeStatus >= House.HOUSE_UPGRADE_STATUS_LIMIT) {
            Barn barn = new Barn(
                    Barn.BARN_KEY + "_" + getId(),
                    getPosition(),
                    imageLibrary.get(Barn.BARN_KEY),
                    behaviorPeriod
            );

            world.removeEntity(scheduler, this);

            world.addEntity(barn);
            barn.scheduleActions(scheduler, world, imageLibrary);

            /*
            if (!(world.isOccupied(new Point(barn.getPosition().x, barn.getPosition().y-1)))){
                Builder builder = new Builder(
                    Builder.BUILDER_KEY + "_" + getId(),
                    new Point(barn.getPosition().x, barn.getPosition().y - 1),
                    imageLibrary.get(Builder.BUILDER_KEY),
                    Builder.BUILDER_ANIMATION_PERIOD,
                    Builder.BUILDER_BEHAVIOR_PERIOD
                );

                world.addEntity(builder);
                builder.scheduleActions(scheduler, world, imageLibrary);
            }
             */




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

    @Override
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

    public void setBehaviorPeriod(double d){
        this.behaviorPeriod = d;
    }

    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }
}
