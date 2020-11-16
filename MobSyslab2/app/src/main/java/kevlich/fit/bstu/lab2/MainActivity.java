package kevlich.fit.bstu.lab2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.ColorSpace;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    RadioButton genderM, genderW;
    EditText weight, height, age;
    Button lowLiveStyle, mediumLiveStyle, highLiveStyle, calculate;
    double LivestyleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lowLiveStyle = (Button) findViewById(R.id.btnLowLiveStyle);
        mediumLiveStyle = (Button) findViewById(R.id.btnMediumLiveStyle);
        highLiveStyle = (Button) findViewById(R.id.btnHighLiveStyle);

        lowLiveStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LivestyleType = 1.2;
                highLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightBlue));
                mediumLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightGreen));
                lowLiveStyle.setBackgroundColor(getResources().getColor(R.color.Tomato));
            }
        });
        mediumLiveStyle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LivestyleType = 1.55;
                highLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightBlue));
                mediumLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightSeaGreen));
                lowLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightTomato));
            }
        });
        highLiveStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LivestyleType = 1.9;
                highLiveStyle.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
                mediumLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightGreen));
                lowLiveStyle.setBackgroundColor(getResources().getColor(R.color.LightTomato));
            }
        });
        //result Method
        calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment ResultFragment = new ResultDialogFragment();
                Bundle args = new Bundle();
                double res = CalculateResult(GenderValue(), LivestyleType);
                args.putDouble("callories", res);
                ResultFragment.setArguments(args);
                ResultFragment.show(getSupportFragmentManager(), "result");
            }
        });
    }

    public int GenderValue() {
        //radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        genderM = (RadioButton) findViewById(R.id.rbGenderM);
        genderW = (RadioButton) findViewById(R.id.rbGenderW);
        if(genderM.isChecked()) { return 1; }
        else { return 0; }
    }

    public double CalculateResult(int genderValue, double livestyletype) {
        weight = (EditText) findViewById(R.id.textPersonWeight);
        height = (EditText) findViewById(R.id.textPersonHeight);
        age = (EditText) findViewById(R.id.textPersonAge);
        try{
        if(genderValue == 1) {
            return livestyletype * (66.4730
                    +(13.7516 * Double.parseDouble(weight.getText().toString()))
                    +(5.0033 * Double.parseDouble(height.getText().toString()))
                    +(6.7550 * Double.parseDouble(age.getText().toString())));
        }
        else {
            return livestyletype * (655.0955
                    +(9.5634 * Double.parseDouble(weight.getText().toString()))
                    +(1.8496 * Double.parseDouble(height.getText().toString()))
                    +(4.6756 * Double.parseDouble(age.getText().toString())));
        }
        }
        catch (Exception ex) { return 0; }
    }
}