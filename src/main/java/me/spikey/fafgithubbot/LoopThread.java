package me.spikey.fafgithubbot;

public class LoopThread extends Thread {
    private final Main main;
    private final int loopDelay;

    public LoopThread(Main main, int loopDelay) {
        super();
        this.main = main;

        this.loopDelay = loopDelay;
    }

    public void run() {
        while (true) {
            try {
                main.makeUnmadePrs();
                sleep(loopDelay);
                System.out.println("Updating!");
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
