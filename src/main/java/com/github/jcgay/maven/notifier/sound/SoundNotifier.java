package com.github.jcgay.maven.notifier.sound;

import com.github.jcgay.maven.notifier.AbstractCustomEventSpy;
import com.github.jcgay.maven.notifier.Notifier;
import com.github.jcgay.maven.notifier.Status;
import org.apache.maven.execution.MavenExecutionResult;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

@Component(role = Notifier.class, hint = "sound")
public class SoundNotifier extends AbstractCustomEventSpy {

    @Override
    public void onEvent(MavenExecutionResult event) {
        AudioInputStream ais = getAudioStream(getBuildStatus(event));
        if (ais == null) {
            logger.warn("Cannot get a sound to play. Skipping notification...");
            return;
        }
        play(ais);
    }

    private void play(AudioInputStream ais) {
        try {
            Clip clip = AudioSystem.getClip();
            EndListener listener = new EndListener();
            clip.addLineListener(listener);
            clip.open(ais);
            playAndWait(clip, listener);
        } catch (LineUnavailableException e) {
            fail(e);
        } catch (IOException e) {
            fail(e);
        } finally {
            IOUtil.close(ais);
        }
    }

    private void playAndWait(Clip clip, EndListener listener) {
        try {
            clip.start();
            listener.waitEnd();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            clip.close();
        }
    }

    private void fail(Exception e) {
        logger.debug("Error playing sound.", e);
    }

    private AudioInputStream getAudioStream(Status success) {
        try {
            return AudioSystem.getAudioInputStream(getUrl(success));
        } catch (UnsupportedAudioFileException e) {
            return noAudioStream(e);
        } catch (IOException e) {
            return noAudioStream(e);
        }
    }

    private AudioInputStream noAudioStream(Exception e) {
        logger.warn("Error reading audio stream.", e);
        return null;
    }

    private InputStream getUrl(Status status) {
        String sound = status == Status.SUCCESS ? "/109662__grunz__success.wav" : "/Sad_Trombone-Joe_Lamb-665429450.wav";
        return getClass().getResourceAsStream(sound);
    }
}
