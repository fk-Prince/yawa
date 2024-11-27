package Product;

import Product.Entity.Payment;

import java.io.*;
import java.util.List;

public class PaymentRepository {

    public static File paymentFile = new File("payments.txt");

    public List<Payment> getPayments(String productChoice) throws IOException {
        if (!paymentFile.exists()) paymentFile.createNewFile();
        return new BufferedReader(new FileReader(paymentFile))
                .lines().map(lines -> lines.split(","))
                .filter(lines -> {
                    if (productChoice.equals("All")) return true;
                    return lines.length > 4 && lines[0].equalsIgnoreCase(productChoice);
                })
                .map(lines -> new Payment(Integer.parseInt(lines[1]), lines[2], Integer.parseInt(lines[3]), Double.parseDouble(lines[4])))
                .toList();
    }

    public void doPayment(Payment payment, String productType) throws IOException {
        if (!paymentFile.exists()) paymentFile.createNewFile();
        inventory(payment, productType);
        BufferedWriter br = new BufferedWriter(new FileWriter(paymentFile, true));
        br.write(productType + "," + payment.getId() + "," + payment.getBrandName() + "," + payment.getQty() + "," + payment.getPrice() + "\n");
        br.close();
    }

    private void inventory(Payment payment, String productType) throws IOException {
        File productFile = new File("products", productType + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(productFile));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            String[] lines = line.split(",");
            if (Integer.parseInt(lines[0]) == payment.getId()) {
                int newQty = Integer.parseInt(lines[2]) - payment.getQty();

                sb.append(payment.getId()).append(",")
                        .append(payment.getBrandName()).append(",")
                        .append(newQty).append(",")
                        .append(payment.getPrice()).append("\n");

            } else {
                sb.append(line).append("\n");
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(productFile));
        bw.write(sb.toString());
        bw.close();
        br.close();

    }

}
