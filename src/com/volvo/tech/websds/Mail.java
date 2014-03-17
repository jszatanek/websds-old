package com.volvo.tech.websds;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Mail extends WebsdsApplication{

  public Mail()  {   }

  public static void sendMail( String subject, 
                               String messageText, 
                               String mailFromAddress) throws MessagingException {

          try
          {
                  String host = getStaticVariable("mailserver"); 
                  String recipients = getStaticVariable("mailToAddress"); 
            
                  Properties props = new Properties();
          
                  props.put("mail.smtp.host", host);
          
                  props.put("mail.transport.protocol", "smtp");
          
                  Session session = Session.getDefaultInstance(props, null);
          
                  session.setDebug(false);
          
                  Message message = new MimeMessage(session);
          
                  message.setFrom(new InternetAddress(mailFromAddress));
          
                  InternetAddress addressTo = new InternetAddress(recipients);
          
                  message.setRecipient(Message.RecipientType.TO, addressTo);
          
                  message.setSubject(subject);
          
                  message.setSentDate(new java.util.Date());
          
                  message.setText(messageText);
          
                  Transport.send(message);

        }
        catch (Exception e)
        {
                   throw new MessagingException(e.getMessage());
          
        }

    }
/*    
    public static void main (String args[]) 
    {

        try
        {
          Mail.sendMail("attila.peterffy@consultant.volvo.com","Header","Meddelande","mail.vtd.volvo.se","xxx@xx");
        }
        catch (MessagingException e)
        {
           e.printStackTrace();
        }

    }
*/  

}