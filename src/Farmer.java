import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Farmer extends Dude{
    public static final String FARMER_KEY = "farmer";
    // Constant save file column positions for properties corresponding to speicific entity types.
    // Do not use these values directly in any constructors or 'create' methods.
    public static final int FARMER_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int FARMER_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int FARMER_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 10;
    public static final double FARMER_BEHAVIOR_PERIOD = 2;
    public static final double FARMER_ANIMATION_PERIOD = 4;
    public static final int FARMER_PARSE_PROPERTY_COUNT = 3;


    public Farmer(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, animationPeriod, behaviorPeriod, resourceCount, 5);
    }

    @Override
    /** Returns the (optional) entity a Dude will path toward. */
    public Optional<Entity> findDudeTarget(World world) {
        List<Class<?>> potentialTargets;

        if (getRescourceCount() == getRescourceLimit()) {
            potentialTargets = List.of(Barn.class);
        } else {
            potentialTargets = List.of(Wheat.class);
        }

        return world.findNearest(getPosition(), potentialTargets);
    }

    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Wheat) {
                ((Healthy)target).setHealth(((Healthy)target).getHealth() - 1);
            }
            else if (target instanceof Barn barn) {
                barn.setWheatUpgradeStatus(barn.getWheatUpgradeStatus() + 1);
            }
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    /** Changes the Dude's graphics. */
    @Override
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (getRescourceCount() < getRescourceLimit()) {
            setResourceCount(getRescourceCount() + 1);
            if (getRescourceCount() == getRescourceLimit()) {
                Farmer farmer = new Farmer(getId(), getPosition(), imageLibrary.get(Farmer.FARMER_KEY), getAnimationPeriod(), getBehaviorPeriod(), getRescourceCount(), getRescourceLimit());

                world.removeEntity(scheduler, this);

                world.addEntity(farmer);
                farmer.scheduleActions(scheduler, world, imageLibrary);


                return true;
            }
        }
        else {
            Farmer farmer = new Farmer(getId(), getPosition(), imageLibrary.get(Farmer.FARMER_KEY), getAnimationPeriod(), getBehaviorPeriod(), 0, getRescourceLimit());

            world.removeEntity(scheduler, this);

            world.addEntity(farmer);
            farmer.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

}
