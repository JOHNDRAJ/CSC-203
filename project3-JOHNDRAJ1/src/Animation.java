public class Animation implements Action{
    private int repeatCount;
    private Entity entity;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    /** Performs 'Animation' specific logic. */
    @Override
    public void execute(EventScheduler scheduler) {
        ((Animateable)entity).updateImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, new Animation(this.entity, Math.max(this.repeatCount - 1, 0)), ((Animateable)entity).getAnimationPeriod());
        }
    }
}
