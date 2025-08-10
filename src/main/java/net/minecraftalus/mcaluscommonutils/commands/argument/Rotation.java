package net.minecraftalus.mcaluscommonutils.commands.argument;

public class Rotation {
    private double yaw;
    private double pitch;
    public Rotation(double yaw, double pitch) {
        this.yaw=yaw;
        this.pitch=pitch;
    }

    public double getYaw() {
        return yaw;
    }
    public double getPitch() {
        return pitch;
    }

    public void setYaw(double yaw) {
        this.yaw=yaw;
    }
    public void setPitch(double pitch) {
        this.pitch=pitch;
    }

    public String toString() {
        return String.format("Rotation{ Yaw:%.2f, Pitch:%.2f }", this.yaw, this.pitch);
    }
}
