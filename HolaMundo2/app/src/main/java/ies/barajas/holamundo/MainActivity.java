package ies.barajas.holamundo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

   // Button b;
    LinearLayout l;
    Button b;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        l= findViewById(R.id.fondo);
        b= findViewById(R.id.b_aceptar);
        tv= findViewById(R.id.tv_hola);

        //tv.setText("Adios");

    }//onCreate

    public void onPulsado(View view) {
        int id = view.getId();

        if(id==R.id.b_salir) {
            finish();
        }
        if(id== R.id.fondo){
            l.setBackgroundColor(Color.RED);
        }
        else if(id== R.id.tv_hola){
            tv.setBackgroundColor(Color.BLUE);
        }
        else miButtonToast(view);



    }//onPulsado

    public void miButtonToast(View view){
        Toast.makeText(this, ((Button)view).getText().toString(), Toast.LENGTH_LONG).show();
    }

   /* public void onPulsado(View view) {

        Button b= (Button) view;

        Toast.makeText(this, b.getText().toString(), Toast.LENGTH_LONG).show(); //hay que usar este, no se hardcodea
       //Toast.makeText(this, "Hola que tal", Toast.LENGTH_LONG).show();

    }*/


}//MainActivity