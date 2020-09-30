package com.example.myapplication;

//import androidx.appcompat.app.AppCompatActivity;

//import android.os.Bundle;
//import android.view.View;

//public class MainActivity extends AppCompatActivity {

  //  @Override
    //protected void onCreate(Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
   // }

   // public void talkClick(View view) {

   // }
//}

//azinja male man

        //import android.support.v7.app.AppCompatActivity;
        import android.content.BroadcastReceiver;
        import android.widget.Button;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        //import android.support.v4.content.LocalBroadcastManager;
        import android.os.Bundle;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.widget.TextView;
        import android.os.VibrationEffect;
        import android.os.Vibrator;
        import android.view.View;

        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.localbroadcastmanager.content.LocalBroadcastManager;

        import com.google.android.gms.wearable.Node;
        import com.google.android.gms.tasks.Task;
        import com.google.android.gms.tasks.Tasks;
        import com.google.android.gms.wearable.Wearable;

        import java.util.List;
        import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button talkbutton;
    TextView textview;
    protected Handler myHandler;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkbutton = findViewById(R.id.talkButton);
        textview = findViewById(R.id.textView);
        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        //Create a message handler//

        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });

//Register to receive local broadcasts, which we'll be creating in the next step//

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    public void messageText(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            textview.append("\n" + newinfo);
        }
    }

//Define a nested class that extends BroadcastReceiver//

    public class Receiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

//Upon receiving each message from the wearable, display the following text//

            String message = "I just received a message from the wearable " + receivedMessageNumber++;
            ;

            textview.setText(message);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void talkClick(View v) {
        String message = "Sending message.... ";
        textview.setText(message);
        int dot = 200;      // Length of a Morse Code "dot" in milliseconds

        int dot1 = 300;     // Length of a Morse Code "dash" in milliseconds

        int dot2 = 400;

        int dot3 = 500;

        int dot4 = 600;

        int dot5 = 700;

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



//Sending a message can block the main UI thread, so use a new thread//

        new NewThread("/my_path", message).start();

    }

//Use a Bundle to encapsulate our message//

    public void sendmessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);

    }

    class NewThread extends Thread {
        String path;
        String message;

//Constructor for sending information to the Data Layer//

        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

//Retrieve the connected devices, known as nodes//

            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =

//Send the message//

                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

//Block on a task and get the result synchronously//

                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage("I just sent the wearable a message " + sentMessageNumber++);

                        //if the Task fails, then…..//

                    } catch (ExecutionException exception) {

                        //TO DO: Handle the exception//

                    } catch (InterruptedException exception) {

                        //TO DO: Handle the exception//

                    }

                }

            } catch (ExecutionException exception) {

                //TO DO: Handle the exception//

            } catch (InterruptedException exception) {

                //TO DO: Handle the exception//
            }

        }
    }
}
