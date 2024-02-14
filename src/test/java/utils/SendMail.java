package utils;

import java.util.Arrays;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {
    static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

    public static String[] getAddress(String address) throws AddressException {
        String[] addressList = new String[0];

        if (address.isEmpty())
            return addressList;
        if (address.indexOf(";") > 0) {
            String[] addresses = address.split(";");

            for (String a : addresses) {
                InternetAddress.parse("addresses");
                addressList = append(addressList, a);
            }
        } else {
            addressList = append(addressList, address);
        }

        return addressList;
    }

    public static void ComposeGmail(String fromMail, String toMail, String groupReport) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        
        Session session = Session.getDefaultInstance(props);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromMail));

            for (String emailRecipient : getAddress(toMail)) {
                Address toAddress = new InternetAddress(emailRecipient);
                message.addRecipient(Message.RecipientType.TO, toAddress);
            }

            message.setSubject("Nimc Services Automated Test Result");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hello," + "\n" +"Attached to this mail is the report of an automated test on Nimc Services.  \nKindly download to view content. \n\nRegards, \nQuality Assurance");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();

            String file = System.getProperty("user.dir") + "/"+groupReport;
            String fileName = "Nimc-Service-Report.html";
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            System.out.println("Sending...");
            message.setContent(multipart);
			Transport.send(message, "seamfix.test.report@gmail.com", "@Bankole2!!!");
            System.out.println("Sent.");
        } catch (MessagingException ex) { 
            throw new RuntimeException(ex);
        }
    }

}
