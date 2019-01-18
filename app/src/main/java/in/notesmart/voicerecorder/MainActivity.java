package in.notesmart.voicerecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button start_record, stop_record, play_recording, stop_recording;
    String path_save = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private final int REQUEST_PERMISSION = 1;

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_aurdio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_aurdio_result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION :
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start_record = findViewById(R.id.start_record);
        stop_record = findViewById(R.id.stop_record);
        play_recording = findViewById(R.id.play_recording);
        stop_recording = findViewById(R.id.stop_recording);

        if (checkPermissionFromDevice()){
            start_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                    path_save = Environment.getExternalStorageDirectory()
                            .getAbsolutePath()
                            +"/"
                            + UUID.randomUUID().toString()
                            +"audio_record.3gp";
                    setupMediaRecorder();
                    try{
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    play_recording.setEnabled(false);
                    stop_recording.setEnabled(false);
                    Snackbar.make(v, "Recording....", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });
            stop_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    stop_record.setEnabled(false);
                    play_recording.setEnabled(true);
                    start_record.setEnabled(true);
                    stop_recording.setEnabled(false);
                }
            });
            play_recording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stop_recording.setEnabled(true);
                    stop_record.setEnabled(false);
                    start_record.setEnabled(false);
                    play_recording.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(path_save);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Snackbar.make(v, "Playing....", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            stop_recording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stop_recording.setEnabled(false);
                    stop_record.setEnabled(false);
                    start_record.setEnabled(true);
                    play_recording.setEnabled(true);
                    if (mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });
        }else {
            requestPermission();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(path_save);
    }
}
