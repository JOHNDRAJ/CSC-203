import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Dude extends Entity implements Behavioral, Animateable, Transformable, Moveable{
    // Constant string identifiers for the corresponding type of entity.
    // Used to identify entities in the save file and retrieve image information.
    public static final String DUDE_KEY = "dude";
    // Constant save file column positions for properties corresponding to speicific entity types.
    // Do not use these values directly in any constructors or 'create' methods.
    public static final int DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 2;
    public static final int DUDE_PARSE_PROPERTY_COUNT = 3;
    private double animationPeriod;
    private double behaviorPeriod;
    private int resourceCount;
    private int resourceLimit;

    public Dude(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
        this.behaviorPeriod = behaviorPeriod;
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
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

    /** Executes Dude specific Logic. */
    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = findDudeTarget(world);
        if (dudeTarget.isEmpty() || !moveTo(world, dudeTarget.get(), scheduler) || !transform(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
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

    /** Returns the (optional) entity a Dude will path toward. */
    public Optional<Entity> findDudeTarget(World world) {
        List<Class<?>> potentialTargets;

        if (resourceCount == resourceLimit) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Tree.class, Sapling.class);
        }

        return world.findNearest(getPosition(), potentialTargets);
    }

    /** Changes the Dude's graphics. */
    @Override
    public boolean transform(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (resourceCount < resourceLimit) {
            resourceCount += 1;
            if (resourceCount == resourceLimit) {
                Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(Dude.DUDE_KEY + "_carry"), animationPeriod, behaviorPeriod, resourceCount, resourceLimit);

                world.removeEntity(scheduler, this);

                world.addEntity(dude);
                dude.scheduleActions(scheduler, world, imageLibrary);


                return true;
            }
        } else {
            Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(Dude.DUDE_KEY), animationPeriod, behaviorPeriod, 0, resourceLimit);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    /** Attempts to move the Dude toward a target, returning True if already adjacent to it. */
    @Override
    public boolean moveTo(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Tree || target instanceof Sapling) {
                ((Healthy)target).setHealth(((Healthy)target).getHealth() - 1);
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

    /** Determines a Dude's next position when moving. */
    @Override
    public Point nextPosition(World world, Point destination) {
        PathingStrategy pathingStrategy = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p -> (
                world.inBounds(p) && (!world.isOccupied(p) || world.getOccupant(p).get() instanceof Stump)
        );

        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.manhattanDistanceTo(p2) == 1;

        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, potentialNeighbors);
        if (path.isEmpty()){
            return getPosition();
        }
        else{
            return path.get(0);
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

    @Override
    public double getAnimationPeriod(){
        return animationPeriod;
    }

    @Override
    public void setAnimationPeriod(double d){
        animationPeriod = d;
    }

    // A lambda function that determines if two points are adjacent to each another
    // Can be simplified if you examine the Point class
    BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.manhattanDistanceTo(p2) == 1;

    // A lambda function that produces a stream of neighboring points for a given point
    // Currently, it only provides the adjacent right neighbor
    // Take a look at the type of PathingStrategy.CARDINAL_NEIGHBORS
    /* Could have an issue where out of bounds points are considers*/
    Function<Point, Stream<Point>> potentialNeighbors = point ->
            Stream.<Point>builder()
                    .add(new Point(point.x, point.y - 1))
                    .add(new Point(point.x, point.y + 1))
                    .add(new Point(point.x - 1, point.y))
                    .add(new Point(point.x + 1, point.y))
                    .build();
}
