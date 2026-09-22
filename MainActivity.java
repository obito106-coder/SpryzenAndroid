package com.spryzen.assistant;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.speech.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout chat; EditText input; SpeechRecognizer sr; TextToSpeech tts;
    TextView status;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        buildUI();
        tts = new TextToSpeech(this, x -> {});
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 7);
    }

    TextView tv(String s, int size) {
        TextView v=new TextView(this); v.setText(s); v.setTextColor(Color.WHITE); v.setTextSize(size);
        v.setPadding(18,12,18,12); return v;
    }

    void buildUI() {
        LinearLayout page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(18,18,18,12);
        page.setBackgroundColor(Color.rgb(5,7,13));

        TextView title=tv("SPRYZEN",28); title.setTextColor(Color.rgb(0,183,255));
        page.addView(title);
        status=tv("Your personal AI assistant",14); status.setTextColor(Color.LTGRAY); page.addView(status);

        ScrollView scroll=new ScrollView(this);
        chat=new LinearLayout(this); chat.setOrientation(LinearLayout.VERTICAL);
        chat.addView(tv("👋 Hi! I'm Spryzen. Tap 🎙️ and speak a command.",18));
        scroll.addView(chat);
        page.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL);
        input=new EditText(this); input.setHint("Type a message..."); input.setTextColor(Color.WHITE); input.setHintTextColor(Color.GRAY);
        bar.addView(input,new LinearLayout.LayoutParams(0,60,1));

        Button mic=new Button(this); mic.setText("🎙️"); mic.setOnClickListener(v->listen());
        bar.addView(mic,new LinearLayout.LayoutParams(80,60));

        Button send=new Button(this); send.setText("Send"); send.setOnClickListener(v->send());
        bar.addView(send,new LinearLayout.LayoutParams(100,60));
        page.addView(bar);
        setContentView(page);
    }

    void send() {
        String s=input.getText().toString().trim(); if(s.isEmpty()) return;
        add("You: "+s); input.setText("");
        String r="I heard: "+s+"\nConnect an AI provider to enable full AI answers.";
        add("Spryzen: "+r); speak(r);
    }

    void add(String s){ chat.addView(tv(s,17)); ((ScrollView)chat.getParent()).post(()->((ScrollView)chat.getParent()).fullScroll(View.FOCUS_DOWN)); }

    void listen() {
        if(sr!=null){ sr.destroy(); }
        sr=SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new RecognitionListener(){
            public void onResults(Bundle x){ ArrayList<String> a=x.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); if(a!=null&&!a.isEmpty()){input.setText(a.get(0));send();}}
            public void onReadyForSpeech(Bundle p){status.setText("Listening…");}
            public void onEndOfSpeech(){status.setText("Your personal AI assistant");}
            public void onError(int e){status.setText("Voice command not recognized");}
            public void onBeginningOfSpeech(){ }
            public void onRmsChanged(float r){ } public void onBufferReceived(byte[] b){ }
            public void onPartialResults(Bundle b){ } public void onEvent(int a,Bundle b){ }
        });
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-IN");
        sr.startListening(i);
    }

    void speak(String s){ if(tts!=null) tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"spryzen"); }
}