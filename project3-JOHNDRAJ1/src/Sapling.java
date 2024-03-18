import processing.core.PImage;

import java.util.List;

public class Sapling extends Entity implements Behavioral, Animateable, Transformable, Healthy{
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_PARSE_PROPERTY_COUNT = 0;
    // Constant limits and default values for specific entity types.
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final double SAPLING_BEHAVIOR_PERIOD = 2.0;
    public static final double SAPLING_ANIMATION_PERIOD = 0.01; // Very small to react to health changes
    private double animationPeriod;
    private double behaviorPeriod;
    private int health;

    public Sapling(String id, Point position, List<PImage> images) {
        super(id, position, images);
        this.animationPeriod = SAPLING_ANIMATION_PERIOD;
        this.behaviorPeriod = SAPLING_BEHAVIOR_PERIOD;
        this.health = 0;
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
    }
    @Override
    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    /** Executes Sapling specific Logic. */
    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        health = health + 1;
        if (!transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Called when an animation action occurs. */
    @Override
    public void updateImage() {
        if (health <= 0) {
            setImageIndex(0);
        } else if (health < Sapling.SAPLING_HEALTH_LIMIT) {
            setImageIndex(getImages().size() * health / Sapling.SAPLING_HEALTH_LIMIT);
        } else {
            setImageIndex(getImages().size() - 1);
        }
    }

    /** Begins all animation updates for the entity. */
    @Override
    public void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    /** Checks the Sapling's health and transforms accordingly, returning true if successful. */
    @Override
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Stump stump = new Stump(Stump.STUMP_KEY + "_" + getId(), getPosition(), imageLibrary.get(Stump.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (health >= Sapling.SAPLING_HEALTH_LIMIT) {
            Tree tree = new Tree(
                    Tree.TREE_KEY + "_" + getId(),
                    getPosition(),
                    imageLibrary.get(Tree.TREE_KEY),
                    NumberUtil.getRandomDouble(Tree.TREE_RANDOM_ANIMATION_PERIOD_MIN, Tree.TREE_RANDOM_ANIMATION_PERIOD_MAX), NumberUtil.getRandomDouble(Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MIN, Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MAX),
                    NumberUtil.getRandomInt(Tree.TREE_RANDOM_HEALTH_MIN, Tree.TREE_RANDOM_HEALTH_MAX)
            );

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    @Override
    public double getBehaviorPeriod(){
        return behaviorPeriod;
    }

    @Override
    public void setBehaviorPeriod(double d){
        behaviorPeriod = d;
    }

    @Override
    public double getAnimationPeriod(){
        return animationPeriod;
    }

    @Override
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
