public class Behavior implements Action{
    private World world;
    private ImageLibrary imageLibrary;
    private Entity entity;

    public Behavior(Entity entity, World world, ImageLibrary imageLibrary) {
        this.entity = entity;
        this.world = world;
        this.imageLibrary = imageLibrary;
    }

    /** Performs 'Behavior' specific logic. */
    @Override
    public void execute(EventScheduler scheduler) {
        ((Behavioral)entity).executeBehavior(world, imageLibrary, scheduler);
    }
}


