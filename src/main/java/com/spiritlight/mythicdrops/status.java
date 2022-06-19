package com.spiritlight.mythicdrops;

public class status {
    private boolean b;

    public status() {
    }

    public void on() {
        this.b = true;
    }

    public void off() {
        this.b = false;
    }

    public boolean check() {
        return b;
    }
}
