import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexd on 19.06.2017.
 */
public class ExpressionConverter {

    static ArrayList<String> strList = new ArrayList<>();
    static ArrayList<String> polish = new ArrayList<>();
    static HashMap<String, Integer> oper = new HashMap<>();

    static {
        oper.put("^", 3);
        oper.put("/", 2);
        oper.put("*", 2);
        oper.put("+", 1);
        oper.put("-", 1);
        oper.put("(", 0);
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //String input = br.readLine();
        String input = "( 5 + 11 ) * 2 - ( 3 - 13 ) * ( 2 + 1 )";
        Pattern p = Pattern.compile("\\d+(?:(?:\\.|,)\\d+)?|[/*\\-+^()]|[a-zA-Zа-яА-Я]+");
        Matcher m = p.matcher(input);
        while (m.find()) {
            strList.add(m.group());
        }

        if (checkExpression()) {
            stringToList();
            System.out.println(calculator());
        } else System.out.println("Скобки не согласованы!");

        for (String s : polish) {
            System.out.print(s + " ");
        }
    }

    static String calculator(){
        Stack<String> str = new Stack<>();
        String op1 = "";
        String op2 = "";
        double a, b;
        for (String s: polish) {
            if (Pattern.compile("-?\\d+(?:(?:\\.)\\d+)?").matcher(s).matches()) {
                str.push(s);
            }
            if (Pattern.compile("[a-zA-Zа-яА-Я]+").matcher(s).matches()) {
                str.push(s);
            }
            if (Pattern.compile("[/*\\-+^]").matcher(s).matches()) {
                op1 = str.pop();
                if (Pattern.compile("-?\\d+(?:(?:\\.)\\d+)?").matcher(op1).matches() &&
                        Pattern.compile("-?\\d+(?:(?:\\.)\\d+)?").matcher(str.peek()).matches()) {
                    op2 = str.pop();
                    b = Double.parseDouble(op1);
                    a = Double.parseDouble(op2);

                    switch (s) {
                        case "+": a = a + b;
                            break;
                        case "-": a = a - b;
                            break;
                        case "*": a = a * b;
                            break;
                        case "/": a = a / b;
                            break;
                        case "^": a = Math.pow(a, b);
                            break;
                    }
                    op1 = Double.toString(a);
                }
                str.push(op1);
            }
        }
        return str.pop();
    }

    static boolean checkExpression() {
        boolean res = true;
        int counter1 = 0;
        int counter2 = 0;
        Stack<String> parentheses = new Stack<>();
        for (String s: strList) {
            if (s.equals("(")) {
                parentheses.push(s);
            }
            if (s.equals(")")) {
                if (parentheses.empty() || !parentheses.pop().equals("(")) res = false;
            }
        }
        if (!parentheses.empty()) res = false;
        for (String s: strList) {
            if (Pattern.compile("\\d+(?:(?:\\.)\\d+)?|[a-zA-Zа-яА-Я]+").matcher(s).matches()) counter1++;
            if ((Pattern.compile("[/*\\-+^]").matcher(s).matches())) counter2++;
        }
        if (counter1 != counter2 + 1) res = false;
        return res;
    }

    static void stringToList() {

        Stack<String> stack = new Stack<>();

        for (String s : strList) {

            if (Pattern.compile("\\d+(?:(?:\\.|,)\\d+)?").matcher(s).matches()) {
                s = Pattern.compile(",").matcher(s).replaceFirst(".");
                polish.add(s);
            } else if (Pattern.compile("[a-zA-Zа-яА-Я]+").matcher(s).matches()) {
                polish.add(s);
            } else if (Pattern.compile("[/*\\-+^()]").matcher(s).matches()) {
                if (s.equals("(")) {
                    stack.push(s);
                }
                if (Pattern.compile("[/*\\-+^]").matcher(s).matches()) {
                    while (!stack.empty() && oper.get(stack.peek()) >= oper.get(s)) {
                        polish.add(stack.pop());
                    }
                    stack.push(s);
                }
                if (s.equals(")")) {
                    while (!stack.peek().equals("(")) {
                        if (!stack.empty()) {
                            polish.add(stack.pop());
                        }
                    }
                    if (stack.peek().equals("(")) stack.pop();
                }
            }
        }
        while (!stack.empty()) {
            polish.add(stack.pop());
        }
    }
}