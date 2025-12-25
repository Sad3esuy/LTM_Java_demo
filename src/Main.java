import java.sql.SQLOutput;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int a, c;
        int tong = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số nguyên bất ký: ");
        a = scanner.nextInt();

        int stringlength = String.valueOf(a).length();
        //tính tổng của chứ số 8545604=32
        for (int i = 1; i <= stringlength; i++) {
            c = a % 10;
            tong = tong + c;
            a = a / 10;
        }
        System.out.println("Số chữ số là: " + tong);


    }
}