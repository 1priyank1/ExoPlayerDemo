package priyank.patel.exoplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button1, button2, button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);

        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.button1:
                intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VIDEO_TYPE, PlayerActivity.SIMPLE_VIDEO);
                intent.putExtra(PlayerActivity.VIDEO_URI, "http://html5demos.com/assets/dizzy.mp4");
                startActivity(intent);
                break;

            case R.id.button2:
                intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VIDEO_TYPE, PlayerActivity.VIDEO_WITH_SUBTITLE);
                intent.putExtra(PlayerActivity.VIDEO_URI, "http://www.storiesinflight.com/js_videosub/jellies.mp4");
                intent.putExtra(PlayerActivity.SUBTITLE_URI, "http://www.storiesinflight.com/js_videosub/jellies.srt");
                startActivity(intent);
                break;

            case R.id.button3:
                intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VIDEO_TYPE, PlayerActivity.LOOPING_VIDEO);
                intent.putExtra(PlayerActivity.VIDEO_URI, "http://html5demos.com/assets/dizzy.mp4");
                startActivity(intent);
                break;

            case R.id.button4:
                intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VIDEO_TYPE, PlayerActivity.SEQUENTIAL_VIDEO);
                String[] videoUriList = {"http://html5demos.com/assets/dizzy.mp4", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"};
                intent.putExtra(PlayerActivity.VIDEO_URI_LIST, videoUriList);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
