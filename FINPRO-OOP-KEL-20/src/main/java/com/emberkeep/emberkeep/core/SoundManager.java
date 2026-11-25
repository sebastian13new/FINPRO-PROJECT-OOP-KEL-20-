package com.emberkeep.emberkeep.core;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class SoundManager {
    private static SoundManager instance;

    private final Map<String, AudioClip> sfxCache = new HashMap<>();
    private MediaPlayer bgmPlayer;
    private BgmTrack currentTrack;

    private double sfxVolume = 0.6;
    private double bgmVolume = 0.4;
    private boolean muted = false;

    private SoundManager() { }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private URL findAudio(String fileName) {
        return SoundManager.class.getResource("/audio/" + fileName);
    }

    public void playSfx(String fileName) {
        if (muted) return;
        AudioClip clip = sfxCache.get(fileName);
        if (clip == null) {
            URL url = findAudio(fileName);
            if (url == null) {
                System.out.println("[SoundManager] File SFX tidak ditemukan: " + fileName);
                return;
            }
            clip = new AudioClip(url.toExternalForm());
            sfxCache.put(fileName, clip);
        }
        clip.play(sfxVolume);
    }

    public void playBgm(BgmTrack track) {
        if (track == null || track == currentTrack) return;
        stopBgm();
        URL url = findAudio(track.getFileName());
        if (url == null) {
            System.out.println("[SoundManager] File BGM tidak ditemukan: " + track.getFileName());
            return;
        }
        bgmPlayer = new MediaPlayer(new Media(url.toExternalForm()));
        bgmPlayer.setCycleCount(track.isLoop() ? MediaPlayer.INDEFINITE : 1);
        bgmPlayer.setVolume(muted ? 0 : bgmVolume);
        bgmPlayer.play();
        currentTrack = track;
    }

    public void stopBgm() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
            bgmPlayer = null;
        }
        currentTrack = null;
    }

    public void toggleMute() {
        muted = !muted;
        if (bgmPlayer != null) bgmPlayer.setVolume(muted ? 0 : bgmVolume);
    }

    public boolean isMuted() { return muted; }
    public BgmTrack getCurrentTrack() { return currentTrack; }
}
