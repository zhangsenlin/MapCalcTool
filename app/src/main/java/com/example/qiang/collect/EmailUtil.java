package com.example.qiang.collect;

/*public class EmailUtil {
    public static void sendEmail(String title,String content ,String datapath) {
        // 参数设置
        String serverAddr = "smtp.163.com";
        //String serverAddr = "smtp.qq.com";
        final String username = "18811346433"; // 163邮箱登录帐号
        final String pwd = "tx15906617106"; // 登录密码
        String nickName = "发件人";// 发送人昵称
        String sendAddr = username + "@163.com";// 发件人地址
        String reAddr = "654751637@qq.com";//这个是收件人

       // String datafilePath = Environment.getExternalStorageDirectory().getPath()+"/书美.txt";
        Log.i(datapath,"tttttttttttttttttttttttttt");
        // 首先，创建一个连接属性。
        Properties props = new Properties();
        props.put("mail.smtp.host", serverAddr); // 设置smtp的服务器地址是smtp.163.com
        props.put("mail.smtp.port", 25+"");//端口
        props.put("mail.smtp.auth", "true"); // 设置smtp服务器要身份验证。

        // 在创建一个身份验证。身份验证稍微复杂一点，要创建一个Authenticator的子类，并重载
        // getPasswordAuthentication（）方法,代码如下：
        class PopupAuthenticator extends Authenticator {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pwd);
            }
        }
        // 创建身份验证的实例:
        PopupAuthenticator auth = new PopupAuthenticator();
        // 创建会话: 关于会话的创建，有两种方法，具体请参看后续的文章,这里只简单使用一种。
        Session session = Session.getInstance(props, auth);
        try {
            // 定义邮件地址:
            // 发送人地址
            javax.mail.Address addressFrom = new InternetAddress(sendAddr, nickName);

            //接收人地址
            javax.mail.Address addressTo = new InternetAddress(reAddr, "mingqiang");

            // 抄送地址, 先把张帅的邮箱作为抄送地址
          //  javax.mail.Address csm = new InternetAddress("18538253565@126.com","zhangshuai");
            // 创建邮件体:
            MimeMessage message = new MimeMessage(session);
           // message.setContent(content, "text/plain");// 或者使用message.setText("Hello");更详细的信息请参看后续文章.
            message.setSubject(title);

            //第一部分（内容）
            BodyPart messageBodyPart1 =new MimeBodyPart();
            messageBodyPart1.setText(content);
            //第二部分（文件附件）
            File datafile=new File(datapath);

            BodyPart messageBodyPart2 = new MimeBodyPart();
            DataSource source = new FileDataSource(datafile);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(datafile.getName());
            Multipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(messageBodyPart1);
           // multipart.addBodyPart(messageBodyPart2);
            //防止文件名 中文乱码
            messageBodyPart2.setFileName(MimeUtility.encodeWord(datafile.getName()));
            multipart.addBodyPart(messageBodyPart2);
            //保存邮件内容。
            message.setContent(multipart);
            //=================================================
            message.setFrom(addressFrom);
            message.addRecipient(Message.RecipientType.TO, addressTo);
          //  message.addRecipient(Message.RecipientType.CC,csm);
            Log.i("33333333333","333333333333333");
            message.saveChanges();//存储邮件信息

            Log.i("44444444444444","4444444444444444");
            // 发送邮件的过程:
            Transport transport = session.getTransport("smtp");// 创建连接
            Log.i("555555555555555","555555555555555555");
            transport.connect(serverAddr, username, pwd);// 连接服务器
            Log.i("66666666666666666","666666666666666666");
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            transport.send(message);// 发送信息
           // transport.sendMessage(message,message.getAllRecipients());
            Log.i("777777777777777","777777777777777777777");
            transport.close();// 关闭连接
            Log.i("8888888888888","88888888888888888");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    */

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
     * 将shape文件以邮件形式发送至指定的邮箱地址
     *//*
    public static void sendContent(final String datafilePath){
        new Thread() {
            public void run() {
                String content = "你好，这是一个发送测量结果的数据文件";
                EmailUtil.sendEmail("测量结果文件", content,datafilePath);
            };
        }.start();
    }
}*/

public class EmailUtil {

    private static final int SUCCESS = 0 ;
    private static final int FAIL = 1 ;

    private static Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case SUCCESS :
                    if(msg.obj != null){
                        ((OnSendEmailListener)msg.obj).onSuccess();
                    }
                    break ;
                case FAIL :
                    if(msg.obj != null){
                        ((OnSendEmailListener)msg.obj).onFail();
                    }
                    break ;
            }
        }
    } ;

    public static boolean sendEmail(String title,String content ,String datapath) {
        // 参数设置
        String serverAddr = "smtp.163.com";
        //String serverAddr = "smtp.qq.com";
        final String username = "18811346433"; // 163邮箱登录帐号
        final String pwd = "tx15906617106"; // 登录密码
        String nickName = "发件人";// 发送人昵称
        String sendAddr = username + "@163.com";// 发件人地址
        String reAddr = "18538253565@126.com";//这个是收件人

        // String datafilePath = Environment.getExternalStorageDirectory().getPath()+"/书美.txt";
        // 首先，创建一个连接属性。
        Properties props = new Properties();
        props.put("mail.smtp.host", serverAddr); // 设置smtp的服务器地址是smtp.163.com
        props.put("mail.smtp.port", 25+"");//端口
        props.put("mail.smtp.auth", "true"); // 设置smtp服务器要身份验证。

        // 在创建一个身份验证。身份验证稍微复杂一点，要创建一个Authenticator的子类，并重载
        // getPasswordAuthentication（）方法,代码如下：
        class PopupAuthenticator extends Authenticator {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pwd);
            }
        }
        // 创建身份验证的实例:
        PopupAuthenticator auth = new PopupAuthenticator();
        // 创建会话: 关于会话的创建，有两种方法，具体请参看后续的文章,这里只简单使用一种。
        Session session = Session.getInstance(props, auth);
        try {
            // 定义邮件地址:
            // 发送人地址
            javax.mail.Address addressFrom = new InternetAddress(sendAddr, nickName);

            //接收人地址
            javax.mail.Address addressTo = new InternetAddress(reAddr, "zhangshuai");
            javax.mail.Address csm = new InternetAddress("654751637@qq.com","mingqiang");
            // 抄送地址, 先把张帅的邮箱作为抄送地址
            //  javax.mail.Address csm = new InternetAddress("18538253565@126.com","zhangshuai");
            // 创建邮件体:
            MimeMessage message = new MimeMessage(session);
            message.setSubject(title);

            //第一部分（内容）
            BodyPart messageBodyPart1 =new MimeBodyPart();
            messageBodyPart1.setText(content);
            //第二部分（文件附件）
            File datafile=new File(datapath);
            BodyPart messageBodyPart2 = new MimeBodyPart();
            DataSource source = new FileDataSource(datafile);

            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(datafile.getName());

            Multipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(messageBodyPart1);
            // multipart.addBodyPart(messageBodyPart2);
            //防止文件名 中文乱码
            messageBodyPart2.setFileName(MimeUtility.encodeWord(datafile.getName()));
            multipart.addBodyPart(messageBodyPart2);
            //保存邮件内容。
            message.setContent(multipart);
            //=================================================
            message.setFrom(addressFrom);
            message.addRecipient(Message.RecipientType.TO, addressTo);
            //抄送人
            message.addRecipient(Message.RecipientType.CC,csm);
            message.saveChanges();//存储邮件信息
            // 发送邮件的过程:
            Transport transport = session.getTransport("smtp");// 创建连接
            transport.connect(serverAddr, username, pwd);// 连接服务器

            //解决javax.activation.UnsupportedDataTypeException: no object DCH for MIME type 问题
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);

            //transport.send(message);// 发送信息
            //把所有发送完才结束
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();// 关闭连接
            return true ;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false ;
    }

    public static boolean sendEmailWithMultiBodyPart(String title,String content ,List<String> datapath, String receiver) {
        // 参数设置
        String serverAddr = "smtp.163.com";
        //String serverAddr = "smtp.qq.com";
        final String username = "18811346433"; // 163邮箱登录帐号
        final String pwd = "tx15906617106"; // 登录密码
        String nickName = "发件人";// 发送人昵称
        String sendAddr = username + "@163.com";// 发件人地址
        String reAddr = "18538253565@126.com";//这个是收件人

        // String datafilePath = Environment.getExternalStorageDirectory().getPath()+"/书美.txt";
        // 首先，创建一个连接属性。
        Properties props = new Properties();
        props.put("mail.smtp.host", serverAddr); // 设置smtp的服务器地址是smtp.163.com
        props.put("mail.smtp.port", 25+"");//端口
        props.put("mail.smtp.auth", "true"); // 设置smtp服务器要身份验证。

        // 在创建一个身份验证。身份验证稍微复杂一点，要创建一个Authenticator的子类，并重载
        // getPasswordAuthentication（）方法,代码如下：
        class PopupAuthenticator extends Authenticator {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pwd);
            }
        }
        // 创建身份验证的实例:
        PopupAuthenticator auth = new PopupAuthenticator();
        // 创建会话: 关于会话的创建，有两种方法，具体请参看后续的文章,这里只简单使用一种。
        Session session = Session.getInstance(props, auth);
        try {
            // 定义邮件地址:
            // 发送人地址
            javax.mail.Address addressFrom = new InternetAddress(sendAddr, nickName);

            //接收人地址
            javax.mail.Address addressTo = null ;
            if(TextUtils.isEmpty(receiver)){
                addressTo = new InternetAddress(reAddr, "zhangshuai");
            }else{
                addressTo = new InternetAddress(receiver, "zhangshuai");
            }

            javax.mail.Address csm = new InternetAddress("654751637@qq.com","mingqiang");
            // 抄送地址, 先把张帅的邮箱作为抄送地址
            //  javax.mail.Address csm = new InternetAddress("18538253565@126.com","zhangshuai");
            // 创建邮件体:
            MimeMessage message = new MimeMessage(session);
            message.setSubject(title);

            Multipart multipart = new MimeMultipart("mixed");
            //第一部分（内容）
            BodyPart messageBodyPart1 =new MimeBodyPart();
            messageBodyPart1.setText(content);
            multipart.addBodyPart(messageBodyPart1);
            //第二部分（文件附件）
            for (String path : datapath) {
                File datafile = new File(path);
                BodyPart messageBodyPart2 = new MimeBodyPart();
                DataSource source = new FileDataSource(datafile);

                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(datafile.getName());

                // multipart.addBodyPart(messageBodyPart2);
                //防止文件名 中文乱码
                messageBodyPart2.setFileName(MimeUtility.encodeWord(datafile.getName()));
                multipart.addBodyPart(messageBodyPart2);
            }
            //保存邮件内容。
            message.setContent(multipart);
            //=================================================
            message.setFrom(addressFrom);
            message.addRecipient(Message.RecipientType.TO, addressTo);
            //抄送人
            message.addRecipient(Message.RecipientType.CC,csm);
            message.saveChanges();//存储邮件信息
            // 发送邮件的过程:
            Transport transport = session.getTransport("smtp");// 创建连接
            transport.connect(serverAddr, username, pwd);// 连接服务器

            //解决javax.activation.UnsupportedDataTypeException: no object DCH for MIME type 问题
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);

            //transport.send(message);// 发送信息
            //把所有发送完才结束
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();// 关闭连接
            return true ;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false ;
    }
    /**
     * 将shape文件以邮件形式发送至指定的邮箱地址
     */
    public void sendContent(final OnSendEmailListener listener, final String datafilePath){
        new Thread() {
            public void run() {
                String content = "请查看附件";
                if(EmailUtil.sendEmail("位置点", content,datafilePath)){
                    mHandler.obtainMessage(SUCCESS, listener).sendToTarget() ;
                }else{
                    mHandler.obtainMessage(FAIL, listener).sendToTarget() ;
                }
            };
        }.start();
    }

    public void sendContentWithMulti(final OnSendEmailListener listener, final List<String> datafilePath, final String receiver){
        new Thread() {
            public void run() {
                String content = "请查看附件";
                if(EmailUtil.sendEmailWithMultiBodyPart("位置点", content, datafilePath, receiver)){
                    mHandler.obtainMessage(SUCCESS, listener).sendToTarget() ;
                }else{
                    mHandler.obtainMessage(FAIL, listener).sendToTarget() ;
                }
            };
        }.start();
    }

    private OnSendEmailListener mOnSendEmailListener ;

    public interface OnSendEmailListener {
        public void onSuccess() ;
        public void onFail() ;
    }
}

