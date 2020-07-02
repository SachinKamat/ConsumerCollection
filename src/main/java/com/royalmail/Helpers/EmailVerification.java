package com.royalmail.Helpers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EmailVerification {

    private boolean textIsHtml = false;
    Store store = null;
    Folder folderInbox = null;
    SearchTerm searchTerm = null;

    public boolean loginEmail(String userName, String password) {
        Properties properties = new Properties();
        boolean val = true;
        // server setting
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", 993);
        // SSL setting
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port", String.valueOf(993));
        Session session = Session.getDefaultInstance(properties);
        try {
            // connects to the message store
            store = session.getStore("imap");
            store.connect(userName, password);
            System.out.println("Connected to Email server….");
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
            val = false;
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
            val = false;
        }
        return val;
    }

    public String searchEmailSubject(String subjectKeyword, String folder, String fromEmail) throws IOException {
        boolean val = false;
        Integer mailCount = 0;
        try {
            processMail(folder);
            Message[] foundMessages = folderInbox.search(searchTerm);
            System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                System.out.println(message.getSubject());
                if (message.getSubject() == null) {
                    continue;
                }
                try {
                    if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail)) {
                        mailCount++;
                        String subject = message.getSubject();
                        System.out.println("Found message at " + i + 1 + ", Subject:" + subject);
                        System.out.println("From: " + email + ", " + message.getReceivedDate());
                        val = true;
                    } else {
                        val = false;
                    }
                } catch (NullPointerException expected) {
                    expected.printStackTrace();
                }
            }
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return mailCount.toString();
    }

    public String searchEmailContent(String subjectKeyword, String bodySearchText, String folder, String fromEmail) throws IOException {
        boolean val = false;
        Integer mailCount = 0;
        Integer mailCountSubject = 0;
        try {
            processMail(folder);
            // performs search through the folder
            Message[] foundMessages = folderInbox.search(searchTerm);
            //System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                if (message.getSubject() == null) {
                    continue;
                }
                try {
                    if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail)) {
                        String subject = message.getSubject();
                        mailCountSubject++;
                        //System.out.println("Body text: " + getText(message));
                        if (getText(message).contains(bodySearchText)) {
                            System.out.println("Found message at " + i + 1 + ", Subject:" + subject + "\nFrom: " + email + ", " + message.getReceivedDate());
                            if (getText(message).contains(bodySearchText) == true) {
                                mailCount++;
                                System.out.println("Message contains the search text: " + bodySearchText);
                                val = true;
                            } else {
                                val = false;
                            }
                        }
                    }
                } catch (NullPointerException expected) {
                    expected.printStackTrace();
                }
            }
            System.out.println("Total mail found with Subject " + subjectKeyword + " is " + mailCountSubject);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return mailCount.toString();
    }

    public String totalEmailDuringParticularTime(String subjectKeyword, String folder, String fromEmail, int time) throws IOException {
        boolean val = false;
        Integer mailCount = 0;
        try {
            processMail(folder);
            Message[] foundMessages = folderInbox.search(searchTerm);
            System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                if (message.getSubject() == null) {
                    continue;
                }
                Date date = new Date();//Getting Present date from the system
                String todayDate = getCurrentDateMonth(date);
                if (message.getReceivedDate().toString().contains(todayDate)) {
                    long diff = date.getTime() - message.getReceivedDate().getTime();//Get The difference between two dates
                    long diffMinutes = diff / (60 * 1000) % 60; //Fetching the difference of minute
                    //System.out.println("Difference in Minutes b/w present time & Email Recieved time :" + diffMinutes);
                    try {
                        if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail) && diffMinutes <= time * 60) {
                            mailCount++;
                            String subject = message.getSubject();
                            System.out.println("Found message at " + (i + 1) + ", Subject:" + subject + "\nFrom: " + email + ", " + message.getReceivedDate());
                            val = true;
                        } else {
                            val = false;
                        }
                    } catch (NullPointerException expected) {
                        expected.printStackTrace();
                    }
                }

            }
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return mailCount.toString();
    }

    public String totalEmailDuringParticularTime(String subjectKeyword, String bodySearchText, String folder, String fromEmail, int time) throws IOException {
        boolean val = false;
        Integer mailCount = 0;
        Integer mailCountSubject = 0;
        try {
            processMail(folder);
            // performs search through the folder
            Message[] foundMessages = folderInbox.search(searchTerm);
            //System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                if (message.getSubject() == null) {
                    continue;
                }
                Date date = new Date();//Getting Present date from the system
                String todayDate = getCurrentDateMonth(date);
                if (message.getReceivedDate().toString().contains(todayDate)) {
                    long diff = date.getTime() - message.getReceivedDate().getTime();//Get The difference between two dates
                    long diffMinutes = diff / (60 * 1000) % 60; //Fetching the difference of minute
                    try {
                        if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail) && diffMinutes <= time * 60) {
                            String subject = message.getSubject();
                            mailCountSubject++;
                            //System.out.println("Body text: " + getText(message));
                            if (getText(message).contains(bodySearchText)) {
                                System.out.println("Found message at " + i + 1 + ", Subject:" + subject + "\nFrom: " + email + ", " + message.getReceivedDate());
                                if (getText(message).contains(bodySearchText) == true) {
                                    mailCount++;
                                    System.out.println("Message contains the search text " + bodySearchText);
                                    val = true;
                                } else {
                                    val = false;
                                }
                            }
                        }
                    } catch (NullPointerException expected) {
                        expected.printStackTrace();
                    }
                }
            }
            System.out.println("Total mail found with Subject " + subjectKeyword + " is " + mailCountSubject);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return mailCount.toString();
    }

    public String searchTempPwdinMail(String subjectKeyword, String bodySearchText, String folder, String fromEmail) throws IOException {
        String temPwd = "";
        try {
            processMail(folder);
            // performs search through the folder
            Message[] foundMessages = folderInbox.search(searchTerm);
            //System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                if (message.getSubject() == null) {
                    continue;
                }
                try {
                    if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail)) {
                        //System.out.println("Body text: " + getText(message));
                        if (getText(message).contains(bodySearchText)) {
                            //System.out.println("Found message at " + i + 1 + ", Subject:" + subject + "\nFrom: " + email + ", " + message.getReceivedDate());
                            temPwd = getText(message).substring(getText(message).indexOf("Your new password is"));
                            temPwd = temPwd.substring(temPwd.indexOf(":") + 1, temPwd.indexOf("</span>")).trim();
                            //tempPassword = temPwd;
                        }
                    }
                } catch (NullPointerException expected) {
                    expected.printStackTrace();
                }
            }
            //System.out.println("temPwd = " + temPwd);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return temPwd;
    }

    public String searchTempPwdinMail(String subjectKeyword, String bodySearchText, String folder, String fromEmail, int time) throws IOException {
        boolean val = false;
        String temPwd = "";
        Integer mailCountSubject = 0;
        try {
            processMail(folder);
            // performs search through the folder
            Message[] foundMessages = folderInbox.search(searchTerm);
            //System.out.println("Total Messages Found :" + foundMessages.length);
            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                Address[] froms = message.getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                if (message.getSubject() == null) {
                    continue;
                }
                Date date = new Date();//Getting Present date from the system
                String todayDate = getCurrentDateMonth(date);
                if (message.getReceivedDate().toString().contains(todayDate)) {
                    long diff = date.getTime() - message.getReceivedDate().getTime();//Get The difference between two dates
                    long diffMinutes = diff / (60 * 1000) % 60; //Fetching the difference of minute
                    try {
                        if (message.getSubject().contains(subjectKeyword) && email.equals(fromEmail) && diffMinutes <= time * 60) {
                            String subject = message.getSubject();
                            mailCountSubject++;
                            //System.out.println("Body text: " + getText(message));
                            if (getText(message).contains(bodySearchText)) {
                                System.out.println("Found message at " + i + 1 + ", Subject:" + subject + "\nFrom: " + email + ", " + message.getReceivedDate());
                                temPwd = getText(message).substring(getText(message).indexOf("Your new password is"));
                                temPwd = temPwd.substring(temPwd.indexOf(":") + 1, temPwd.indexOf("</span>")).trim();
                                //tempPassword = temPwd;
                            }
                        }
                    } catch (NullPointerException expected) {
                        expected.printStackTrace();
                    }
                }
            }
            System.out.println("Total mail found with Subject " + subjectKeyword + " is " + mailCountSubject);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return temPwd;
    }

    private String processMail(String folderName) throws MessagingException, IOException {
        folderInbox = store.getFolder(folderName.toUpperCase());
        folderInbox.open(Folder.READ_ONLY);
        //create a search term for all “unseen” messages
        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
        //create a search term for all recent messages
        Flags recent = new Flags(Flags.Flag.RECENT);
        FlagTerm recentFlagTerm = new FlagTerm(recent, false);
        searchTerm = new OrTerm(unseenFlagTerm, recentFlagTerm);
        return null;
    }

    private String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    private String getCurrentDateMonth(Date date) {
        String todayDate;
        SimpleDateFormat formatNowDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatNowMonth = new SimpleDateFormat("MMM");
        String currentDay = formatNowDay.format(date);
        String currentMonth = formatNowMonth.format(date);
        todayDate = currentMonth + " " + currentDay;
        return todayDate;
    }
}