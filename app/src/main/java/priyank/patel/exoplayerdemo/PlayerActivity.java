package priyank.patel.exoplayerdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  // bandwidth meter to measure and estimate bandwidth
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final String TAG = "PlayerActivity";

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

    String uri = getIntent().getStringExtra("URI");
    Log.d(TAG, "initializePlayer URI : " + uri);

    MediaSource mediaSource = buildMediaSource(Uri.parse(uri));
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

}
