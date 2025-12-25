import java.io.*;
import java.util.Scanner;

// --- LỚP 1: ĐẢM NHẬN VIỆC ĐỌC FILE ---
class FileReaderHandler {
    public String readContentFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file: " + e.getMessage());
            return null;
        }
        return content.toString();
    }
}

// --- LỚP 2: MÃ HÓA VÀ GHI FILE ---
class CipherAndWriter {
    // Hàm mã hóa theo chuẩn ROT13 (như trong hình: A <-> N)
    private String encryptROT13(String text) {
        StringBuilder result = new StringBuilder();
        int shift = 4; // key

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                // Công thức Caesar: (vị trí cũ + độ dịch) chia lấy dư cho 26
                char encryptedChar = (char) (base + (character - base + shift) % 26);
                result.append(encryptedChar);
            } else {
                // Giữ nguyên ký tự đặc biệt, số, khoảng trắng
                result.append(character);
            }
        }
        return result.toString();
    }

    // Hàm nhận dữ liệu, mã hóa và ghi ra file
    public void processAndWriteFile(String content, String outputPath) {
        String encryptedContent = encryptROT13(content);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write(encryptedContent);
            System.out.println("Đã mã hóa xong! Kiểm tra file: " + outputPath);
            System.out.println("Nội dung mã hóa: \n" + encryptedContent);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }
}


public class BT_4_2 {
    public static void main(String[] args) {
        // Đường dẫn file (Bạn nhớ tạo file input.txt trước nhé)
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        // Bước 1: Sử dụng Lớp 1 để đọc dữ liệu
        FileReaderHandler reader = new FileReaderHandler();
        String data = reader.readContentFromFile(inputFile);

        // Bước 2: Nếu đọc thành công, dùng Lớp 2 để mã hóa và ghi file
        if (data != null && !data.isEmpty()) {
            CipherAndWriter processor = new CipherAndWriter();
            processor.processAndWriteFile(data, outputFile);
        } else {
            System.out.println("File đầu vào rỗng hoặc không tồn tại.");

            // Tạo mẫu file input để test nếu chưa có
            createDummyInputFile(inputFile);
        }
    }

    // Hàm phụ trợ để tạo file mẫu cho bạn test nhanh
    private static void createDummyInputFile(String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write("HELLO");
            System.out.println("--- Đã tự động tạo file input.txt mẫu với nội dung: HELLO ---");
            System.out.println("--- Vui lòng chạy lại chương trình ---");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
