package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {
    private TextView textView;
    Button talkButton;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    private Vibrator vibrator;
    //private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        textView = findViewById(R.id.text);
        talkButton = findViewById(R.id.talkClick);
        //Create an OnClickListener//

        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onClickMessage = "I just sent the handheld a message " + sentMessageNumber++;
                textView.setText(onClickMessage);
                //Use the same path//

                String datapath = "/my_path";
                new SendMessage(datapath, onClickMessage).start();

            }
        });
        //Register to receive local broadcasts, which we'll be creating in the next step//

        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

    }
    public class Receiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {

//Display the following when a new message is received//

            String onMessageReceived = "I just received a message from the handheld " + receivedMessageNumber++;
            textView.setText(onMessageReceived);
            int dot = 200;      // Length of a Morse Code "dot" in milliseconds

            int dot1 = 300;     // Length of a Morse Code "dash" in milliseconds

            int dot2= 400;

            int dot3= 500;

            int dot4= 600;

            int dot5= 700;

            int short_gap = 500;    // Length of Gap Between dots/dashes

            int medium_gap = 500;   // Length of Gap Between Letters

            int long_gap = 1000;    // Length of Gap Between Words

            long[] pattern = {
                    0,  // Start immediately

                    dot,
                    short_gap,
                    dot,
                    short_gap,
                    dot,
                    short_gap,
                    dot,
                    short_gap,
                    dot,
                    short_gap,
                    dot,
                    short_gap
            };
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));

        }
    }

    class SendMessage extends Thread {
        String path;
        String message;
        //Constructor for sending information to the Data Layer//

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

//Retrieve the connected devices//

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

//Block on a task and get the result synchronously//

                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {

//Send the message///

                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);
                        //Handle the errors//

                    } catch (ExecutionException exception) {

//TO DO//

                    } catch (InterruptedException exception) {

//TO DO//

                    }

                }

            } catch (ExecutionException exception) {

//TO DO//

            } catch (InterruptedException exception) {

//TO DO//

            }
        }
    }
    }

