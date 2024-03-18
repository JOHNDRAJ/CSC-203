public interface Behavioral {
    void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary);
    void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary);
    void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler);

    double getBehaviorPeriod();
    void setBehaviorPeriod(double d);
}
