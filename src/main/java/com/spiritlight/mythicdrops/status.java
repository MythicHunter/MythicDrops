package com.spiritlight.mythicdrops;

public class status {
    private byte b;

    public status(byte b) {
        this.b = b;
    }

    public void on() {
        this.b = 1;
    }

    public void off() {
        this.b = 0;
    }

    public boolean check() {
        return (b==(byte)1);
    }
}
