package ies.barajas.holamundo;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   // Button b;
    LinearLayout l;
    Button b_aceptar, b_salir, b_hola;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l= findViewById(R.id.fondo);
        b_aceptar= findViewById(R.id.b_aceptar);
        b_hola= findViewById(R.id.b_saludar);
        b_salir= findViewById(R.id.b_salir);
        tv= findViewById(R.id.tv_hola);

        //tv.setText("Adios");

        b_aceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onPulsado(view);
                    }
                }
                /*
                Segunda forma de utilizar un evento, con una clase anónima
                usamos un método para instanciar un objeto en este caso de la clase onclicklistener que siempre
                implementa el metodo onClick
                 */
        );
        b_hola.setOnClickListener(this);
        l.setOnClickListener(this);
        tv.setOnClickListener(this);
        /*
        Tercera forma de utilizar un evento, implementando la interfaz onclicklistener al MainActivity, al igual que en
        en la segunda forma, se implementa además el método onClick
         */


    }//onCreate

    public void onClick(View view) {
        onPulsado(view);
    }
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