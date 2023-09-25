package ies.barajas.holamundo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b= findViewById(R.id.b_aceptar);
        TextView tv= findViewById(R.id.tv_hola);

        //tv.setText("Adios");

    }//onCreate

    public void onPulsado(View view) {
        //Toast.makeText(this, R.string.ny_name, Toast.LENGTH_LONG).show(); //hay que usar este, no se hardcodea
       //Toast.makeText(this, "Hola que tal", Toast.LENGTH_LONG).show();
    }
}//MainActivity