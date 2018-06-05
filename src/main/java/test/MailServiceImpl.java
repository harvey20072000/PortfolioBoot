package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

	private final String USERNAME = "geosatai@gmail.com";
	private final String PASSWORD = "ai27285850";
	private final String HOST = "smtp.gmail.com";
	private final int PORT = 587;
	private Properties mailProps;
	private Map<String, List<String>> batchMap = new HashMap<>();
	
	JsonParser parser = new JsonParser();
	
	@PostConstruct
	private void init(){
		mailProps = new Properties();
		mailProps.put("mail.smtp.host", HOST);
		mailProps.put("mail.smtp.auth", "true");
		mailProps.put("mail.smtp.starttls.enable", "true");
		mailProps.put("mail.smtp.port", PORT);
	}
	
	public static void main(String[] argv) {
		
	}

	@Override
	public void sendMail(String recipients, String subject, String text, String level) throws Exception {
		String[] recipientArray = null;
		List<String> mailDetails = null;
		if(recipients != null && !"".equals(recipients)){
			if(subject == null || "".equals(subject)){
				subject = "系統預設通知信件";
			}
			recipientArray = recipients.split(",");
			for(String recipient : recipientArray){
				if ("batch".equals(level)) {
					if (batchMap.containsKey(recipient)) {
						mailDetails = batchMap.get(recipient);
						mailDetails.set(0, subject);
						mailDetails.set(1, mailDetails.get(1)+"\n\n"+text);
					} else {
						mailDetails = new LinkedList<>();
						mailDetails.add(subject);
						mailDetails.add(text);
					}
					batchMap.put(recipient, mailDetails);
				}else if ("instant".equals(level)) {
					executeSendMail(recipient, subject, text);
				}else {
					throw new Exception("undefined level："+level);
				}
			}
		}else {
			throw new Exception("undefined recipients");
		}
	}

	@Override
	public void sendMail(String recipients, String subject, String text) throws Exception {
		sendMail(recipients, subject, text, "batch");
	}

	@Override
	public void sendSelfMail(String subject, String text) throws Exception {
		sendMail(USERNAME, subject, text);
	}
	
	/*
	 * 定時送mail，並且清除batch
	 */
	@Scheduled(cron = "0 0 9,17,22 * * *")
	public void sendMail() {
		List<String> mailDetails;
		for(String recipient : batchMap.keySet()){
			mailDetails = batchMap.get(recipient);
			try {
				executeSendMail(recipient, mailDetails.get(0), mailDetails.get(1));
			} catch (Exception e) {
				log.error("sendMail to recipient({}) fail, exception => {}",recipient,e.toString());
				e.printStackTrace();
			}
		}
		batchMap = new HashMap<>();
	}

	// 執行送mail動作
	private void executeSendMail(String recipient, String subject, String text) throws Exception {
		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		};
		
		Session session = Session.getInstance(mailProps, authenticator);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(USERNAME));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setText(text);

		Transport transport = session.getTransport("smtp");
		transport.connect(HOST, PORT, USERNAME, PASSWORD);

		Transport.send(message);

		log.debug("executeSendMail[recipient：{}, subject：{}] done !");
	}
}
