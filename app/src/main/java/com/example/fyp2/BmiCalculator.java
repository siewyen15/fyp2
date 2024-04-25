package com.example.fyp2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BmiCalculator extends AppCompatActivity {

    EditText ediWeight, ediFeet, ediInch;
    Button buttonBmiCalculate;
    TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        ediWeight = findViewById(R.id.ediWeight);
        ediFeet = findViewById(R.id.ediFeet);
        buttonBmiCalculate = findViewById(R.id.buttonBmiCalculate);
        tvResult = findViewById(R.id.tvResult);

        buttonBmiCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sWeight = ediWeight.getText().toString();
                String sFeet = ediFeet.getText().toString();
                //String sInch = ediInch.getText().toString();

                float weight = Float.parseFloat(sWeight);
                float feet = Float.parseFloat(sFeet);
                //float inch = Float.parseFloat(sInch);

                float height = (float) (feet/100);
                float bmiIndex = weight/ (height*height);

                tvResult.setText("Your BMI Index is: " + bmiIndex+"\n\nBMI Categories:\n" +
                        "Underweight = <18.5\n" +
                        "Normal weight = 18.5–24.9\n" +
                        "Overweight = 25–29.9\n" +
                        "Obesity = BMI of 30 or greater ");

            }
        });
    }
}