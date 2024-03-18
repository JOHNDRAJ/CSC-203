import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.Random;


public class Builder extends Entity implements Behavioral, Animateable{
    public static final String BUILDER_KEY = "builder";
    public static final double BUILDER_BEHAVIOR_PERIOD = 2;
    public static final double BUILDER_ANIMATION_PERIOD = 3;
    private double animationPeriod;
    private double behaviorPeriod;

    private boolean found;
    private boolean previousFarmed;
    public boolean seedNext = true;
    private Point previousPoint;

    public Builder(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
        this.behaviorPeriod = behaviorPeriod;
        this.found = false;
        this.previousFarmed = false;
        this.previousPoint = null;

    }

    public boolean moveTo(World world, EventScheduler scheduler) {
        Point p = getPosition();
        List<Class<?>> toAvoid = new ArrayList<>(List.of(Tree.class, Sapling.class, House.class, Barn.class, Castle.class, Seed.class, Sapling.class, Wheat.class));
        Optional<Entity> closest = world.findNearest(p, toAvoid);
        int distanceAway = closest.get().getPosition().manhattanDistanceTo(p);
        if (!found && world.isolated(p, toAvoid)){
            //System.out.println("test");
            found = true;
            String backgroundID = world.getBackgroundCell(getPosition()).getId();
            if (backgroundID.equals("farmland")){
                setBehaviorPeriod(5);
                previousFarmed = true;
            }
            else if (((backgroundID.equals("grass")) || backgroundID.equals("grass_flowers"))){
                setBehaviorPeriod(60);
                previousFarmed = true;
            }
            return true;
        }
        else{
            //found = false;
            Point nextPos = nextPosition(world, getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    public Point nextPosition(World world, Point destination){
            Predicate<Point> canPassThrough = p -> (
                    world.inBounds(p) && (!world.isOccupied(p) || world.getOccupant(p).get() instanceof Stump)
            );

            Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

            Point p = getPosition();
            List<Class<?>> toAvoid = new ArrayList<>(List.of(Tree.class, Sapling.class, House.class, Barn.class, Castle.class, Seed.class, Sapling.class, Wheat.class));
            Optional<Entity> closest = world.findNearest(p, toAvoid);
            int distanceAway = closest.get().getPosition().manhattanDistanceTo(p);
            if (!found && world.isolated(p, toAvoid)) {
                return getPosition();
            }
            found = false;
            List<Point> possibleNext = potentialNeighbors.apply(p).filter(canPassThrough).toList();
            if (possibleNext.isEmpty()){
                return getPosition();
            }
            else {
                Random random = new Random();
                int randomInt = random.nextInt(possibleNext.size());
                return possibleNext.get(randomInt);
            }
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler, world, imageLibrary);
        scheduleBehavior(scheduler, world, imageLibrary);
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

    @Override
    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    /** Executes Dude specific Logic. */
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Point previousPoint = getPosition();
        String backgroundID = world.getBackgroundCell(getPosition()).getId();
        if (previousFarmed && (backgroundID.equals("grass") || backgroundID.equals("grass_flowers"))) {
            Background bg = new Background("farmland", imageLibrary.get("farmland"), 0);
            world.setBackgroundCell(getPosition(), bg);
        }
            //moveTo(world, scheduler);
        else {
            previousFarmed = false;
            moveTo(world, scheduler);
            if (seedNext) {
                Seed seed = new Seed(
                        Seed.SEED_KEY + "_" + "seed",
                        previousPoint,
                        imageLibrary.get(Seed.SEED_KEY)
                );
                if (!world.isOccupied(previousPoint) && world.getBackgroundCell(previousPoint).getId().equals("farmland")) {
                    world.addEntity(seed);
                    seed.scheduleActions(scheduler, world, imageLibrary);
                    seedNext = false;
                }
            }
            else{
                Wheat wheat = new Wheat(
                        Wheat.WHEAT_KEY + "_" + "wheat",
                        previousPoint,
                        imageLibrary.get(Wheat.WHEAT_KEY)
                );
                if (!world.isOccupied(previousPoint) && world.getBackgroundCell(previousPoint).getId().equals("farmland")) {
                    world.addEntity(wheat);
                    wheat.scheduleActions(scheduler, world, imageLibrary);
                    seedNext = true;
                }
            }
        }
        scheduleBehavior(scheduler, world, imageLibrary);
        setBehaviorPeriod(2);
        //setBehaviorPeriod(2);
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
}