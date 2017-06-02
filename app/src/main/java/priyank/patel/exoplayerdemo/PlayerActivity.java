package priyank.patel.exoplayerdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  // bandwidth meter to measure and estimate bandwidth
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final String TAG = "PlayerActivity";

  public static final String VIDEO_TYPE = "video_type";
  public static final String VIDEO_URI = "video_uri";
  public static final String SUBTITLE_URI = "subtitle_uri";

  public static final int SIMPLE_VIDEO        = 101;
  public static final int VIDEO_WITH_SUBTITLE = 102;

  private SimpleExoPlayer player;
  private SimpleExoPlayerView playerView;

  private long playbackPosition;
  private int currentWindow;
  private boolean playWhenReady = true;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);

    playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  private void initializePlayer() {
    if (player == null) {
      // a factory to create an AdaptiveVideoTrackSelection
      TrackSelection.Factory adaptiveTrackSelectionFactory =
              new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
      // using a DefaultTrackSelector with an adaptive video selection factory
      player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
              new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
      playerView.setPlayer(player);
      player.setPlayWhenReady(playWhenReady);
      player.seekTo(currentWindow, playbackPosition);
    }

    int videoType = getIntent().getIntExtra(VIDEO_TYPE, SIMPLE_VIDEO);
    String videoUri = getIntent().getStringExtra(VIDEO_URI);
    String subtitleUri = getIntent().getStringExtra(SUBTITLE_URI);
    Log.d(TAG, "initializePlayer videoUri : " + videoUri + ", subtitleUri : " + subtitleUri);

    MediaSource mediaSource = null;
    if( videoType == SIMPLE_VIDEO ) {
      mediaSource = buildMediaSource(Uri.parse(videoUri));
    }
    else if( videoType == VIDEO_WITH_SUBTITLE ) {
      mediaSource = buildMergingMediaSource(Uri.parse(videoUri), Uri.parse(subtitleUri));
    }

    player.prepare(mediaSource, true, false);
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.release();
      player = null;
    }
  }

  private MediaSource buildMediaSource(Uri uri) {
    // Measures bandwidth during playback. Can be null if not required.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    // Produces DataSource instances through which media data is loaded.
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerDemo"), bandwidthMeter);
    // Produces Extractor instances for parsing the media data.
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    // This is the MediaSource representing the media to be played.
    return new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
  }

  private MediaSource buildMergingMediaSource(Uri videoUri,Uri subtitleUri) {
    // Measures bandwidth during playback. Can be null if not required.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    // Produces DataSource instances through which media data is loaded.
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerDemo"), bandwidthMeter);
    // Produces Extractor instances for parsing the media data.
    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
    MediaSource videoSource = new ExtractorMediaSource(videoUri,dataSourceFactory, extractorsFactory, null, null);

    Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
            null, Format.NO_VALUE, Format.NO_VALUE, "en", null);

    MediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, dataSourceFactory, textFormat, C.TIME_UNSET);
    // Plays the video with the sideloaded subtitle.
    return new MergingMediaSource(videoSource, subtitleSource);
  }



}
