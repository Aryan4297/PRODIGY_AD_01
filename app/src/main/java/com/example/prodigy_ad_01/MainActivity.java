package com.example.prodigy_ad_01;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView solutionTv, resultTv;
    StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solutionTv = findViewById(R.id.solution_tv);
        resultTv = findViewById(R.id.result_tv);

        int[] buttonIds = new int[] {
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9, R.id.button_dot,
                R.id.button_plus, R.id.button_minus, R.id.button_multiply, R.id.button_divide,
                R.id.button_open_bracket, R.id.button_close_bracket,
                R.id.button_equals, R.id.button_ac, R.id.button_c
        };

        for (int id : buttonIds) {
            MaterialButton button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "AC":
                expression.setLength(0);
                resultTv.setText("0");
                solutionTv.setText("");
                break;

            case "C":
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    solutionTv.setText(expression.toString());
                }
                break;

            case "=":
                try {
                    double result = evaluateExpression(expression.toString());
                    resultTv.setText(String.valueOf(result));
                } catch (Exception e) {
                    resultTv.setText("Error");
                }
                break;

            default:
                expression.append(buttonText);
                solutionTv.setText(expression.toString());
        }
    }

    // Evaluate math expression using Shunting Yard + Postfix evaluation
    private double evaluateExpression(String expr) {
        List<String> postfix = toPostfix(expr);
        return evaluatePostfix(postfix);
    }

    private List<String> toPostfix(String expr) {
        Stack<String> operators = new Stack<>();
        List<String> output = new ArrayList<>();

        int i = 0;
        while (i < expr.length()) {
            char ch = expr.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    number.append(expr.charAt(i));
                    i++;
                }
                output.add(number.toString());
                continue;
            }

            // Handle unary minus for negative numbers
            if (ch == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(expr.charAt(i - 1)))) {
                i++; // skip '-'
                StringBuilder number = new StringBuilder("-");
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    number.append(expr.charAt(i));
                    i++;
                }
                output.add(number.toString());
                continue;
            }

            if (ch == '(') {
                operators.push("(");
            } else if (ch == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(String.valueOf(ch))) {
                    output.add(operators.pop());
                }
                operators.push(String.valueOf(ch));
            }

            i++;
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }


    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }
        return stack.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private int precedence(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        return 0;
    }
}
