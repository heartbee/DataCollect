package sdk.apigw.jcloud.com.datasenderdemo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.chenqihong.queen.DataSender;
import com.github.chenqihong.queen.Queen;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Queen queen=Queen.getInstance();
        queen.init("10.12.137.132:8000",getApplication());
        queen.setUrl("http://10.12.137.132:8000/");
        queen.sendData();




    }
}
