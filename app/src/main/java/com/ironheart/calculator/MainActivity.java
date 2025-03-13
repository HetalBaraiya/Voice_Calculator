
package com.ironheart.calculator;
import com.ironheart.calculator.R;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextView textViewResult;
    private Button buttonSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);
        buttonSpeak = findViewById(R.id.buttonSpeak);

        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your calculation");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0).toLowerCase();
                textViewResult.setText("You said: " + spokenText);
                String expression = parseExpression(spokenText);
                if (expression != null) {
                    try {
                        double resultValue = evaluateExpression(expression);
                        textViewResult.setText(spokenText + " = " + resultValue);
                    } catch (Exception e) {
                        textViewResult.setText("Invalid expression!");
                    }
                } else {
                    textViewResult.setText("Could not understand the expression!");
                }
            }
        }
    }

    private String parseExpression(String spokenText) {
        // Replace spoken words with symbols
        spokenText = spokenText.replace("plus", "+")
                .replace("minus", "-")
                .replace("times", "*")
                .replace("into", "*")
                .replace("multiply by", "*")
                .replace("x", "*")
                .replace("divided by", "/")
                .replace("over", "/");

        // Regex to extract a basic math expression
//        Pattern pattern = Pattern.compile("([0-9]+\\s*[+\\-*/]\\s*[0-9]+)");
//        Matcher matcher = pattern.matcher(spokenText);
//        if (matcher.find()) {
//            return matcher.group(1).replaceAll("\\s+", "");
//        }
        if (!spokenText.isEmpty()) {
            return spokenText.replaceAll(" ", "");
        }
        return null;
    }

    private double evaluateExpression(String expression) {
        Log.d("expression", expression);

        // Replace words with symbols for arithmetic operations
        expression = expression.replace("plus", "+")
                .replace("minus", "-")
                .replace("times", "*")
                .replace("into", "*")
                .replace("multiply by", "*")
                .replace("x", "*")
                .replace("divided by", "/")
                .replace("over", "/");

        // 🛠 Step 1: Split numbers and operators
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char ch : expression.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);  // Build the number
            } else if ("+-*/".indexOf(ch) != -1) {
                numbers.add(Double.parseDouble(number.toString()));  // Add number to list
                operators.add(ch);  // Add operator to list
                number = new StringBuilder();  // Reset for the next number
            }
        }
        // Add the last number
        if (number.length() > 0) {
            numbers.add(Double.parseDouble(number.toString()));
        }

        // 🛠 Step 2: Evaluate * and / first
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '*' || operators.get(i) == '/') {
                double left = numbers.get(i);
                double right = numbers.get(i + 1);
                double result = (operators.get(i) == '*') ? left * right : left / right;

                // Replace the used numbers and operator
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;  // Stay on the same index as lists are reduced
            }
        }

        // 🛠 Step 3: Evaluate + and - next
        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '+') {
                result += numbers.get(i + 1);
            } else if (operators.get(i) == '-') {
                result -= numbers.get(i + 1);
            }
        }

        return result;
    }

//    private double evaluateExpression(String expression) {
//        Log.d("expression", expression);
//        // Basic evaluation logic
//        if (expression.contains("+")) {
//            Log.d("expression inside plus", "");
//            String[] parts = expression.split("\\+");
////            return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]) + Double.parseDouble(parts[2]);
//            double sum = 0;
//            for (String part : parts) {
//                sum += Double.parseDouble(part.trim());
//            }
//            return sum;
//        } else if (expression.contains("-")) {
//            Log.d("expression inside minus", "");
//            String[] parts = expression.split("-");
////            return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
//            double minus = Double.parseDouble(parts[0].trim());
//
//            for (int i = 1; i < parts.length; i++) {
//                minus -= Double.parseDouble(parts[i].trim());
//            }
//
//            return minus;
//        } else if (expression.contains("*")) {
//            String[] parts = expression.split("\\*");
////            return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
//            double into = 1;
//
//            for (String part : parts) {
//                into *= Double.parseDouble(part.trim());
//            }
//
//            return into;
//        } else if (expression.contains("/")) {
//            String[] parts = expression.split("\\/");
////            return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
//            double divide = Double.parseDouble(parts[0].trim());
//
//            for (int i = 1; i < parts.length; i++) {
//                divide /= Double.parseDouble(parts[i].trim());
//            }
//
//            return divide;
//        }
//        throw new IllegalArgumentException("Invalid expression");
//    }
}
