package com.example.dovomish;

//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;

//public class MessageService extends Service {
//  public MessageService() {
//  }

//   @Override
//  public IBinder onBind(Intent intent) {
// TODO: Return the communication channel to the service.
//throw new UnsupportedOperationException("Not yet implemented");
//  }
//}

//azinja maleman
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

//Extend WearableListenerService//

public class MessageService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

//If the message’s path equals "/my_path"...//

        if (messageEvent.getPath().equals("/my_path")) {

//...retrieve the message//

            final String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);

//Broadcast the received Data Layer messages locally//

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

}
