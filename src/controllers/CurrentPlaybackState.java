package controllers;


public class CurrentPlaybackState extends Thread {

    @Override
    public void run(){
        while (API_Data.playbackTimerStarted) {

            API_Data.getPlaybackState();

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                System.out.println("Current playback state interrupted");
            }
        }
    }
}