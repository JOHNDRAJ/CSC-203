public interface Animateable {
    void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary);
    void updateImage();

    double getAnimationPeriod();
    void setAnimationPeriod(double d);
}
