package test;

public interface MailService {

	/**
	 * 發送email
	 * @param recipients 收件者，有多個則以","來分隔
	 * @param subject 標題
	 * @param text 寄件內容
	 * @param level 緊急程度，"batch"會將text內容以batch的方式發送，"instant"則會馬上發送
	 * @throws Exception
	 */
	void sendMail(String recipients, String subject, String text, String level) throws Exception;
	
	/**
	 * 以預設level(batch)發送email
	 * @param recipients 收件者，有多個則以","來分隔
	 * @param subject 標題
	 * @param text 寄件內容
	 * @throws Exception
	 */
	void sendMail(String recipients, String subject, String text) throws Exception;
	
	/**
	 * 寄通知信給自己
	 * @param subject 標題
	 * @param text 寄件內容
	 * @throws Exception
	 */
	void sendSelfMail(String subject, String text) throws Exception;
}
